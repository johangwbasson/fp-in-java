package net.johanbasson.fp.security;

import java.time.ZonedDateTime;

public record JwtToken(String token, ZonedDateTime expires) {
}
