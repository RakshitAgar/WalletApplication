package com.example.WalletApplication.service;

import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.TransferTransaction;
import com.example.WalletApplication.entity.Wallet;
import com.example.WalletApplication.enums.TransactionType;
import com.example.WalletApplication.repository.TransactionRepository;
import com.example.WalletApplication.repository.TransferTransactionRepository;
import com.example.WalletApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransferTransactionRepository transferTransactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionHistory() {
        Long walletId = 1L;
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(100.0, TransactionType.DEPOSIT, null)); // Sample transaction

        Wallet senderWallet = mock(Wallet.class);
        when(senderWallet.getId()).thenReturn(walletId);

        Wallet recipientWallet = mock(Wallet.class);
        when(recipientWallet.getId()).thenReturn(walletId);

        List<TransferTransaction> sendTransactions = new ArrayList<>();
        sendTransactions.add(new TransferTransaction(50.0, TransactionType.TRANSFER, senderWallet, recipientWallet)); // Sample transfer

        when(transactionRepository.findByWalletId(walletId)).thenReturn(transactions);
        when(transferTransactionRepository.findBySenderWalletId(walletId)).thenReturn(sendTransactions);
        when(transferTransactionRepository.findByRecipientWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(1L, walletId);

        assertEquals(2, result.size()); // Expecting 1 Transaction + 1 TransferTransactionDTO
        verify(transactionRepository, times(1)).findByWalletId(walletId);
        verify(transferTransactionRepository, times(1)).findBySenderWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryByType() {
        Long walletId = 1L;
        Wallet wallet = mock(Wallet.class);
        when(wallet.getId()).thenReturn(walletId);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(100.0, TransactionType.DEPOSIT, wallet)); // Sample transaction with non-null wallet

        when(transactionRepository.findByWalletIdAndType(walletId, TransactionType.DEPOSIT)).thenReturn(transactions);

        List<String> types = List.of("DEPOSIT");
        List<Object> result = transactionService.getTransactionHistoryByType(1L, walletId, types);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByWalletIdAndType(walletId, TransactionType.DEPOSIT);
    }

    @Test
    void testSortTransactionsAscending() {
        Wallet wallet = mock(Wallet.class);
        List<Object> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction(100.0, TransactionType.DEPOSIT, wallet);
        Transaction transaction2 = new Transaction(50.0, TransactionType.WITHDRAWAL, wallet);
        transactions.add(transaction1);
        transactions.add(transaction2);

        List<Object> sortedTransactions = transactionService.sortTransactions(transactions, "asc");

        assertEquals(transaction1, sortedTransactions.get(0));
        assertEquals(transaction2, sortedTransactions.get(1));
    }

    @Test
    void testSortTransactionsDescending() {
        Wallet wallet = mock(Wallet.class);
        List<Object> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction(100.0, TransactionType.DEPOSIT, wallet);
        Transaction transaction2 = new Transaction(50.0, TransactionType.WITHDRAWAL, wallet);
        transactions.add(transaction1);
        transactions.add(transaction2);

        List<Object> sortedTransactions = transactionService.sortTransactions(transactions, "desc");

        assertEquals(transaction2, sortedTransactions.get(0));
        assertEquals(transaction1, sortedTransactions.get(1));
    }
}
