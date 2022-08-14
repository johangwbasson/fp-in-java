package net.johanbasson.fp.security;

import io.javalin.http.Context;
import net.johanbasson.fp.core.ApiError;
import net.johanbasson.fp.infrastructure.Resources;
import org.eclipse.jetty.http.HttpStatus;

public final class SecurityController {

    private final Resources resources;

    public SecurityController(Resources resources) {
        this.resources = resources;
    }

    public void authenticate(Context ctx) {
        Authenticate.authenticate(getAuthenticateRequest(ctx))
                .apply(resources)
                .fold(
                        err -> badRequest(ctx, err),
                        token -> ok(ctx, token)
                );
    }

    private Void badRequest(Context ctx, ApiError apiError) {
        ctx.json(apiError.getErrors()).status(HttpStatus.BAD_REQUEST_400);
        return null;
    }

    private Void ok(Context ctx, Object result) {
        ctx.json(result).status(HttpStatus.OK_200);
        return null;
    }

    private AuthenticateRequest getAuthenticateRequest(Context ctx) {
        return ctx.bodyAsClass(AuthenticateRequest.class);
    }
}
