package com.example.WalletApplication.Exceptions;

public class UnAuthorisedUserException extends RuntimeException {
    public UnAuthorisedUserException(String message) {
        super(message);
    }
}
