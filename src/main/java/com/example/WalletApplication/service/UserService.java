package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.InValidPasswordException;
import com.example.WalletApplication.Exceptions.InvalidUserException;
import com.example.WalletApplication.Exceptions.InvalidUserRegistrationCredentials;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(String username, String password) {
//        if(username == null || username.isBlank() || password == null || password.isBlank()) {
//            throw new InvalidUserRegistrationCredentials("Username and password cannot be empty");
//        }
        return userRepository.save(new User(username, password));
    }

    public void authenticateUser(Long id, String password) {
        User user = userRepository.findById(id).orElseThrow(() -> new InvalidUserException("User not found"));
        if (!user.getPassword().equals(password)) {
            throw new InValidPasswordException("Invalid password");
        }
    }
}