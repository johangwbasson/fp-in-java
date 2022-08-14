package net.johanbasson.fp.users;

import io.vavr.control.Option;
import io.vavr.control.Try;
import net.johanbasson.fp.core.types.BCryptPassword;
import net.johanbasson.fp.core.types.Email;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import java.util.UUID;

public class JdbcUserRepository implements UserRepository {

    private final Sql2o sql2o;

    public JdbcUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Try<Option<User>> findByEmail(Email email) {
        return Try.of(() -> {
            try (Connection con = sql2o.open()) {
                try (Query query = con.createQuery("SELECT id, email, password, role FROM users WHERE email = :email")) {
                    query.addParameter("email", email.value());
                    return Option.of(query.executeAndFetchFirst(toUser()));
                }
            }
        });
    }

    private static ResultSetHandler<User> toUser() {
        return rs -> new User(
                UUID.fromString(rs.getString("id")),
                new Email(rs.getString("email")),
                new BCryptPassword(rs.getString("password")),
                Role.valueOf(rs.getString("role"))
        );
    }
}
