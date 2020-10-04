package ng.min.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import ng.min.gateway.dto.Response;
import ng.min.gateway.dto.ResponseData;
import ng.min.gateway.utils.JsonUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
public class TokenPostFilter extends AbstractGatewayFilterFactory<TokenPostFilter.Config> {
	private static final Logger log = Logger.getLogger(TokenPreFilter.class.getName());
	public TokenPostFilter() {
		super(TokenPostFilter.Config.class);
	}

	@Override
	public GatewayFilter apply(TokenPostFilter.Config config) {
		log.info("inside SCGWPostFilter.apply method...");

		return(exchange, chain)->{

			ServerHttpResponse response = exchange.getResponse();
			DataBufferFactory dataBufferFactory = response.bufferFactory();
			ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
				@Override
				public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
					if (!response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) {

						return chain.filter(exchange);
					}

					if (!(body instanceof Flux)) {
						return super.writeWith(body);
					}

					Flux<? extends DataBuffer> flux = (Flux<? extends DataBuffer>) body;
					return super.writeWith(flux.buffer().map(dataBuffers -> {
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						dataBuffers.forEach(buffer -> {
							// three byte copies here
							byte[] array = new byte[buffer.readableByteCount()];
							buffer.read(array);
							try {
								outputStream.write(array);
							} catch (IOException e) {
								// TODO: need catch?
							}
							DataBufferUtils.release(buffer);
						});

						Response gatewayResult = new Response();
						try {
							Response apiResult = JsonUtils.read(outputStream.toByteArray());
							log.info("Api result "+apiResult);
							var data = apiResult.getData();
							log.info("Result data "+data);
							gatewayResult.setCode(apiResult.getCode());
							gatewayResult.setMsg(apiResult.getMsg());
							gatewayResult.setData(new ResponseData(data.getToken()));
							gatewayResult.setData(data);
						} catch (IOException e) {
							gatewayResult.setCode("503");
							gatewayResult.setMsg("json error:" + e.getMessage());
						}

						try {
							byte[] write = JsonUtils.write(gatewayResult);
							/**
							 * https://github.com/spring-cloud/spring-cloud-gateway/issues/113
							 * You need to modify the header Content-Length, If you don't do this,
							 * the body may be truncated after you have modify the request body
							 * and the body becomes longer.
							 */
							response.getHeaders().setContentLength(write.length);
							return dataBufferFactory.wrap(write);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
							return null;
						}

					}));
				}
			};

			ServerWebExchange serverWebExchange = exchange.mutate().response(decoratedResponse).build();
			return chain.filter(serverWebExchange);
//			return chain.filter(exchange).then(Mono.fromRunnable(()->{
//
//
//			}));
		};

	}

    public static class Config {

        public Config() {
        }

    }

//	@Bean
//	public RouteLocator routes(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route("rewrite_request_obj", r -> r.host("*.rewriterequestobj.org")
//						.filters(f -> f.prefixPath("/httpbin")
//								.modifyRequestBody(String.class, Response.class, MediaType.APPLICATION_JSON_VALUE,
//										(exchange, s) -> return Mono.just(new Response(s.toUpperCase())))).uri(uri))
//        .build();
//	}
}

/*{
    "code": "00",
    "msg": " Successful",
    "data": {
        "token": "Q9sZSLJVDqUthrEHedYlTfNTQpMJnK8OXJbd7Mvn9grDtNaUCW6jI/9m1VR326R9GlM1tzwfhlOQFTBEz8MKspcYKb1TfZ+wUulkRFcmE0IIA5hdrPo4WnyJ29+bE6TXe20UDq8L3yvKU3lmqlNTd8u7+KxeQ/0qMhHbzSNuhRfhOqHZ3k4ZkdEyikl8Kg58HZtGx3KIIKZd9PaPs1TzzQbJ1Kx+ggxWb75iu+e6SnfxtVBan4fBi+7/jMgeKp+u9PTv8Dofly0gcAgLs0hdEJMHutSg5QaExhCoCIRQxbKQB1Wn97E0Gp4n8cMP1TKZCWXNJuRV4AP4wQ9B+BD6J5i7drX3xTJiHEjPuljvm7Mq2l5ZieBwPTCqrq6IsmW+GfpGmD88zO02eUnwsKKNnF0r89EgBNYHLj68blTK3uWEyTvz8A0FgQ4YG81yN88loyO+HLJEdFA2OU4tf28aN4SgSztCxkYa2RInQ5bsxPxQz2f4qu6lytpxGYzG7xcfKZZ2EF9QyCvwm/1llak4/fbJDH9rjTcGxL3v2llONsVP9w/jMRWRFB3UNr5TgHkTVex3DiU82+jg5MuVhkpDnPwuPCrnbhvlX+SarrLjcBo=",
        "expiration": 1601746868435
    }
}*/