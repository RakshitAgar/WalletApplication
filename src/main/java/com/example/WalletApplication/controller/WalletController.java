package com.example.WalletApplication.controller;

import com.example.WalletApplication.Exceptions.*;
import com.example.WalletApplication.dto.TransactionRequestDTO;
import com.example.WalletApplication.dto.TransferTransactionRequestDTO;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("users/{userId}/wallets/{walletId}")
@CrossOrigin
public class WalletController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/deposits")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deposit(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransactionRequestDTO depositRequest) {
        try {

            Double amount = depositRequest.getAmount();
            walletService.deposit(userId, amount,walletId);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Amount deposited successfully",
                    "amount", amount
            ));
        } catch (InvalidAmount e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UnAuthorisedUserException e) {
            return ResponseEntity.status(403).body("User not authorized");
        } catch (UnAuthorisedWalletException e) {
            return ResponseEntity.status(403).body("User not authorized for this Wallet");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(400).body("User Not Found");
        }
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<?> withdraw(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransactionRequestDTO withdrawRequest) {
        try {
            Double amount = withdrawRequest.getAmount();
            Double withdrawnAmount = walletService.withdraw(userId, amount,walletId);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Amount withdrawn successfully",
                    "withdrawnAmount", withdrawnAmount
            ));
        } catch (InvalidAmount e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UnAuthorisedUserException e) {
            return ResponseEntity.status(403).body("User not authorized");
        } catch (UnAuthorisedWalletException e) {
            return ResponseEntity.status(403).body("User not authorized for this Wallet");
        } catch (NotSufficientBalance e) {
            return ResponseEntity.status(400).body("Insufficient balance");
        }
    }


    @PostMapping("/transfers")
    public ResponseEntity<?> transfer(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransferTransactionRequestDTO transferRequest) {
        try {
            Double amount = transferRequest.getAmount();
            walletService.transfer(userId, transferRequest.getReceiverId(), amount,walletId);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Amount transferred successfully",
                    "amount", amount
            ));
        } catch (UnAuthorisedUserException e) {
            return ResponseEntity.status(403).body("User not authorized");
        } catch (UnAuthorisedWalletException e) {
            return ResponseEntity.status(403).body("User not authorized for this Wallet");
        } catch (NotSufficientBalance e) {
            return ResponseEntity.status(400).body("Insufficient balance");
        }catch (ReceiverNotFoundException e) {
            return ResponseEntity.status(404).body("Receiver not found");
        }
    }

}