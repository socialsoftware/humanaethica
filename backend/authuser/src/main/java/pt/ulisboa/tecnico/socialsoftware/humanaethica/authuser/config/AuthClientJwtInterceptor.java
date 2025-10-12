package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.config;

import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;



@Component
@GrpcGlobalClientInterceptor
public class AuthClientJwtInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                // Opção A: o token está em Authentication.getCredentials()
                if (auth != null && auth.getCredentials() instanceof String token && !token.isBlank()) {
                    headers.put(AUTHORIZATION, "Bearer " + token);
                }

                super.start(responseListener, headers);
            }
        };
    }
}