package com.example.WalletApplication.controller;

import com.example.WalletApplication.dto.TransactionRequestDTO;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @Test
    public void testDeposit_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(1L);
        request.setAmount(100.0);
        request.setPassword("testPassword");

        doNothing().when(userService).authenticateUser(anyLong(), anyString());
        doNothing().when(walletService).deposit(anyLong(), anyDouble());

        mockMvc.perform(post("/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":100.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount deposited successfully"))
                .andExpect(jsonPath("$.amount").value(100.0));

        verify(userService, times(1)).authenticateUser(1L, "testPassword");
        verify(walletService, times(1)).deposit(1L, 100.0);
    }

    @Test
    public void testWithdraw_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(1L);
        request.setAmount(50.0);
        request.setPassword("testPassword");

        doNothing().when(userService).authenticateUser(anyLong(), anyString());
        when(walletService.withdraw(anyLong(), anyDouble())).thenReturn(50.0);

        mockMvc.perform(post("/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":50.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount withdrawn successfully"))
                .andExpect(jsonPath("$.withdrawnAmount").value(50.0));

        verify(userService, times(1)).authenticateUser(1L, "testPassword");
        verify(walletService, times(1)).withdraw(1L, 50.0);
    }

    @Test
    public void testDeposit_Failure_InvalidPassword() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(1L);
        request.setAmount(100.0);
        request.setPassword("wrongPassword");

        doThrow(new IllegalArgumentException("Invalid password")).when(userService).authenticateUser(anyLong(), anyString());

        mockMvc.perform(post("/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":100.0,\"password\":\"wrongPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid password"));

        verify(userService, times(1)).authenticateUser(1L, "wrongPassword");
        verify(walletService, times(0)).deposit(anyLong(), anyDouble());
    }

    @Test
    public void testDeposit_Failure_InvalidUserId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(999L); // Non-existent user ID
        request.setAmount(100.0);
        request.setPassword("testPassword");

        doThrow(new IllegalArgumentException("User not found")).when(userService).authenticateUser(anyLong(), anyString());

        mockMvc.perform(post("/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":999,\"amount\":100.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));

        verify(userService, times(1)).authenticateUser(999L, "testPassword");
        verify(walletService, times(0)).deposit(anyLong(), anyDouble());
    }

    @Test
    public void testWithdraw_Failure_InvalidPassword() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(1L);
        request.setAmount(50.0);
        request.setPassword("wrongPassword");

        doThrow(new IllegalArgumentException("Invalid password")).when(userService).authenticateUser(anyLong(), anyString());

        mockMvc.perform(post("/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":50.0,\"password\":\"wrongPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid password"));

        verify(userService, times(1)).authenticateUser(1L, "wrongPassword");
        verify(walletService, times(0)).withdraw(anyLong(), anyDouble());
    }

    @Test
    public void testWithdraw_Failure_InvalidUserId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(999L); // Non-existent user ID
        request.setAmount(50.0);
        request.setPassword("testPassword");

        doThrow(new IllegalArgumentException("User not found")).when(userService).authenticateUser(anyLong(), anyString());

        mockMvc.perform(post("/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":999,\"amount\":50.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));

        verify(userService, times(1)).authenticateUser(999L, "testPassword");
        verify(walletService, times(0)).withdraw(anyLong(), anyDouble());
    }
}