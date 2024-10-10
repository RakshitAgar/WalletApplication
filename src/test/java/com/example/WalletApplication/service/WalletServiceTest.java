package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.NotSufficientBalance;
import com.example.WalletApplication.Exceptions.UserNotFoundException;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.entity.Wallet;
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
        Wallet wallet = new Wallet();

        User savedUser = new User(username,password,wallet);
        savedUser.setId(1L);
        savedUser.setUsername(username);
        savedUser.setPassword(password);
        savedUser.setWallet(wallet);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        assertEquals(0.0,wallet.getBalance());
        walletService.deposit(username,100.0);
        verify(userRepository, times(1)).save(savedUser);
        assertEquals(100.0,wallet.getBalance());

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
        Wallet wallet = new Wallet();

        User savedUser = new User(username,password,wallet);
        savedUser.setId(1L);
        savedUser.setUsername(username);
        savedUser.setPassword(password);
        savedUser.setWallet(wallet);
        wallet.setBalance(200);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        assertEquals(200.0,wallet.getBalance());
        walletService.withdraw(username,100.0);
        verify(userRepository, times(1)).save(savedUser);
        assertEquals(100.0,wallet.getBalance());

    }

    @Test
    public void testWalletServiceWithDrawWithInSufficientBalance() {
        UserRepository userRepository = mock(UserRepository.class);
        WalletService walletService = new WalletService(userRepository);

        String username = "testUser";
        String password = "testPassword";
        Wallet wallet = new Wallet();

        User savedUser = new User(username,password,wallet);
        savedUser.setId(1L);
        savedUser.setUsername(username);
        savedUser.setPassword(password);
        savedUser.setWallet(wallet);
        wallet.setBalance(100);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        assertEquals(100.0,wallet.getBalance());
        assertThrows(NotSufficientBalance.class, () -> {
            walletService.withdraw(username,200.0);
        });
    }

}