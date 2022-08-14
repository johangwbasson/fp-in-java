package net.johanbasson.fp.security;

import io.vavr.control.Either;
import net.johanbasson.fp.TestResourcesFactory;
import net.johanbasson.fp.core.ApiError;
import net.johanbasson.fp.infrastructure.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticateTest {

    private static final Resources resources = TestResourcesFactory.create();

    @Test
    void shouldAuthenticateWithCorrectCredentials() {
        // Given
        AuthenticateRequest cmd = new AuthenticateRequest("admin@local.com", "admin");

        // When
        Either<ApiError, JwtToken> result = Authenticate.authenticate(cmd).apply(resources);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isNotNull();
        assertThat(result.get().token()).isNotNull().hasSizeGreaterThan(200);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForAuthenticateFailures")
    void shouldReturnError(AuthenticateRequest cmd, String errorMessage) {
        Either<ApiError, JwtToken> result = Authenticate.authenticate(cmd).apply(resources);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isLeft()).isTrue();
        assertThat(result.swap().get()).isNotNull();
        assertThat(result.swap().get().toString()).hasToString(errorMessage);
    }

    private static Stream<Arguments> provideArgumentsForAuthenticateFailures() {
        return Stream.of(
                Arguments.of(new AuthenticateRequest("", "Pass1234"), "Invalid email specified"),
                Arguments.of(new AuthenticateRequest("admin@local.com", ""), "Password cannot be empty"),
                Arguments.of(new AuthenticateRequest("admin@abc.com", "Pass1234"), "User not found"),
                Arguments.of(new AuthenticateRequest("a@a.com", "Pass1234"), "User not found"),
                Arguments.of(new AuthenticateRequest("admin@local.com", "password"), "Incorrect credentials")
        );
    }

}