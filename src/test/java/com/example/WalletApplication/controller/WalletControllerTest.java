package com.example.WalletApplication.controller;

import com.example.WalletApplication.Exceptions.CustomExceptionHandler;
import com.example.WalletApplication.Exceptions.UnAuthorisedUserException;
import com.example.WalletApplication.Exceptions.UnAuthorisedWalletException;
import com.example.WalletApplication.config.SecurityConfig;
import com.example.WalletApplication.dto.TransactionRequestDTO;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
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

        doNothing().when(walletService).deposit(anyLong(), anyDouble());

        mockMvc.perform(post("/users/1/wallets/1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":100.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount deposited successfully"))
                .andExpect(jsonPath("$.amount").value(100.0));

        verify(walletService, times(1)).deposit(1L, 100.0);
    }

    @Test
    public void testDeposit_UnAuthorisedUserId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedUserException("User not authorized"))
                .when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(post("/users/1/wallets/1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized"));

        verify(walletService, times(1)).isUserAuthorized(anyLong(), anyLong());
    }

    @Test
    public void testDeposit_InvalidWalletId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedWalletException("User not authorized for this Wallet"))
                .when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(post("/users/1/wallets/999/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized for this Wallet"));

        verify(walletService, times(1)).isUserAuthorized(anyLong(), anyLong());
    }



    @Test
    public void testWithdraw_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(1L);
        request.setAmount(50.0);
        request.setPassword("testPassword");

        when(walletService.withdraw(anyLong(), anyDouble())).thenReturn(50.0);

        mockMvc.perform(post("/users/1/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":50.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount withdrawn successfully"))
                .andExpect(jsonPath("$.withdrawnAmount").value(50.0));

        verify(walletService, times(1)).withdraw(1L, 50.0);
    }


    @Test
    public void testDeposit_Failure_InvalidUserId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new IllegalArgumentException("User not found"))
                .when(walletService).deposit(anyLong(), anyDouble());

        mockMvc.perform(post("/users/999/wallets/1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).deposit(eq(999L), eq(100.0));
    }



    @Test
    public void testWithdraw_Failure_InvalidUserId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        when(walletService.withdraw(anyLong(), anyDouble())).thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(post("/users/999/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":999,\"amount\":50.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).withdraw(eq(999L), eq(50.0));
    }

    @Test
    public void testWithdraw_Failure_UnAuthorisedUser() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedUserException("User not authorized"))
                .when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(post("/users/999/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":999,\"amount\":50.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized"));

        verify(walletService, times(1)).isUserAuthorized(anyLong(), anyLong());
    }

    @Test
    public void testWithdraw_FailureInValidWalletID() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedWalletException("User not authorized for this Wallet"))
                .when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(post("/users/999/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":999,\"amount\":50.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized for this Wallet"));

        verify(walletService, times(1)).isUserAuthorized(anyLong(), anyLong());
    }
}