package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UnAuthorisedUserException;
import com.example.WalletApplication.Exceptions.UnAuthorisedWalletException;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.enums.TransactionType;
import com.example.WalletApplication.repository.TransactionRepository;
import com.example.WalletApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    public WalletService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void  isUserAuthorized(Long userId, Long walletId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getUsername().equals(authenticatedUsername)){
            throw new UnAuthorisedUserException("User not authorized");
        }
        if(!user.getWallet().getId().equals(walletId)) {
            throw new UnAuthorisedWalletException("User not authorized for this Wallet");
        }
    }

    @Transactional
    public void deposit(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.getWallet().deposit(amount);
        transactionRepository.save(new Transaction(amount, TransactionType.DEPOSIT, user.getWallet()));
    }

    @Transactional
    public Double withdraw(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        try {
            user.getWallet().withdraw(amount);
            transactionRepository.save(new Transaction(amount, TransactionType.WITHDRAWAL, user.getWallet()));
            return amount;
        } catch (IllegalStateException e) {
            throw new NotSufficientBalance("Not sufficient balance");
        }
    }

}