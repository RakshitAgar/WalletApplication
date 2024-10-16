package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.TransferTransaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.enums.TransactionType;
import com.example.WalletApplication.repository.TransactionRepository;
import com.example.WalletApplication.repository.TransferTransactionRepository;
import com.example.WalletApplication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private TransferTransactionRepository transferTransactionRepository;

    @Test
    public void testTransactionServiceTransferWithSufficientBalance() {
        Long senderId = 1L;
        Long receiverId = 2L;
        User sender = new User("senderUser", "senderPassword");
        User receiver = new User("receiverUser", "receiverPassword");
        sender.getWallet().deposit(200);

        // Mock repository behavior
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));

        // Perform the transfer
        transactionService.transfer(senderId, receiverId, 100.0);

        // Verify that the transfer transaction is saved
        verify(transferTransactionRepository, times(1)).save(any(TransferTransaction.class));

        // Verify the balances
        assertEquals(100.0, sender.getWallet().getBalance());
        assertEquals(100.0, receiver.getWallet().getBalance());
    }
    @Test
    public void testTransactionServiceTransferWithInsufficientBalance() {
        Long senderId = 1L;
        Long receiverId = 2L;
        User sender = new User("senderUser", "senderPassword");
        User receiver = new User("receiverUser", "receiverPassword");
        sender.getWallet().deposit(50);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));

        // Test insufficient balance case
        assertThrows(NotSufficientBalance.class, () -> {
            transactionService.transfer(senderId, receiverId, 100.0);
        });

        assertEquals(50.0, sender.getWallet().getBalance());
        assertEquals(0.0, receiver.getWallet().getBalance());
    }
//    @Test
//    public void testTransactionServiceGetTransactionHistory() {
//        Long walletId = 1L;
//        User user = new User("testUser", "testPassword");
//        user.getWallet().deposit(100);
//        user.getWallet().withdraw(50);
//
//        // Create mock transactions
//        Transaction depositTransaction = new Transaction(100.0, TransactionType.DEPOSIT, "deposit", user.getWallet());
//        Transaction withdrawTransaction = new Transaction(50.0, TransactionType.WITHDRAWAL, "withdraw", user.getWallet());
//
//        // Mock repository behavior
//        when(transactionRepository.findByWalletId(walletId)).thenReturn(List.of(depositTransaction, withdrawTransaction));
//
//        // Retrieve transaction history
//        List<Transaction> transactions = transactionService.getTransactionHistory(walletId);
//
//        // Ensure 2 transactions (deposit and withdraw)
//        assertEquals(2, transactions.size());
//        assertEquals(100.0, transactions.get(0).getAmount());
//        assertEquals(50.0, transactions.get(1).getAmount());
//    }

}
