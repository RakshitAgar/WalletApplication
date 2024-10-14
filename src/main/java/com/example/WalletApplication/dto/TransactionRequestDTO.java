package com.example.WalletApplication.dto;

import lombok.Data;

@Data
public class TransactionRequestDTO {
    private Double amount;
    private String password;
}
