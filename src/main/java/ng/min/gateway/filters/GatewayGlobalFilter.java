package ng.min.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
public class GatewayGlobalFilter extends AbstractGatewayFilterFactory<GatewayGlobalFilter.Config> implements GlobalFilter, Ordered {
	private static final int HTTPS_TO_HTTP_FILTER_ORDER = 10099;
	public GatewayGlobalFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest().mutate()
					.header("scgw-global-header", Math.random()*10+"")
//					.header("ClientId","deleogold@gmail.com")
					.build();
			return chain.filter(exchange.mutate().request(request).build());
		};
	}
	
	public static class Config {
		private String name;
		
		public String getName() {
			return this.name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		URI originalUri = exchange.getRequest().getURI();
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpRequest.Builder mutate = request.mutate();
		String forwardedUri = request.getURI().toString();
		if (forwardedUri != null && forwardedUri.startsWith("https")) {
			try {
				URI mutatedUri = new URI("http",
						originalUri.getUserInfo(),
						originalUri.getHost(),
						originalUri.getPort(),
						originalUri.getPath(),
						originalUri.getQuery(),
						originalUri.getFragment());
				mutate.uri(mutatedUri);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		ServerHttpRequest build = mutate.build();
		return chain.filter(exchange.mutate().request(build).build());
	}

	@Override
	public int getOrder() {
		return HTTPS_TO_HTTP_FILTER_ORDER;
	}
}