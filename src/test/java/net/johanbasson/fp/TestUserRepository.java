package net.johanbasson.fp;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;
import net.johanbasson.fp.core.types.BCryptPassword;
import net.johanbasson.fp.core.types.Email;
import net.johanbasson.fp.users.Role;
import net.johanbasson.fp.users.User;
import net.johanbasson.fp.users.UserRepository;

import java.util.UUID;

public class TestUserRepository implements UserRepository {
    private final Set<User> users = HashSet.of(
            new User(
                    UUID.randomUUID(),
                    new Email("admin@local.com"),
                    BCryptPassword.from("admin"),
                    Role.ADMINISTRATOR)
    );

    @Override
    public Try<Option<User>> findByEmail(Email email) {
        return Try.of(() -> users.find(user -> user.email().equals(email)));
    }
}
