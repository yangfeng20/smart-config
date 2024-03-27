package com.maple.smart.config.core.exp;

/**
 * @author maple
 * @since 2023/12/20 21:42
 * Description:
 */

public class SmartConfigApplicationException extends RuntimeException{

    public SmartConfigApplicationException() {
    }

    public SmartConfigApplicationException(String message) {
        super(message);
    }

    public SmartConfigApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmartConfigApplicationException(Throwable cause) {
        super(cause);
    }

    public SmartConfigApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
