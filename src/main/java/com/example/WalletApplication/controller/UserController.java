package com.example.WalletApplication.controller;
import com.example.WalletApplication.Exceptions.InvalidUserRegistrationCredentials;
import com.example.WalletApplication.Exceptions.UserAlreadyPresentException;
import com.example.WalletApplication.dto.RegisterRequestDTO;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.enums.CurrencyType;
import com.example.WalletApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        try {
            String username = registerRequestDTO.getUsername();
            String password = registerRequestDTO.getPassword();
            CurrencyType currencyType = CurrencyType.valueOf(registerRequestDTO.getCurrencyType());
            User user = userService.registerUser(username, password,currencyType);
            return ResponseEntity.ok(user);
        } catch (InvalidUserRegistrationCredentials e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }  catch (UserAlreadyPresentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}