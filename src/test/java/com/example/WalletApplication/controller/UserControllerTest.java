package com.example.WalletApplication.controller;

import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private WalletService walletService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        // Arrange
        User mockUser = new User("testUser", "testPassword");

        when(userService.registerUser(anyString(), anyString())).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.password").value("testPassword"));

        verify(userService, times(1)).registerUser("testUser", "testPassword");
    }

    @Test
    public void testRegisterUser_Failure() throws Exception {

        when(userService.registerUser(anyString(), anyString())).thenThrow(new IllegalArgumentException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"testPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid credentials"));

        verify(userService, times(1)).registerUser(anyString(), anyString());
    }

    @Test
    public void testDeposit_Success() throws Exception {
        // Arrange
        Long userId = 1L;

        doNothing().when(userService).authenticateUser(anyLong(), anyString());
        doNothing().when(walletService).deposit(anyLong(), anyDouble());

        // Act & Assert
        mockMvc.perform(post("/user/{id}/deposit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount deposited successfully"))
                .andExpect(jsonPath("$.amount").value(100.0));

        verify(userService, times(1)).authenticateUser(userId, "testPassword");
        verify(walletService, times(1)).deposit(userId, 100.0);
    }

    @Test
    public void testWithdraw_Success() throws Exception {
        // Arrange
        Long userId = 1L;

        doNothing().when(userService).authenticateUser(anyLong(), anyString());
        when(walletService.withdraw(anyLong(), anyDouble())).thenReturn(50.0);

        // Act & Assert
        mockMvc.perform(post("/user/{id}/withdraw", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":50.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount withdrawn successfully"))
                .andExpect(jsonPath("$.withdrawnAmount").value(50.0));

        verify(userService, times(1)).authenticateUser(userId, "testPassword");
        verify(walletService, times(1)).withdraw(userId, 50.0);
    }

    @Test
    public void testDeposit_Failure_InvalidPassword() throws Exception {
        // Arrange
        Long userId = 1L;

        doThrow(new IllegalArgumentException("Invalid password")).when(userService).authenticateUser(anyLong(), anyString());

        // Act & Assert
        mockMvc.perform(post("/user/{id}/deposit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0,\"password\":\"wrongPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid password"));

        verify(userService, times(1)).authenticateUser(userId, "wrongPassword");
        verify(walletService, times(0)).deposit(anyLong(), anyDouble());
    }

    @Test
    public void testDeposit_Failure_InvalidUserId() throws Exception {
        // Arrange
        Long userId = 999L; // Non-existent user ID

        doThrow(new IllegalArgumentException("User not found")).when(userService).authenticateUser(anyLong(), anyString());

        // Act & Assert
        mockMvc.perform(post("/user/{id}/deposit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));

        verify(userService, times(1)).authenticateUser(userId, "testPassword");
        verify(walletService, times(0)).deposit(anyLong(), anyDouble());
    }
    @Test
    public void testWithdraw_Failure_InvalidPassword() throws Exception {
        // Arrange
        Long userId = 1L;

        doThrow(new IllegalArgumentException("Invalid password")).when(userService).authenticateUser(anyLong(), anyString());

        // Act & Assert
        mockMvc.perform(post("/user/{id}/withdraw", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":50.0,\"password\":\"wrongPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid password"));

        verify(userService, times(1)).authenticateUser(userId, "wrongPassword");
        verify(walletService, times(0)).withdraw(anyLong(), anyDouble());
    }

    @Test
    public void testWithdraw_Failure_InvalidUserId() throws Exception {
        // Arrange
        Long userId = 999L; // Non-existent user ID

        doThrow(new IllegalArgumentException("User not found")).when(userService).authenticateUser(anyLong(), anyString());

        // Act & Assert
        mockMvc.perform(post("/user/{id}/withdraw", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":50.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));

        verify(userService, times(1)).authenticateUser(userId, "testPassword");
        verify(walletService, times(0)).withdraw(anyLong(), anyDouble());
    }

//    @Test
//    public void testWithdraw_Failure_NotSufficientBalance() throws Exception {
//        // Arrange
//        Long userId = 1L;
//
//        doNothing().when(userService).authenticateUser(anyLong(), anyString());
//        doThrow(new NotSufficientBalance("Not sufficient balance")).when(walletService).withdraw(anyLong(), anyDouble());
//
//        // Act & Assert
//        mockMvc.perform(post("/user/{id}/withdraw", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"amount\":1000.0,\"password\":\"testPassword\"}"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$").value("Not sufficient balance"));
//
//        verify(userService, times(1)).authenticateUser(userId, "testPassword");
//        verify(walletService, times(1)).withdraw(userId, 1000.0);
//    }
}