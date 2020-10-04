//package ng.min.gateway.filters;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import ng.min.gateway.dto.Response;
//import ng.min.gateway.dto.ResponseData;
//import ng.min.gateway.utils.JsonUtils;
//import org.reactivestreams.Publisher;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.nio.charset.Charset;
//
///**
// * @author chenkunyun
// * @date 2019-05-02
// */
//@Component
//public class ModifyResponseBodyFilter implements GlobalFilter, Ordered {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpResponse response = exchange.getResponse();
//        DataBufferFactory dataBufferFactory = response.bufferFactory();
//        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
//            @Override
//            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                if (!response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) {
//
//                    return chain.filter(exchange);
//                }
//
//                if (!(body instanceof Flux)) {
//                    return super.writeWith(body);
//                }
//
//                Flux<? extends DataBuffer> flux = (Flux<? extends DataBuffer>) body;
//                return super.writeWith(flux.buffer().map(dataBuffers -> {
//                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                    dataBuffers.forEach(buffer -> {
//                        // three byte copies here
//                        byte[] array = new byte[buffer.readableByteCount()];
//                        buffer.read(array);
//                        try {
//                            outputStream.write(array);
//                        } catch (IOException e) {
//                            // TODO: need catch?
//                        }
//                        DataBufferUtils.release(buffer);
//                    });
//
//                    Response gatewayResult = new Response();
//                    try {
//                        Response apiResult = JsonUtils.read(outputStream.toByteArray());
//                        System.out.println("Api result "+apiResult);
//                        var data = apiResult.getData();
//                        System.out.println("Result data "+data);
//                        gatewayResult.setCode(apiResult.getCode());
//                        gatewayResult.setMsg(apiResult.getMsg());
//                        gatewayResult.setData(new ResponseData(data.getToken(),data.getTokenOld(),data.getExpiration()));
//                        gatewayResult.setData(data);
//                    } catch (IOException e) {
//                        gatewayResult.setCode("503");
//                        gatewayResult.setMsg("json error:" + e.getMessage());
//                    }
//
//                    try {
//                        byte[] write = JsonUtils.write(gatewayResult);
//                        /**
//                         * https://github.com/spring-cloud/spring-cloud-gateway/issues/113
//                         * You need to modify the header Content-Length, If you don't do this,
//                         * the body may be truncated after you have modify the request body
//                         * and the body becomes longer.
//                         */
//                        response.getHeaders().setContentLength(write.length);
//                        return dataBufferFactory.wrap(write);
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//
//                }));
//            }
//        };
//
//        ServerWebExchange serverWebExchange = exchange.mutate().response(decoratedResponse).build();
//        return chain.filter(serverWebExchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
//    }
//}