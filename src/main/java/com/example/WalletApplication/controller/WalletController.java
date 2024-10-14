package com.example.WalletApplication.controller;

import com.example.WalletApplication.dto.TransactionRequestDTO;
import com.example.WalletApplication.dto.TransferTransactionRequestDTO;
import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wallet")
@CrossOrigin
public class WalletController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody TransactionRequestDTO depositRequest) {
        try {
            userService.authenticateUser(depositRequest.getUserId(), depositRequest.getPassword());
            Double amount = depositRequest.getAmount();
            walletService.deposit(depositRequest.getUserId(), amount);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Amount deposited successfully",
                    "amount", amount
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody TransactionRequestDTO withdrawRequest) {
        try {
            userService.authenticateUser(withdrawRequest.getUserId(), withdrawRequest.getPassword());
            Double amount = withdrawRequest.getAmount();
            Double withdrawnAmount = walletService.withdraw(withdrawRequest.getUserId(), amount);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Amount withdrawn successfully",
                    "withdrawnAmount", withdrawnAmount
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferTransactionRequestDTO transferRequest) {
        try {
            userService.authenticateUser(transferRequest.getUserId(), transferRequest.getPassword());
            Double amount = transferRequest.getAmount();
            walletService.transfer(transferRequest.getUserId(), transferRequest.getReceiverId(), amount);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Amount transferred successfully",
                    "amount", amount
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactionHistory(@RequestParam Long userId, @RequestParam String password) {
        try {
            userService.authenticateUser(userId, password);
            List<Transaction> transactions = walletService.getTransactionHistory(userId);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }


}
