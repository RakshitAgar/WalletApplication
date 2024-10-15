package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testTransactionServiceTransferWithSufficientBalance() {
        Long senderId = 1L;
        Long receiverId = 2L;
        User sender = new User("senderUser", "senderPassword");
        User receiver = new User("receiverUser", "receiverPassword");
        sender.getWallet().deposit(200);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));

        transactionService.transfer(senderId, receiverId, 100.0);

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

        assertThrows(NotSufficientBalance.class, () -> {
            transactionService.transfer(senderId, receiverId, 100.0);
        });

        assertEquals(50.0, sender.getWallet().getBalance());
        assertEquals(0.0, receiver.getWallet().getBalance());
    }

    @Test
    public void testTransactionServiceGetTransactionHistory() {
        Long userId = 1L;
        User user = new User("testUser", "testPassword");
        user.getWallet().deposit(100);
        user.getWallet().withdraw(50);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<Transaction> transactions = transactionService.getTransactionHistory(userId);

        assertEquals(2, transactions.size());
        assertEquals(100.0, transactions.get(0).getAmount());
        assertEquals(50.0, transactions.get(1).getAmount());
    }
}