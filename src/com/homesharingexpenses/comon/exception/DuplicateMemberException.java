package com.homesharingexpenses.comon.exception;

/**
 * This exception is throw when a member already exists
 */
public final class DuplicateMemberException extends RuntimeException {

    public DuplicateMemberException(final String message) {
        super(message);
    }
}
