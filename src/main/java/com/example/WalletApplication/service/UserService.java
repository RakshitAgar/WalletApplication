package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.InvalidUserRegistrationCredentials;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.entity.Wallet;
import com.example.WalletApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService{

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String password) {
        if(username.isBlank() || password.isBlank()) {
            throw new InvalidUserRegistrationCredentials("Username and password cannot be empty");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Wallet wallet = new Wallet();

        user.setWallet(wallet);

        return userRepository.save(user);
    }
}
