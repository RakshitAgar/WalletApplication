package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.InvalidUserRegistrationCredentials;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.entity.Wallet;
import com.example.WalletApplication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    public void testUserRegistration() {
        UserRepository userRepository = mock(UserRepository.class);
        assertDoesNotThrow(() -> {
            UserService userService = new UserService(userRepository);
        });
    }

    @Test
    void testRegisterUserWhenUserNameIsEmpty() {
        UserRepository userRepository = mock(UserRepository.class);

        UserService userService = new UserService(userRepository);
        assertThrows(InvalidUserRegistrationCredentials.class, () -> {
            userService.registerUser("","Password");
        });
    }

    @Test
    void testRegisterUserWhenPasswordIsEmpty() {
        UserRepository userRepository = mock(UserRepository.class);

        UserService userService = new UserService(userRepository);
        assertThrows(InvalidUserRegistrationCredentials.class, () -> {
            userService.registerUser("username","");
        });
    }

    @Test
    void testRegisterUserWhenPasswordAndUserNameIsEmpty() {
        UserRepository userRepository = mock(UserRepository.class);

        UserService userService = new UserService(userRepository);
        assertThrows(InvalidUserRegistrationCredentials.class, () -> {
            userService.registerUser("","");
        });
    }


    @Test
    void testRegisterUser() {
        // Create the mock
        UserRepository userRepository = mock(UserRepository.class);

        // Create the service with the mock
        UserService userService = new UserService(userRepository);

        String username = "testUser";
        String password = "testPassword";
        Wallet wallet = new Wallet();

        User savedUser = new User(username,password,wallet);
        savedUser.setId(1L);
        savedUser.setUsername(username);
        savedUser.setPassword(password);
        savedUser.setWallet(wallet);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(username, password);

        verify(userRepository,times(1)).save(any(User.class));
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertNotNull(result.getWallet());
    }

}