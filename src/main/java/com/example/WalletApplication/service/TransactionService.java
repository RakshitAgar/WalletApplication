package com.example.WalletApplication.service;

import com.example.WalletApplication.dto.TransferTransactionDTO;
import com.example.WalletApplication.entity.TransferTransaction;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.enums.TransactionType;
import com.example.WalletApplication.repository.TransactionRepository;
import com.example.WalletApplication.repository.TransferTransactionRepository;
import com.example.WalletApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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

    public List<Object> getTransactionHistory(Long userId,Long walletId) {
        List<Transaction> transaction = transactionRepository.findByWalletId(walletId);
        List<TransferTransaction> sendTransaction = transferTransactionRepository.findBySenderWalletId(walletId);
        List<TransferTransaction> receiveTransaction = transferTransactionRepository.findByRecipientWalletId(walletId);

        List<Object> allTransactions = new ArrayList<>();
        allTransactions.addAll(transaction);
        allTransactions.addAll(sendTransaction.stream().map(this::convertToDTO).collect(Collectors.toList()));
        allTransactions.addAll(receiveTransaction.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return allTransactions;
    }

    public List<Object> getTransactionHistoryByType(Long userId,Long walletId, List<String> type) {
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

    public List<Object> sortTransactions(List<Object> transactions, String sortByTime) {
    Comparator<Object> comparator = Comparator.comparing(transaction -> {
        if (transaction instanceof Transaction) {
            return ((Transaction) transaction).getTimestamp();
        } else if (transaction instanceof TransferTransactionDTO) {
            return ((TransferTransactionDTO) transaction).getTimestamp();
        } else {
            return null;
        }
    });

    if (sortByTime.equalsIgnoreCase("desc")) {
        comparator = comparator.reversed();
    }

    transactions.sort(comparator);
    return transactions;
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