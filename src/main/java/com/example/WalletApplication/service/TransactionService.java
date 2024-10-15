package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

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
