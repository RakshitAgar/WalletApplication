package com.example.WalletApplication.dto;

import com.example.WalletApplication.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TransferTransactionDTO {
    private Long id;
    private double amount;
    private LocalDateTime timestamp;
    private TransactionType type;
    private Long senderWalletId;
    private Long recipientWalletId;

    public TransferTransactionDTO(Long id, double amount, LocalDateTime timestamp, TransactionType type, Long senderWalletId, Long recipientWalletId) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.type = type;
        this.senderWalletId = senderWalletId;
        this.recipientWalletId = recipientWalletId;
    }

    // Getters and Setters
}