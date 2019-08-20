package com.homesharingexpenses.comon.exception;

/**
 * This exception is throw when an object is not found
 */
public final class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(final String message) {
        super(message);
    }
}
