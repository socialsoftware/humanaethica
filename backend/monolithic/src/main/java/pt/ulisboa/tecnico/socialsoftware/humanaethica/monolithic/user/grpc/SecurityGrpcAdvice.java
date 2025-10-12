package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.grpc;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;


@GrpcAdvice
public class SecurityGrpcAdvice {

    private static final Logger log = LoggerFactory.getLogger(SecurityGrpcAdvice.class);

    @GrpcExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public Status handleNoCredentials(AuthenticationCredentialsNotFoundException e) {
        return Status.UNAUTHENTICATED.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(BadCredentialsException.class)
    public Status handleBadCredentials(BadCredentialsException e) {
        return Status.UNAUTHENTICATED.withDescription("Bad credentials");
    }

    @GrpcExceptionHandler(AccessDeniedException.class)
    public Status handleAccessDenied(AccessDeniedException e) {
        return Status.PERMISSION_DENIED.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(Exception.class)
    public Status handleGeneric(Exception e) {
        log.error("[GrpcAdvice] Unhandled exception", e);
        return Status.INTERNAL.withDescription("Internal server error");
    }
}

