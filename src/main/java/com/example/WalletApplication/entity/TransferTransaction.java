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
public class TransferTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id", nullable = false)
    private Wallet senderWallet;

    @ManyToOne
    @JoinColumn(name = "recipient_wallet_id", nullable = false)
    private Wallet recipientWallet;

    public TransferTransaction(double amount, TransactionType type, Wallet senderWallet , Wallet receiverWallet) {
        this.amount = amount;
        this.type = type;
        this.senderWallet = senderWallet;
        this.recipientWallet = receiverWallet;
        this.timestamp = LocalDateTime.now();
    }
}