package com.example.WalletApplication.controller;

import com.example.WalletApplication.dto.RegisterRequestDTO;
import com.example.WalletApplication.dto.TransactionRequestDTO;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.service.UserService;
import com.example.WalletApplication.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registrationRequest) {
        try {
            User registerUser = userService.registerUser(registrationRequest.getUsername(), registrationRequest.getPassword());
            return ResponseEntity.ok(registerUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long id, @RequestBody TransactionRequestDTO depositRequest) {
        try {
            userService.authenticateUser(id, depositRequest.getPassword());
            Double amount = depositRequest.getAmount();
            walletService.deposit(id, amount);
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

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody TransactionRequestDTO withdrawRequest) {
        try {
            userService.authenticateUser(id, withdrawRequest.getPassword());
            Double amount = withdrawRequest.getAmount();
            Double withdrawnAmount = walletService.withdraw(id, amount);
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