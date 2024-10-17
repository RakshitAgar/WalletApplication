package com.example.WalletApplication.entity;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    private double balance = 0.0;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private List<Transaction> transactions = new ArrayList<>();

    public Wallet(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public Wallet() {
        this.currencyType = CurrencyType.INR;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > this.balance) {
            throw new NotSufficientBalance("Insufficient balance");
        }
        this.balance -= amount;
    }
}