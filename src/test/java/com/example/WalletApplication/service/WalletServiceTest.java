package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testWalletServiceCreation() {
        UserRepository userRepository = mock(UserRepository.class);
        assertDoesNotThrow(() -> new WalletService(userRepository));
    }

    @Test
    public void testWalletServiceDepositExceptionUserNotFound() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);
        assertThrows(UserNotFoundException.class, () -> {
            walletService.deposit(1L, 100.0);
        });
    }

    @Test
    public void testWalletServiceDepositWhenUserFound() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);

        Long userId = 1L;
        User savedUser = new User("testUser", "testPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));

        assertEquals(0.0, savedUser.getWallet().getBalance());
        walletService.deposit(userId, 100.0);
        assertEquals(100.0, savedUser.getWallet().getBalance());
    }

    @Test
    public void testWalletServiceWithdrawExceptionUserNotFound() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);
        assertThrows(UserNotFoundException.class, () -> {
            walletService.withdraw(1L, 100.0);
        });
    }

    @Test
    public void testWalletServiceWithdrawWithSufficientBalance() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);

        Long userId = 1L;
        User savedUser = new User("testUser", "testPassword");
        savedUser.getWallet().deposit(200);

        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));

        assertEquals(200.0, savedUser.getWallet().getBalance());
        Double amount = walletService.withdraw(userId, 100.0);
        assertEquals(100.0, amount);
    }

    @Test
    public void testWalletServiceWithdrawWithInsufficientBalance() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);

        Long userId = 1L;
        User savedUser = new User("testUser", "testPassword");
        savedUser.getWallet().deposit(100);

        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));

        assertEquals(100.0, savedUser.getWallet().getBalance());
        assertThrows(NotSufficientBalance.class, () -> {
            walletService.withdraw(userId, 200.0);
        });
    }
}