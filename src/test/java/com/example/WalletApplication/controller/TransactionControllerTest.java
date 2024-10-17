package com.example.WalletApplication.controller;

import com.example.WalletApplication.Exceptions.UnAuthorisedUserException;
import com.example.WalletApplication.Exceptions.UnAuthorisedWalletException;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.TransferTransaction;
import com.example.WalletApplication.entity.Wallet;
import com.example.WalletApplication.enums.TransactionType;
import com.example.WalletApplication.service.TransactionService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    WalletService walletService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    public void testTransfer_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        doNothing().when(transactionService).transfer(anyLong(), anyLong(), anyDouble());
        doNothing().when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(post("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverId\":2,\"amount\":50.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount transferred successfully"))
                .andExpect(jsonPath("$.amount").value(50.0));

        verify(transactionService, times(1)).transfer(1L, 2L, 50.0);
    }

    @Test
    public void testTransfer_UnAuthorisedUser() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        doThrow(new UnAuthorisedUserException("User not authorized")).when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(post("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverId\":2,\"amount\":50.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized"));
        verify(walletService, times(1)).isUserAuthorized(anyLong(), anyLong());
    }

    @Test
    public void testTransferInValidWalletID() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        doThrow(new UnAuthorisedWalletException("User not authorized for this Wallet")).when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(post("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverId\":2,\"amount\":50.0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized for this Wallet"));
        verify(walletService, times(1)).isUserAuthorized(anyLong(), anyLong());
    }

    @Test
    public void testGetTransactionHistory_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        Long userId = 1L;
        Wallet wallet = new Wallet();
        // Ensure the returned list contains both Transaction and TransferTransaction if applicable
        List<Object> transactions = List.of(
                new Transaction(100.0, TransactionType.DEPOSIT, wallet),
                new Transaction(-50.0, TransactionType.WITHDRAWAL, wallet)
        );

        doNothing().when(walletService).isUserAuthorized(anyLong(), anyLong());
        when(transactionService.getTransactionHistory(anyLong())).thenReturn(transactions);
        when(transactionService.sortTransactions(transactions, "asc")).thenReturn(transactions);

        mockMvc.perform(get("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].amount").value(-50.0))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));

        verify(transactionService, times(1)).getTransactionHistory(userId);
    }

    @Test
    public void testGetTransactionHistory_UnAuthorisedUser() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        doThrow(new UnAuthorisedUserException("User not authorized")).when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(get("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized"));

        verify(walletService, times(1)).isUserAuthorized(anyLong(), anyLong());
    }

    @Test
    public void testGetTransactionHistoryInValidWalletID() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        doThrow(new UnAuthorisedWalletException("User not authorized for this Wallet")).when(walletService).isUserAuthorized(anyLong(), anyLong());

        mockMvc.perform(get("/users/1/wallets/1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authorized for this Wallet"));

        verify(walletService, times(1)).isUserAuthorized(anyLong(), anyLong());
    }

    @Test
    public void testGetTransactionHistoryWithDepositType() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet();

        doNothing().when(walletService).isUserAuthorized(userId, walletId);
        List<Object> transactions = List.of(
                new Transaction(100.0, TransactionType.DEPOSIT, wallet)
        );

        when(transactionService.getTransactionHistoryByType(walletId, List.of("DEPOSIT")))
                .thenReturn(transactions);
        when(transactionService.sortTransactions(transactions, "asc")).thenReturn(transactions);

        // Perform request with filter type 'DEPOSIT'
        mockMvc.perform(get("/users/1/wallets/1/transfers")
                        .param("type", "DEPOSIT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1]").doesNotExist());

        // Verifying the interaction with the service method
        verify(transactionService, times(1)).getTransactionHistoryByType(walletId, List.of("DEPOSIT"));
    }


    @Test
    public void testGetTransactionHistoryWithWithDrawType() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet();

        doNothing().when(walletService).isUserAuthorized(userId, walletId);
        List<Object> transactions = List.of(
                new Transaction(50.0, TransactionType.WITHDRAWAL, wallet)
        );
        when(transactionService.getTransactionHistoryByType(walletId, List.of("WITHDRAWAL")))
                .thenReturn(transactions);

        when(transactionService.sortTransactions(transactions, "asc")).thenReturn(transactions);


        mockMvc.perform(get("/users/1/wallets/1/transfers")
                        .param("type", "WITHDRAWAL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(50.0))
                .andExpect(jsonPath("$[0].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[1]").doesNotExist());

        verify(transactionService, times(1)).getTransactionHistoryByType(walletId, List.of("WITHDRAWAL"));
    }

    @Test
    public void testGetTransactionHistoryWithDepositAndTransferType() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet();

        doNothing().when(walletService).isUserAuthorized(userId, walletId);

        List<Object> transactions = List.of(
                new Transaction(100.0, TransactionType.DEPOSIT, wallet),
                new TransferTransaction(50.0, TransactionType.TRANSFER, wallet, new Wallet())
        );

        when(transactionService.getTransactionHistoryByType(walletId, List.of("DEPOSIT", "TRANSFER")))
                .thenReturn(transactions);
        when(transactionService.sortTransactions(transactions, "asc")).thenReturn(transactions);

        mockMvc.perform(get("/users/1/wallets/1/transfers")
                        .param("type", "DEPOSIT,TRANSFER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].amount").value(50.0))
                .andExpect(jsonPath("$[1].type").value("TRANSFER"))
                .andExpect(jsonPath("$[2]").doesNotExist());

        verify(transactionService, times(1)).getTransactionHistoryByType(walletId, List.of("DEPOSIT", "TRANSFER"));
    }

    @Test
    public void testGetTransactionHistoryWithDepositAndWithdrawType() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet();

        doNothing().when(walletService).isUserAuthorized(userId, walletId);
        List<Object> transactions = List.of(
                new Transaction(100.0, TransactionType.DEPOSIT, wallet),
                new Transaction(50.0, TransactionType.WITHDRAWAL, wallet)
        );

        when(transactionService.getTransactionHistoryByType(walletId, List.of("DEPOSIT", "WITHDRAWAL")))
                .thenReturn(transactions);
        when(transactionService.sortTransactions(transactions, "asc")).thenReturn(transactions);

        mockMvc.perform(get("/users/1/wallets/1/transfers")
                        .param("type", "DEPOSIT,WITHDRAWAL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].amount").value(50.0))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[2]").doesNotExist());

        verify(transactionService, times(1)).getTransactionHistoryByType(walletId, List.of("DEPOSIT", "WITHDRAWAL"));
    }
}