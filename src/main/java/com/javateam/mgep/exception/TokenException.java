package com.javateam.mgep.exception;

public class TokenException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public TokenException() {
        super("Đường dẫn không đúng hoặc đã hết thời hạn!");
    }
}
