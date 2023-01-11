package com.javateam.mgep.exception;

public class EmailAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super("Email đã được sử dụng");
    }
}
