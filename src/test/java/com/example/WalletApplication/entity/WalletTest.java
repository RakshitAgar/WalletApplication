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

    @Test
    public void testWalletSetBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(100.0);

        assertEquals(100.0, wallet.getBalance());
    }

    @Test
    public void testTwoWalletCreation() {
        Wallet wallet1 = new Wallet();
        Wallet wallet2 = new Wallet();

        wallet1.setBalance(100.0);
        wallet2.setBalance(101.0);

        assertNotEquals(wallet1, wallet2);
    }
}