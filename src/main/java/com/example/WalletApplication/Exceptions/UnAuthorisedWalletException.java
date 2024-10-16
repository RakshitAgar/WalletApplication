package com.example.WalletApplication.Exceptions;

public class UnAuthorisedWalletException extends RuntimeException {
    public UnAuthorisedWalletException(String message) {
        super(message);
    }
}
