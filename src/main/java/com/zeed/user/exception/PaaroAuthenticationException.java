package com.zeed.user.exception;

import org.springframework.security.core.AuthenticationException;

public class PaaroAuthenticationException extends AuthenticationException {
    public PaaroAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public PaaroAuthenticationException(String msg) {
        super(msg);
    }
}
