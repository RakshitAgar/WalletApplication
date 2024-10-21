package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.*;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.TransferTransaction;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.entity.Wallet;
import com.example.WalletApplication.enums.CurrencyType;
import com.example.WalletApplication.repository.TransactionRepository;
import com.example.WalletApplication.repository.TransferTransactionRepository;
import com.example.WalletApplication.repository.UserRepository;
import com.example.WalletApplication.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransferTransactionRepository transferTransactionRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletService = new WalletService(userRepository, transactionRepository, walletRepository,transferTransactionRepository);
        // Set up authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testDepositSuccess() {
        Long userId = 1L;
        Long walletId = 1L;
        Double amount = 100.0;

        User mockUser = mock(User.class);
        Wallet mockWallet = mock(Wallet.class);

        // Mock authentication and repository interactions
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockUser.getWallet()).thenReturn(mockWallet);

        // Spy on the walletService to mock the isUserValid method
        WalletService spyWalletService = spy(walletService);
        doNothing().when(spyWalletService).isUserValid(userId, walletId);

        // Call the method to test
        spyWalletService.deposit(userId, amount, walletId);

        // Verify interactions
        verify(mockWallet).deposit(amount);
        verify(transactionRepository).save(any(Transaction.class));
    }



    @Test
    void testWithdrawSuccess() {
        Long userId = 1L;
        Long walletId = 1L;
        Double amount = 50.0;

        User mockUser = mock(User.class);
        Wallet mockWallet = mock(Wallet.class);

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockUser.getWallet()).thenReturn(mockWallet);

        // Spy on the walletService to mock the isUserValid method
        WalletService spyWalletService = spy(walletService);
        doNothing().when(spyWalletService).isUserValid(userId, walletId);

        // Call the method to test
        spyWalletService.withdraw(userId, amount, walletId);

        // Verify interactions
        verify(mockWallet).withdraw(amount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testTransferSuccess() {
        Long senderId = 1L;
        Long receiverId = 2L;
        Long walletId = 1L;
        Double amount = 30.0;

        User sender = mock(User.class);
        User receiver = mock(User.class);
        Wallet senderWallet = mock(Wallet.class);
        Wallet receiverWallet = mock(Wallet.class);
        CurrencyType mockCurrencyType = mock(CurrencyType.class);

        // Mock authentication and repository interactions
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(senderWallet));
        when(sender.getUsername()).thenReturn("testUser");
        when(sender.getWallet()).thenReturn(senderWallet);
        when(receiver.getWallet()).thenReturn(receiverWallet);
        when(senderWallet.getCurrencyType()).thenReturn(mockCurrencyType);
        when(receiverWallet.getCurrencyType()).thenReturn(mockCurrencyType);
        when(mockCurrencyType.toBase(amount)).thenReturn(amount);
        when(mockCurrencyType.fromBase(amount)).thenReturn(amount);

        // Spy on the walletService to mock the isUserValid method
        WalletService spyWalletService = spy(walletService);
        doNothing().when(spyWalletService).isUserValid(senderId, walletId);

        // Call the method to test
        spyWalletService.transfer(senderId, receiverId, amount, walletId);

        // Verify interactions
        verify(senderWallet).withdraw(amount);
        verify(receiverWallet).deposit(amount);
        verify(transferTransactionRepository).save(any(TransferTransaction.class));
    }

    @Test
    void testDepositUnauthorizedUser() {
        Long userId = 1L;
        Long walletId = 1L;
        Double amount = 100.0;

        User mockUser = mock(User.class);
        when(authentication.getName()).thenReturn("wrongUser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getUsername()).thenReturn("testUser");

        assertThrows(UnAuthorisedUserException.class, () -> walletService.deposit(userId, amount, walletId));
    }

    @Test
    void testWithdrawInsufficientBalance() {
        Long userId = 1L;
        Long walletId = 1L;
        Double amount = 50.0;

        User mockUser = mock(User.class);
        Wallet mockWallet = mock(Wallet.class);

        // Mock authentication and repository interactions
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockUser.getWallet()).thenReturn(mockWallet);

        // Spy on the walletService to mock the isUserValid method
        WalletService spyWalletService = spy(walletService);
        doNothing().when(spyWalletService).isUserValid(userId, walletId);

        // Mock the withdraw method to throw NotSufficientBalance exception
        doThrow(new NotSufficientBalance("Insufficient balance")).when(mockWallet).withdraw(amount);

        // Assert that the NotSufficientBalance exception is thrown
        assertThrows(NotSufficientBalance.class, () -> spyWalletService.withdraw(userId, amount, walletId));
    }

}