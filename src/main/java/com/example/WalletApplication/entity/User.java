package com.example.WalletApplication.entity;

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

    public User(String username, String password) {
        if(username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Username and password cannot be null or blank");
        }
        this.username = username;
        this.password = password;
        initializeWallet();
    }

    private void initializeWallet() {
        this.wallet = new Wallet();
    }

    public User() {
    }
}