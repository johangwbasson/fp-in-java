package net.johanbasson.fp;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.johanbasson.fp.infrastructure.Repositories;
import net.johanbasson.fp.infrastructure.Resources;
import net.johanbasson.fp.infrastructure.Security;

import javax.crypto.SecretKey;

public class TestResourcesFactory {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public static Resources create() {
        var userRepository = new TestUserRepository();
        var repositories = new Repositories(userRepository);
        var security = new Security(SECRET_KEY);
        return new Resources(repositories, security);
    }
}
