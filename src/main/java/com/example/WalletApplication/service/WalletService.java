package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final UserRepository userRepository;

    public WalletService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void deposit(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.deposit(amount);
    }

    @Transactional
    public Double withdraw(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        try {
            user.withdraw(amount);
            return amount;
        } catch (IllegalStateException e) {
            throw new NotSufficientBalance("Not sufficient balance");
        }
    }
}