package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.entity.Wallet;
import com.example.WalletApplication.repository.UserRepository;

public class WalletService {
    private final UserRepository userRepository;
    public WalletService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deposit(String userName,Double amount) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new UserNotFoundException("User not found"));
        Wallet wallet = user.getWallet();
        wallet.setBalance(wallet.getBalance() + amount);

        userRepository.save(user);
    }

    public void withdraw(String userName,Double amount) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new UserNotFoundException("User not found"));
        Wallet wallet = user.getWallet();
        if(wallet.getBalance() < amount) {
            throw new NotSufficientBalance("Not sufficient balance");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        userRepository.save(user);
    }
}
