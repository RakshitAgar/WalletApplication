package com.example.WalletApplication.dto;

import lombok.Data;

@Data
public class TransferTransactionRequestDTO {
    private Long userId;
    private Double amount;
    private String password;
    private Long receiverId;
}
