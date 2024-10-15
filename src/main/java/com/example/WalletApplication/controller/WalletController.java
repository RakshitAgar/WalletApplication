package com.example.WalletApplication.controller;

import com.example.WalletApplication.dto.TransactionRequestDTO;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("user/{userId}/wallet/{walletId}")
@CrossOrigin
public class WalletController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransactionRequestDTO depositRequest) {
        try {
            Double amount = depositRequest.getAmount();
            walletService.deposit(userId, amount);
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
    public ResponseEntity<?> withdraw(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransactionRequestDTO withdrawRequest) {
        try {
            Double amount = withdrawRequest.getAmount();
            Double withdrawnAmount = walletService.withdraw(userId, amount);
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
}