package com.evtnet.evtnetback.error;

public class HttpErrorException extends RuntimeException {
    private final int code;

    public HttpErrorException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

