package net.johanbasson.fp.users;

import net.johanbasson.fp.core.types.BCryptPassword;
import net.johanbasson.fp.core.types.Email;

import java.util.UUID;

public record User(UUID id, Email email, BCryptPassword password, Role role) {
}
