package com.example.WalletApplication.controller;

import com.example.WalletApplication.Exceptions.*;
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



    // DEPOSIT TESTS
    @Test
    public void testDeposit_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(1L);
        request.setAmount(100.0);
        request.setPassword("testPassword");

        mockMvc.perform(post("/users/1/wallets/1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":100.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount deposited successfully"))
                .andExpect(jsonPath("$.amount").value(100.0));

        verify(walletService, times(1)).deposit(1L, 100.0,1L);
    }

    @Test
    public void testDeposit_UnAuthorisedUserId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedUserException("User not authorized"))
                .when(walletService).deposit(anyLong(),anyDouble(), anyLong());

        mockMvc.perform(post("/users/1/wallets/1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized"));

        verify(walletService, times(1)).deposit(anyLong(),anyDouble(), anyLong());
    }

    @Test
    public void testDeposit_InvalidWalletId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedWalletException("User not authorized for this Wallet"))
                .when(walletService).deposit(anyLong(),anyDouble(), anyLong());

        mockMvc.perform(post("/users/1/wallets/999/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized for this Wallet"));

        verify(walletService, times(1)).deposit(anyLong(),anyDouble(), anyLong());
    }









    // WITHDRAWAL TEST

    @Test
    public void testWithdraw_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setUserId(1L);
        request.setAmount(50.0);
        request.setPassword("testPassword");

        when(walletService.withdraw(anyLong(), anyDouble(),anyLong())).thenReturn(50.0);

        mockMvc.perform(post("/users/1/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":50.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount withdrawn successfully"))
                .andExpect(jsonPath("$.withdrawnAmount").value(50.0));

        verify(walletService, times(1)).withdraw(1L, 50.0,1L);
    }


    @Test
    public void testWithdraw_Failure_UnAuthorisedUser() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedUserException("User not authorized"))
                .when(walletService).withdraw(anyLong(),anyDouble(), anyLong());

        mockMvc.perform(post("/users/999/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":999,\"amount\":50.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized"));

        verify(walletService, times(1)).withdraw(anyLong(),anyDouble(), anyLong());
    }

    @Test
    public void testWithdraw_FailureInValidWalletID() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedWalletException("User not authorized for this Wallet"))
                .when(walletService).withdraw(anyLong(), anyDouble(), anyLong());

        mockMvc.perform(post("/users/999/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":999,\"amount\":50.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized for this Wallet"));

        verify(walletService, times(1)).withdraw(anyLong(),anyDouble(), anyLong());
    }

    @Test
    public void testWithdrawFailure_InSufficientBalance() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new NotSufficientBalance("Insufficient balance"))
                .when(walletService).withdraw(anyLong(), anyDouble(),anyLong());

        mockMvc.perform(post("/users/1/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":50.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));

        verify(walletService, times(1)).withdraw(anyLong(), anyDouble(),anyLong());
    }

    @Test
    public void testWithdrawFailure_InvalidAmount() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new InvalidAmount("Withdrawal amount must be positive"))
                .when(walletService).withdraw(anyLong(), anyDouble(),anyLong());

        mockMvc.perform(post("/users/1/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amount\":50.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Withdrawal amount must be positive"));

        verify(walletService, times(1)).withdraw(anyLong(), anyDouble(),anyLong());
    }












    // TRANSFER TEST
    @Test
    public void testTransfer_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doNothing().when(walletService).transfer(anyLong(), anyLong(), anyDouble(),anyLong());

        mockMvc.perform(post("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverId\":2,\"amount\":50.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount transferred successfully"))
                .andExpect(jsonPath("$.amount").value(50.0));

        verify(walletService, times(1)).transfer(1L, 2L, 50.0,1L);
    }

    @Test
    public void testTransfer_UnAuthorisedUser() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedUserException("User not authorized")).when(walletService).transfer(anyLong(), anyLong(),anyDouble(),anyLong());

        mockMvc.perform(post("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverId\":2,\"amount\":50.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized"));
        verify(walletService, times(1)).transfer(anyLong(), anyLong(), anyDouble(),anyLong());
    }


    @Test
    public void testTransfer_UnAuthorisedWalletId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new UnAuthorisedWalletException("User not authorized for this Wallet")).when(walletService).transfer(anyLong(), anyLong(),anyDouble(),anyLong());

        mockMvc.perform(post("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverId\":2,\"amount\":50.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized for this Wallet"));
        verify(walletService, times(1)).transfer(anyLong(), anyLong(),anyDouble(),anyLong());
    }

    @Test
    public void testTransferFailure_InSufficientBalance() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new NotSufficientBalance("Insufficient balance")).when(walletService).transfer(anyLong(), anyLong(), anyDouble(),anyLong());

        mockMvc.perform(post("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverId\":2,\"amount\":50.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));
        verify(walletService, times(1)).transfer(anyLong(), anyLong(), anyDouble(),anyLong());
    }

    @Test
    public void testTransferFailure_WhenInValidReceiverID() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

        doThrow(new ReceiverNotFoundException("Receiver not found")).when(walletService).transfer(anyLong(), anyLong(), anyDouble(),anyLong());

        mockMvc.perform(post("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverId\":2,\"amount\":50.0}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Receiver not found"));
        verify(walletService, times(1)).transfer(anyLong(), anyLong(), anyDouble(),anyLong());

    }

}