package com.example.WalletApplication.entity;

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
        assertThrows(IllegalArgumentException.class, () -> {
            wallet.deposit(0);
        });
    }

    @Test
    public void testWalletDepositExceptionWhenDepositNegativeAmount() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        assertThrows(IllegalArgumentException.class, () -> {
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
        assertThrows(IllegalArgumentException.class, () -> {
            wallet.withdraw(-10);
        });
    }

    @Test
    public void testWalletWithDrawlExceptionWhenDepositZeroAmount() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
        assertThrows(IllegalArgumentException.class, () -> {
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

    @Test
    public void testWalletTransfer() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        senderWallet.deposit(100);
        assertEquals(100.0, senderWallet.getBalance());
        assertEquals(0.0, recipientWallet.getBalance());

        senderWallet.transfer(50, recipientWallet);

        assertEquals(50.0, senderWallet.getBalance());
        assertEquals(50.0, recipientWallet.getBalance());
    }

    @Test
    public void testWalletTransferExceptionWhenAmountMoreThanAvailable() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        senderWallet.deposit(100);
        assertEquals(100.0, senderWallet.getBalance());
        assertEquals(0.0, recipientWallet.getBalance());

        assertThrows(NotSufficientBalance.class, () -> {
            senderWallet.transfer(200, recipientWallet);
        });

        assertEquals(100.0, senderWallet.getBalance());
        assertEquals(0.0, recipientWallet.getBalance());
    }
}