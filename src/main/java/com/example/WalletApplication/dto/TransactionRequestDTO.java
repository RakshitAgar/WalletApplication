package com.example.WalletApplication.dto;

import lombok.Data;

@Data
public class TransactionRequestDTO {
    private Long userId;
    private Double amount;
    private String password;
}
