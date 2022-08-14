CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(128) NOT NULL
);

INSERT INTO users (id, email, password, role)
VALUES ('6066bc35-8700-4b11-ac09-4869ab255b89', 'admin@local.com', '$2a$10$/BDjGbWbOCKSgOPN.brlIOYlmKnoEhcly4GIXv52XcstKIdVvypnm', 'ADMINISTRATOR');