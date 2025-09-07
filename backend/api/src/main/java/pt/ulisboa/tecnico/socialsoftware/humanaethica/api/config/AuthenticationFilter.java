package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator routerValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (this.isAuthMissing(request))
                return this.onError(exchange, "Authorization header is missing in request");

            final String token = getToken(request);

            if (ApiGatewaySecurityUtils.isTokenExpired(token))
                return this.onError(exchange, "Authorization header is invalid");
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }

    public static String getToken(ServerHttpRequest req) {
        String authHeader = req.getHeaders().getOrEmpty("Authorization").get(0);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else if (authHeader != null && authHeader.startsWith("AUTH")) {
            return authHeader.substring(4);
        } else if (authHeader != null) {
            return authHeader;
        }
        return "";
    }


    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }
}
