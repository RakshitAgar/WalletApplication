package com.example.WalletApplication.Exceptions;

public class NotSufficientBalance extends RuntimeException {
    public NotSufficientBalance(String message) {
        super(message);
    }
}
