package net.johanbasson.fp.core;

import io.vavr.control.Validation;
import net.johanbasson.fp.core.types.Email;
import net.johanbasson.fp.core.types.PlainTextPassword;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

public final class Validations {

    private static final EmailValidator emailValidator = EmailValidator.getInstance(true);

    private Validations() {}

    public static Validation<ApiError, Email> email(String email) {
        if (emailValidator.isValid(email)) {
            return Validation.valid(new Email(email));
        } else {
            return Validation.invalid(ApiError.validationError("Invalid email specified"));
        }
    }

    public static Validation<ApiError, PlainTextPassword> password(String password) {
        if (StringUtils.isBlank(password)) {
            return Validation.invalid(ApiError.validationError("Password cannot be empty"));
        } else {
            return Validation.valid(new PlainTextPassword(password));
        }
    }
}
