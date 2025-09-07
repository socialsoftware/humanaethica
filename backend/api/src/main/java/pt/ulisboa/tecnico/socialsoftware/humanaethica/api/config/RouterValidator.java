package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> openEndpoints = List.of(
            "/auth/**",
            "/users/registerVolunteer",
            "/users/register/confirm",
            "/images/**",
            "/resources/**"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openEndpoints
                    .stream()
                    .noneMatch(pattern -> pathMatcher.match(pattern, request.getURI().getPath()));
}

