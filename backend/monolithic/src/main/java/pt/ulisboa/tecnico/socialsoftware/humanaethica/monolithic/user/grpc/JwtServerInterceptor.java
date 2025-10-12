package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.grpc;


import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security.JwtUtil;

@Component
@GrpcGlobalServerInterceptor
public class JwtServerInterceptor implements ServerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(JwtServerInterceptor.class);

    private static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final JwtUtil jwtUtil;

    public JwtServerInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        var ctx = SecurityContextHolder.createEmptyContext();

        try {
            String authHeader = headers.get(AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.length() > 7 ? authHeader.substring(7) : null;

                if (token != null && !token.isBlank()) {
                    try {
                        Authentication auth = jwtUtil.getAuthentication(token);
                        ctx.setAuthentication(auth);
                    } catch (Exception e) {
                        log.warn("[JwtServerInterceptor] Invalid JWT ignored: {}", e.getMessage());
                    }
                }
            }

            SecurityContextHolder.setContext(ctx);
            return next.startCall(call, headers);

        } catch (Exception ex) {
            log.error("[JwtServerInterceptor] Unexpected auth error", ex);
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid Auth Header").withCause(ex), new Metadata());
            return new ServerCall.Listener<>() {};
        }
    }
}