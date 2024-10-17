package com.example.WalletApplication.controller;

import com.example.WalletApplication.Exceptions.UnAuthorisedUserException;
import com.example.WalletApplication.Exceptions.UnAuthorisedWalletException;
import com.example.WalletApplication.dto.TransferTransactionRequestDTO;
import com.example.WalletApplication.service.TransactionService;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user/{userId}/wallet/{walletId}")
@CrossOrigin
public class TransactionController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private WalletService walletService;

    @PostMapping("/transfers")
    public ResponseEntity<?> transfer(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransferTransactionRequestDTO transferRequest) {
        try {
            walletService.isUserAuthorized(userId, walletId);
            Double amount = transferRequest.getAmount();
            transactionService.transfer(userId, transferRequest.getReceiverId(), amount);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Amount transferred successfully",
                    "amount", amount
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UnAuthorisedUserException e) {
            return ResponseEntity.status(403).body("User not authorized");
        }catch (UnAuthorisedWalletException e) {
            return ResponseEntity.status(403).body("User not authorized for this Wallet");
        }
    }

    @GetMapping("/transfers")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestParam(required = false) List<String> type) {
        try {
            walletService.isUserAuthorized(userId, walletId);

            List<Object> transactions;
            if (type != null) {
                transactions = new ArrayList<>(transactionService.getTransactionHistoryByType(walletId, type));
            } else {
                transactions = transactionService.getTransactionHistory(walletId);
            }

            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UnAuthorisedUserException e) {
            return ResponseEntity.status(403).body("User not authorized");
        }catch (UnAuthorisedWalletException e) {
            return ResponseEntity.status(403).body("User not authorized for this Wallet");
        }
    }

    // sort -> date , type , amount
}