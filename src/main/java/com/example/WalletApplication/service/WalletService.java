package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        user.getWallet().deposit(amount);
    }

    @Transactional
    public Double withdraw(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        try {
            user.getWallet().withdraw(amount);
            return amount;
        } catch (IllegalStateException e) {
            throw new NotSufficientBalance("Not sufficient balance");
        }
    }

    @Transactional
    public void transfer(Long senderId, Long receiverId, Double amount) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Receiver not found"));
        try {
            sender.getWallet().transfer(amount, receiver.getWallet());
            userRepository.save(sender);
            userRepository.save(receiver);
        } catch (NotSufficientBalance e) {
            throw new NotSufficientBalance("Not sufficient balance");
        }
    }

    public List<Transaction> getTransactionHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getWallet().getTransactions();
    }

}