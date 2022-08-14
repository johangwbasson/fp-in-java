package net.johanbasson.fp.core.types;

public record PlainTextPassword(String value) {

    @Override
    public String toString() {
        return value;
    }
}
