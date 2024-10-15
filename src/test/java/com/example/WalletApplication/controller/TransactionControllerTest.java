package com.example.WalletApplication.controller;

import com.example.WalletApplication.dto.TransferTransactionRequestDTO;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.enums.TransactionType;
import com.example.WalletApplication.service.TransactionService;
import com.example.WalletApplication.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    public void testTransfer_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        TransferTransactionRequestDTO request = new TransferTransactionRequestDTO();
        request.setUserId(1L);
        request.setReceiverId(2L);
        request.setAmount(50.0);
        request.setPassword("testPassword");

        doNothing().when(transactionService).transfer(anyLong(), anyLong(), anyDouble());

        mockMvc.perform(post("/user/1/wallet/1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"receiverId\":2,\"amount\":50.0,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Amount transferred successfully"))
                .andExpect(jsonPath("$.amount").value(50.0));

        verify(transactionService, times(1)).transfer(1L, 2L, 50.0);
    }

    @Test
    public void testGetTransactionHistory_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        Long userId = 1L;
        String password = "testPassword";
        List<Transaction> transactions = List.of(
                new Transaction(100.0, TransactionType.DEPOSIT),
                new Transaction(-50.0, TransactionType.WITHDRAWAL)
        );

        when(transactionService.getTransactionHistory(anyLong())).thenReturn(transactions);

        mockMvc.perform(get("/user/1/wallet/1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].amount").value(-50.0))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));

        verify(transactionService, times(1)).getTransactionHistory(userId);
    }
}