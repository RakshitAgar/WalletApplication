package com.example.WalletApplication.entity;

import com.example.WalletApplication.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private LocalDateTime timestamp;
    private TransactionType type;
    private Long recipientWalletId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "wallet_id")
//    @JsonIgnore
//    private Wallet wallet;

    public Transaction(double amount, TransactionType type) {
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(double amount, TransactionType type, Long recipientWalletId) {
        this.amount = amount;
        this.type = type;
        this.recipientWalletId = recipientWalletId;
        this.timestamp = LocalDateTime.now();
    }
}