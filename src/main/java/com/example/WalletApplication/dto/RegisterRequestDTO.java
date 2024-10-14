package com.example.WalletApplication.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class RegisterRequestDTO {
    private String username;
    private String password;
}
