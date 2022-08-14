package net.johanbasson.fp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.johanbasson.fp.security.AuthenticateRequest;
import net.johanbasson.fp.security.JwtToken;
import okhttp3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class APITests {

    private static final Server server = new Server(9999);
    private final OkHttpClient client = new OkHttpClient();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @BeforeAll
    public static void init() {
        objectMapper.registerModule(new JavaTimeModule());
        server.start();
    }

    @AfterAll
    public static void shutdown() {
        server.stop();
    }

    @Test
    void shouldAuthenticate() throws IOException {
        String json = objectMapper.writeValueAsString(new AuthenticateRequest("admin@local.com", "admin"));
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("http://localhost:9999/api/v1/authenticate")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            JwtToken token = objectMapper.readValue(Objects.requireNonNull(response.body()).bytes(), JwtToken.class);
            assertThat(token).isNotNull();
            assertThat(token.token()).isNotBlank();
        }
    }
}
