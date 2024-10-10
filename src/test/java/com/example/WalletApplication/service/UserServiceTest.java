package com.example.WalletApplication.service;

import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.entity.Wallet;
import com.example.WalletApplication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void testUserRegistration() {
        assertDoesNotThrow(() -> {
            UserService userService = new UserService(userRepository);
        });
    }

}