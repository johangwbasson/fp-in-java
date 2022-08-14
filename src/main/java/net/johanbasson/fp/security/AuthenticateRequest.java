package net.johanbasson.fp.security;

public record AuthenticateRequest(String email, String password) {
}
