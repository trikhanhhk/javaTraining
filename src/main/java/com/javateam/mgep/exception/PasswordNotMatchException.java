package com.javateam.mgep.exception;

public class PasswordNotMatchException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PasswordNotMatchException () {
        super("Password and Repeat Password don't match !!");
    }
}
