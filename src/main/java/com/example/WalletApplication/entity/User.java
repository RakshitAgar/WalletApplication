package com.example.WalletApplication.entity;

import com.example.WalletApplication.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    public User(String username, String password, CurrencyType currencyType) {
        if(username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Username and password cannot be null or blank");
        }
        this.username = username;
        this.password = password;
        if(currencyType == null) currencyType = CurrencyType.INR;
        initializeWallet(currencyType);
    }

    private void initializeWallet(CurrencyType currencyType) {
        this.wallet = new Wallet(currencyType);
    }

    public User() {
    }
}