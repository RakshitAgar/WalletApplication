package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.dto.TransferTransactionDTO;
import com.example.WalletApplication.entity.TransferTransaction;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.enums.TransactionType;
import com.example.WalletApplication.repository.TransactionRepository;
import com.example.WalletApplication.repository.TransferTransactionRepository;
import com.example.WalletApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransferTransactionRepository transferTransactionRepository;

    @Transactional
    public void transfer(Long senderId, Long receiverId, Double amount) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Receiver not found"));
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

    public List<Object> getTransactionHistory(Long walletId) {
        List<Transaction> transaction = transactionRepository.findByWalletId(walletId);
        List<TransferTransaction> sendTransaction = transferTransactionRepository.findBySenderWalletId(walletId);
        List<TransferTransaction> receiveTransaction = transferTransactionRepository.findByRecipientWalletId(walletId);

        List<Object> allTransactions = new ArrayList<>();
        allTransactions.addAll(transaction);
        allTransactions.addAll(sendTransaction.stream().map(this::convertToDTO).collect(Collectors.toList()));
        allTransactions.addAll(receiveTransaction.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return allTransactions;
    }

    public List<Object> getTransactionHistoryByType(Long walletId, List<String> type) {
        List<Object> transaction  = new ArrayList<>();
        for( String typeString : type) {
            switch (typeString) {
                case "DEPOSIT":
                    transaction.addAll(transactionRepository.findByWalletIdAndType(walletId, TransactionType.DEPOSIT));
                    break;
                case "WITHDRAWAL":
                    transaction.addAll(transactionRepository.findByWalletIdAndType(walletId, TransactionType.WITHDRAWAL));
                    break;
                case "TRANSFER":
                    transaction.addAll(transferTransactionRepository.findBySenderWalletId(walletId).stream().map(this::convertToDTO).collect(Collectors.toList()));
                    transaction.addAll(transferTransactionRepository.findByRecipientWalletId(walletId).stream().map(this::convertToDTO).collect(Collectors.toList()));
                    break;
            }
        }
        return transaction;
    }

    private TransferTransactionDTO convertToDTO(TransferTransaction transferTransaction) {
        return new TransferTransactionDTO(
                transferTransaction.getId(),
                transferTransaction.getAmount(),
                transferTransaction.getTimestamp(),
                transferTransaction.getType(),
                transferTransaction.getSenderWallet().getId(),
                transferTransaction.getRecipientWallet().getId()
        );
    }
}