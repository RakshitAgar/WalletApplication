package com.example.WalletApplication.entity;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "wallets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance = 0.0;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private List<Transaction> transactions = new ArrayList<>();

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance += amount;
        this.transactions.add(new Transaction(amount, TransactionType.DEPOSIT));
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > this.balance) {
            throw new NotSufficientBalance("Insufficient balance");
        }
        this.balance -= amount;
        this.transactions.add(new Transaction(amount, TransactionType.WITHDRAWAL));
    }

    public void transfer(double amount, Wallet recipient) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (amount > this.balance) {
            throw new NotSufficientBalance("Insufficient balance");
        }
        this.balance -= amount;
        recipient.balance += amount;
        this.transactions.add(new Transaction(amount, TransactionType.TRANSFER, recipient.getId()));
        recipient.transactions.add(new Transaction(amount, TransactionType.DEPOSIT, this.getId()));
    }
}