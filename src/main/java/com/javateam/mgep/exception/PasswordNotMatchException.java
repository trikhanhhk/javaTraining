package com.javateam.mgep.exception;

public class PasswordNotMatchException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PasswordNotMatchException () {
        super("Mật khẩu và nhập lại mật khẩu không khớp");
    }
}
