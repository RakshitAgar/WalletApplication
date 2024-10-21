package com.example.WalletApplication.controller;

import com.example.WalletApplication.Exceptions.UnAuthorisedUserException;
import com.example.WalletApplication.Exceptions.UnAuthorisedWalletException;
import com.example.WalletApplication.service.TransactionService;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users/{userId}/wallets/{walletId}")
@CrossOrigin
public class TransactionController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private WalletService walletService;


    @GetMapping("/transfers")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false, defaultValue = "asc") String sortByTime) {
        try {
            List<Object> transactions;
            if (type != null) {
                transactions = new ArrayList<>(transactionService.getTransactionHistoryByType(userId,walletId, type));
            } else {
                transactions = transactionService.getTransactionHistory(userId,walletId);
            }
            transactions = transactionService.sortTransactions(transactions, sortByTime);

            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (UnAuthorisedUserException e) {
            return ResponseEntity.status(403).body("User not authorized");
        }catch (UnAuthorisedWalletException e) {
            return ResponseEntity.status(403).body("User not authorized for this Wallet");
        }
    }

    // sort -> date , type , amount
}