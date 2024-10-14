package com.example.WalletApplication.Exceptions;

public class InValidPasswordException extends RuntimeException {
    public InValidPasswordException(String message) {
        super(message);
    }
}
