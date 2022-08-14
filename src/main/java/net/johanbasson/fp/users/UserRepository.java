package net.johanbasson.fp.users;

import io.vavr.control.Option;
import io.vavr.control.Try;
import net.johanbasson.fp.core.types.Email;

public interface UserRepository {
    Try<Option<User>> findByEmail(Email email);
}
