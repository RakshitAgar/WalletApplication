package com.example.WalletApplication.controller;

import com.example.WalletApplication.dto.RegisterRequestDTO;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

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
}