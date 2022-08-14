package net.johanbasson.fp.core;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ApiError {

    Set<String> getErrors();

    Logger logger = LoggerFactory.getLogger(ApiError.class);

    static ValidationError validationError(String message) {
        return new ValidationError(message);
    }

    static ValidationErrors validationErrors(Seq<ApiError> errors) {
        return new ValidationErrors(errors);
    }

    static ApiError databaseError(Throwable th) {
        logger.error("Database Error", th);
        return new ThrowableError(th);
    }

    static ApiError error(String message) {
        return new GeneralError(message);
    }

    class ThrowableError implements ApiError {
        private final String message;

        public ThrowableError(Throwable throwable) {
            message = throwable.getMessage();
        }

        @Override
        public String toString() {
            return message;
        }

        @Override
        public Set<String> getErrors() {
            return HashSet.of(message);
        }
    }

    class GeneralError implements ApiError {
        private final String message;

        public GeneralError(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }

        @Override
        public Set<String> getErrors() {
            return HashSet.of(message);
        }
    }

    class ValidationError implements ApiError {
        private final String message;

        public ValidationError(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }

        @Override
        public Set<String> getErrors() {
            return HashSet.of(message);
        }
    }

    class ValidationErrors implements ApiError {
        private final Set<String> messages;

        public ValidationErrors(Seq<ApiError> errors) {
            List<String> items = errors.map(Object::toString).toList();
            messages = HashSet.ofAll(items.toStream());
        }

        @Override
        public String toString() {
            return StringUtils.join(messages.toArray(), ",");
        }

        @Override
        public Set<String> getErrors() {
            return messages;
        }
    }
}
