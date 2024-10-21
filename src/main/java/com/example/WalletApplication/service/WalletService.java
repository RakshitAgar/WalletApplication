package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.*;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.TransferTransaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.entity.Wallet;
import com.example.WalletApplication.enums.TransactionType;
import com.example.WalletApplication.repository.TransactionRepository;
import com.example.WalletApplication.repository.TransferTransactionRepository;
import com.example.WalletApplication.repository.UserRepository;
import com.example.WalletApplication.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final UserRepository userRepository;

    private TransactionRepository transactionRepository;

    private WalletRepository walletRepository;

    private TransferTransactionRepository transferTransactionRepository;

    public WalletService(UserRepository userRepository, TransactionRepository transactionRepository, WalletRepository walletRepository, TransferTransactionRepository transferTransactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.transferTransactionRepository = transferTransactionRepository;
    }

    public void isUserValid(Long userId, Long walletId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String authenticatedUsername = authentication.getName();
    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    if (!user.getUsername().equals(authenticatedUsername)){
        throw new UnAuthorisedUserException("User not authorized");
    }
    Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new UnAuthorisedWalletException("Wallet not found"));
    if (!wallet.getId().equals(walletId)) {
        throw new UnAuthorisedWalletException("User not authorized for this Wallet");
    }
}

    @Transactional
    public void deposit(Long userId, Double amount, Long walletId) {
        try {
            isUserValid(userId, walletId);
        } catch (UnAuthorisedUserException | UnAuthorisedWalletException e) {
            throw e;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.getWallet().deposit(amount);
        transactionRepository.save(new Transaction(amount, TransactionType.DEPOSIT, user.getWallet()));
    }

    @Transactional
    public Double withdraw(Long userId, Double amount, Long walletId) {
        try {
            isUserValid(userId, walletId);
        } catch (UnAuthorisedUserException | UnAuthorisedWalletException e) {
            throw e;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        try {
            user.getWallet().withdraw(amount);
            transactionRepository.save(new Transaction(amount, TransactionType.WITHDRAWAL, user.getWallet()));
            return amount;
        } catch (InvalidAmount e) {
            throw new InvalidAmount(e.getMessage());
        } catch (NotSufficientBalance e) {
            throw new NotSufficientBalance("Insufficient balance");
        }
    }

    @Transactional
    public void transfer(Long senderId, Long receiverId, Double amount, Long walletId) {
        try {
            isUserValid(senderId, walletId);
        } catch (UnAuthorisedUserException | UnAuthorisedWalletException e) {
            throw e;
        }
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ReceiverNotFoundException("Receiver not found"));
        double baseValue = sender.getWallet().getCurrencyType().toBase(amount);
        double convertedAmount = receiver.getWallet().getCurrencyType().fromBase(baseValue);
        try {
            sender.getWallet().withdraw(amount);
            receiver.getWallet().deposit(convertedAmount);
            transferTransactionRepository.save(new TransferTransaction(amount, TransactionType.TRANSFER, sender.getWallet(), receiver.getWallet()));
        } catch (NotSufficientBalance e) {
            throw new NotSufficientBalance("Not sufficient balance");
        }
    }

}