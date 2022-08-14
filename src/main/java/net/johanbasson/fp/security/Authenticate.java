package net.johanbasson.fp.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vavr.control.Either;
import io.vavr.control.Validation;
import net.johanbasson.fp.core.ApiError;
import net.johanbasson.fp.core.Reader;
import net.johanbasson.fp.core.Validations;
import net.johanbasson.fp.core.types.Email;
import net.johanbasson.fp.core.types.PlainTextPassword;
import net.johanbasson.fp.infrastructure.Resources;
import net.johanbasson.fp.users.User;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static net.johanbasson.fp.Constants.ROLE;

public final class Authenticate {

    private Authenticate() {
    }

    public static Reader<Resources, Either<ApiError, JwtToken>> authenticate(AuthenticateRequest request) {
        return Reader.of(ctx ->
                validate(request)
                        .flatMap(findUser(ctx))
                        .flatMap(Authenticate::checkPassword)
                        .flatMap(ar -> right(generateToken(ar.user).apply(ctx))));
    }

    private static Either<ApiError, ValidAuthenticateRequest> validate(AuthenticateRequest cmd) {
        Validation<ApiError, Email> email = Validations.email(cmd.email());
        Validation<ApiError, PlainTextPassword> password = Validations.password(cmd.password());
        return Validation.combine(email, password)
                .ap(ValidAuthenticateRequest::new)
                .toEither()
                .mapLeft(ApiError::validationErrors);
    }

    private static Function<ValidAuthenticateRequest, Either<ApiError, UserAndRequest>> findUser(Resources ctx) {
        return validAuthenticateRequest -> ctx.repositories().userRepository()
                .findByEmail(validAuthenticateRequest.email())
                .toEither()
                .mapLeft(ApiError::databaseError)
                .flatMap(maybeUser -> maybeUser.toEither(ApiError.error("User not found")))
                .map(user -> new UserAndRequest(user, validAuthenticateRequest));
    }

    private static Either<ApiError, UserAndRequest> checkPassword(UserAndRequest t) {
        if (t.user().password().matches(t.request().plainTextPassword())) {
            return right(t);
        } else {
            return left(ApiError.error("Incorrect credentials"));
        }
    }

    public static Reader<Resources, JwtToken> generateToken(User user) {
        return Reader.of(ctx -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expires = now.plus(4, ChronoUnit.HOURS);
            String token = Jwts.builder()
                    .setSubject(user.id().toString())
                    .claim(ROLE, user.role())
                    .setExpiration(Date.from(expires.toInstant(ZoneOffset.UTC)))
                    .signWith(ctx.security().secretKey(), SignatureAlgorithm.HS512)
                    .compact();
            return new JwtToken(token, expires.atZone(ZoneId.systemDefault()));
        });
    }

    private record ValidAuthenticateRequest(Email email, PlainTextPassword plainTextPassword) {}
    private record UserAndRequest(User user, ValidAuthenticateRequest request) {}
}
