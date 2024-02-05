package com.telerikacademy.web.forumsystem.exceptions;

public class AuthenticationFailureException extends RuntimeException {
    public AuthenticationFailureException(String message) {
        super(message);
    }
}