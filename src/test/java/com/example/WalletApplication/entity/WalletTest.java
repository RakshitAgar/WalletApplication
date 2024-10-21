package com.example.WalletApplication.entity;

import com.example.WalletApplication.Exceptions.InvalidAmount;
import com.example.WalletApplication.Exceptions.NotSufficientBalance;
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
    public void testWalletDepositExceptionWhenDepositZeroAmount() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        assertThrows(InvalidAmount.class, () -> {
            wallet.deposit(0);
        });
    }

    @Test
    public void testWalletDepositExceptionWhenDepositNegativeAmount() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        assertThrows(InvalidAmount.class, () -> {
            wallet.deposit(-10);
        });
    }

    @Test
    public void testWalletDepositExceptionWhenDepositPositiveAmount() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        wallet.deposit(100);
        assertEquals(100.0, wallet.getBalance());
    }

    @Test
    public void testWalletWithDrawlExceptionWhenDepositNegativeAmount() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        assertThrows(InvalidAmount.class, () -> {
            wallet.withdraw(-10);
        });
    }

    @Test
    public void testWalletWithDrawlExceptionWhenDepositZeroAmount() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        assertThrows(InvalidAmount.class, () -> {
            wallet.withdraw(0);
        });
    }

    @Test
    public void testWalletWithDrawlExceptionWhenDepositAmountMoreThanAvailable() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        wallet.deposit(100);
        assertThrows(NotSufficientBalance.class, () -> {
            wallet.withdraw(200);
        });
    }

    @Test
    public void testWalletWithDrawlExceptionWhenDepositAmountLessThanAvailable() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        wallet.deposit(100);

        wallet.withdraw(20);
        assertEquals(80.0, wallet.getBalance());
    }
}