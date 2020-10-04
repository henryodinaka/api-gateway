package ng.min.gateway.filters;

import ng.min.gateway.utils.AES;
import ng.min.gateway.utils.CommonConstant;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Component
public class TokenPreFilter extends AbstractGatewayFilterFactory<TokenPreFilter.Config> {
    private static final Logger log = Logger.getLogger(TokenPreFilter.class.getName());

    public TokenPreFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("inside Token pre filter ");

        return (exchange, chain) -> {
            List<String> headers = exchange.getRequest().getHeaders().get("Authorization");
            log.info("Headers List " + headers);
            if (headers == null || headers.isEmpty()) {
                var serverHttpResponse = exchange.getResponse();
                serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                return chain.filter(exchange.mutate().response(serverHttpResponse).build());
            }
            var token = headers.stream().findFirst().get();
            log.info("Headers passed " + token);
            if (!token.startsWith(CommonConstant.TOKEN_PREFIX)) {

                log.info("Checking prefix " + token);
                var serverHttpResponse = exchange.getResponse();
                serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                return chain.filter(exchange.mutate().response(serverHttpResponse).build());
            }

            String[] authTokenArray = token.split("\\s+");

            if (!(authTokenArray.length == 2)) {
                log.info("Checking the length " + Arrays.toString(authTokenArray));
                var serverHttpResponse = exchange.getResponse();
                serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                return chain.filter(exchange.mutate().response(serverHttpResponse).build());
            }

            var authTokenEncrypted = authTokenArray[1]; /*Decrypt token*/
            log.info("authTokenEncrypted from header =======:::" + authTokenEncrypted);

            /*Decrypt with frontend Key*/
            var decryptedToken = AES.decrypt(authTokenEncrypted, true);
            log.info("decryptedToken from header =====::: " + decryptedToken);
            if (decryptedToken == null) {
                log.info("Token decrypted is null");
                var serverHttpResponse = exchange.getResponse();
                serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                return chain.filter(exchange.mutate().response(serverHttpResponse).build());
            }
            /*Encrypt with backend Key*/
            var encryptedToken = AES.encrypt(decryptedToken, false);
            log.info("encrypted token with backend key =====::: " + encryptedToken);

            ServerHttpRequest request = exchange.getRequest().mutate().header(CommonConstant.HEADER_STRING, CommonConstant.TOKEN_PREFIX+" "+encryptedToken).build();
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
}
