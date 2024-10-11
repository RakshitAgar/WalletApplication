package com.example.WalletApplication.controller;

import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testRegisterUser_Success() {

        String username = "testUser";
        String password = "testPassword";
        User mockUser = new User(username, password);
        when(userService.registerUser(anyString(), anyString())).thenReturn(mockUser);

        ResponseEntity<?> response = userController.register(username, password);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockUser, response.getBody());
        verify(userService, times(1)).registerUser(username, password);
    }
}