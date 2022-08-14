package net.johanbasson.fp.core.types;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mindrot.jbcrypt.BCrypt;

public final class BCryptPassword {

    private final String encoded;

    public BCryptPassword(String encoded) {
        this.encoded = encoded;
    }

    @Override
    public String toString() {
        return encoded;
    }

    public static BCryptPassword from(String plain) {
        return new BCryptPassword(BCrypt.hashpw(plain, BCrypt.gensalt()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BCryptPassword that = (BCryptPassword) o;

        return new EqualsBuilder().append(encoded, that.encoded).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(encoded).toHashCode();
    }

    public boolean matches(PlainTextPassword password) {
        return BCrypt.checkpw(password.value(), encoded);
    }
}
