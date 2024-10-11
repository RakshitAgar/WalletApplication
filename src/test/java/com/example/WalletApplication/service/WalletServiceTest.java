package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

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
            walletService.deposit("userName",100.0);
        });
    }

    @Test
    public void testWalletServiceDepositWhenUserFound() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);

        String username = "testUser";
        String password = "testPassword";

        User savedUser = new User(username,password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        assertEquals(0.0,savedUser.getWallet().getBalance());
        walletService.deposit(username,100.0);
        assertEquals(100.0,savedUser.getWallet().getBalance());

    }

    @Test
    public void testWalletServiceWithDrawExceptionUserNotFound() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);
        assertThrows(UserNotFoundException.class, () -> {
            walletService.withdraw("userName",100.0);
        });
    }

    @Test
    public void testWalletServiceWithDrawWithSufficientBalance() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);

        String username = "testUser";
        String password = "testPassword";

        User savedUser = new User(username,password);
        savedUser.getWallet().deposit(200);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        assertEquals(200.0,savedUser.getWallet().getBalance());
        Double amount = walletService.withdraw(username,100.0);
        assertEquals(100.0,amount);

    }

    @Test
    public void testWalletServiceWithDrawWithInSufficientBalance() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);

        String username = "testUser";
        String password = "testPassword";

        User savedUser = new User(username,password);
        savedUser.getWallet().deposit(100);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        assertEquals(100.0,savedUser.getWallet().getBalance());
        assertThrows(NotSufficientBalance.class, () -> {
            walletService.withdraw(username,200.0);
        });
    }

}