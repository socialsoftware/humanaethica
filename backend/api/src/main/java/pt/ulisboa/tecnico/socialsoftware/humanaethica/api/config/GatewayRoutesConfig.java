package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayRoutesConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // microservice authuser
                .route("authuser", r -> r
                        .path("/auth/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://authuser"))

                // microservice monolithic
                .route("monolithic", r -> r
                        .predicate(exchange -> true)
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://monolithic"))
                .build();
    }
}
