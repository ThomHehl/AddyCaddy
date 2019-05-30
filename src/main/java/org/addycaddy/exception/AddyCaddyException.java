package org.addycaddy.exception;

public class AddyCaddyException extends Exception {
    public AddyCaddyException(String message) {
        super(message);
    }

    public AddyCaddyException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddyCaddyException(Throwable cause) {
        super(cause);
    }

    public AddyCaddyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
