package com.example.WalletApplication.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    public void testWalletCreation() {
        assertDoesNotThrow(() -> {
            new Wallet();
        });
    }

    @Test
    public void testWalletBalanceAfterCreation() {
        Wallet wallet = new Wallet();
        assertEquals(0.0, wallet.getBalance());
    }
}