package net.johanbasson.fp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.johanbasson.fp.infrastructure.Repositories;
import net.johanbasson.fp.infrastructure.Resources;
import net.johanbasson.fp.infrastructure.Security;
import net.johanbasson.fp.security.SecurityController;
import net.johanbasson.fp.users.JdbcUserRepository;
import org.flywaydb.core.Flyway;
import org.sql2o.Sql2o;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public final class Server {

    private final int port;
    private final Javalin app;

    public Server(int port) {
        this.port = port;
        var resources = initializeResources();
        var securityController = new SecurityController(resources);

        app = Javalin.create(cfg -> {
            cfg.enableDevLogging();
            cfg.enableCorsForAllOrigins();
        });

        app.routes(() -> {
            path("/api/v1", () -> {
                post("/authenticate", securityController::authenticate);
            });
        });
    }

    public void start() {
        app.start(port);
    }

    public void stop() {
        app.stop();
    }

    private static Resources initializeResources() {
        var hikariConfig = new HikariConfig();
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setJdbcUrl("jdbc:h2:mem:db");
        var hikariDataSource = new HikariDataSource(hikariConfig);
        Flyway.configure().dataSource(hikariDataSource).load().migrate();
        var sql2o = new Sql2o(hikariDataSource);
        var security = new Security(Keys.secretKeyFor(SignatureAlgorithm.HS512));
        var repositories = new Repositories(new JdbcUserRepository(sql2o));
        return new Resources(repositories, security);
    }

}
