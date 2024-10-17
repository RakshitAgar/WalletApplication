package com.example.WalletApplication.dto;

import com.example.WalletApplication.enums.CurrencyType;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String currencyType;
}
