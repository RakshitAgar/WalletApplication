package com.example.WalletApplication.Exceptions;

public class InvalidUserRegistrationCredentials extends RuntimeException {
    public InvalidUserRegistrationCredentials(String message) {
        super(message);
    }
}
