package org.addycaddy.exception;

public class DuplicateContactPointException extends AddyCaddyException {
    public DuplicateContactPointException(String message) {
        super(message);
    }

    public DuplicateContactPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateContactPointException(Throwable cause) {
        super(cause);
    }

    public DuplicateContactPointException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
