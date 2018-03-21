package com.db.awmd.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TryLockFailException extends RuntimeException {

    public TryLockFailException(String message) {
        super(message);
    }

    public TryLockFailException(Throwable cause) {
        super(cause);
    }
}
