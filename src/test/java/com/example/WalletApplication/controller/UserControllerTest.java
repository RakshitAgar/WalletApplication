package com.example.WalletApplication.controller;

import com.example.WalletApplication.Exceptions.InvalidUserRegistrationCredentials;
import com.example.WalletApplication.Exceptions.UserAlreadyPresentException;
import com.example.WalletApplication.config.SecurityConfig;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.enums.CurrencyType;
import com.example.WalletApplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        // Arrange
        User mockUser = new User("testUser", "testPassword",CurrencyType.USD);

        when(userService.registerUser(anyString(), anyString(), any(CurrencyType.class))).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"testPassword\",\"currencyType\":\"USD\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.password").value("testPassword"));

        verify(userService, times(1)).registerUser("testUser", "testPassword", CurrencyType.USD);
    }

    @Test
    public void testRegisterUserFailureWhenUserAlreadyPresent() throws Exception {

        when(userService.registerUser(anyString(), anyString(),any(CurrencyType.class))).thenThrow(new UserAlreadyPresentException("User with username already exists"));

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"User1\",\"password\":\"testPassword\",\"currencyType\":\"USD\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("User with username already exists"));

        verify(userService, times(1)).registerUser(anyString(), anyString(),any(CurrencyType.class));
    }

    @Test
    public void testRegisterUserFailureWhenUserNameIsPassedEmpty() throws Exception {

        when(userService.registerUser(anyString(), anyString(),any(CurrencyType.class))).thenThrow(new InvalidUserRegistrationCredentials("Username and password cannot be empty"));

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"testPassword\",\"currencyType\":\"USD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Username and password cannot be empty"));

        verify(userService, times(1)).registerUser(anyString(), anyString(),any(CurrencyType.class));
    }

    @Test
    public void testRegisterUserFailureWhenPasswordIsPassedEmpty() throws Exception {

        when(userService.registerUser(anyString(), anyString(),any(CurrencyType.class))).thenThrow(new InvalidUserRegistrationCredentials("Username and password cannot be empty"));

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"name\",\"password\":\"\",\"currencyType\":\"USD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Username and password cannot be empty"));

        verify(userService, times(1)).registerUser(anyString(), anyString(),any(CurrencyType.class));
    }

}