package com.example.WalletApplication.service;

import com.example.WalletApplication.Exceptions.InvalidUserRegistrationCredentials;
import com.example.WalletApplication.entity.User;
import com.example.WalletApplication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void testUserRegistration() {
        UserRepository userRepository = mock(UserRepository.class);
        assertDoesNotThrow(() -> {
            new UserService(userRepository);
        });
    }
    @Test
    public void testRegisterUserWithLocalDbAndAutowired() {
        String username = "realUser";
        String password = "realPassword";

        // This uses the real database (H2) and the real repository via Autowired
        User result = userService.registerUser(username, password);

        // Verify the user was saved in the real local database (H2)
        User savedUser = userRepository.findById(result.getId()).orElse(null);
        assertNotNull(savedUser);
        assertEquals(username, savedUser.getUsername());
        assertEquals(password, savedUser.getPassword());
    }

    @Test
    public void testRegisterUserExceptionWithSameUsernameAgain() {
        String username = "realUser";
        String password = "realPassword";

        User result = userService.registerUser(username, password);

        // Verify the user was saved in the real local database (H2)
        Optional<User> savedUser = userRepository.findByUsername(username);
        assertNotNull(savedUser);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.registerUser(username, password);
        });
    }

    @Test
    void testRegisterUserWhenUserNameIsEmpty() {
        UserRepository userRepository = mock(UserRepository.class);

        UserService userService = new UserService(userRepository);
        assertThrows(InvalidUserRegistrationCredentials.class, () -> {
            userService.registerUser("","Password");
        });
    }

    @Test
    void testRegisterUserWhenPasswordIsEmpty() {
        UserRepository userRepository = mock(UserRepository.class);

        UserService userService = new UserService(userRepository);
        assertThrows(InvalidUserRegistrationCredentials.class, () -> {
            userService.registerUser("username","");
        });
    }

    @Test
    void testRegisterUserWhenPasswordAndUserNameIsEmpty() {
        UserRepository userRepository = mock(UserRepository.class);

        UserService userService = new UserService(userRepository);
        assertThrows(InvalidUserRegistrationCredentials.class, () -> {
            userService.registerUser("","");
        });
    }


    @Test
    void testRegisterUser() {
        // Create the mock
        UserRepository userRepository = mock(UserRepository.class);

        // Create the service with the mock
        UserService userService = new UserService(userRepository);

        String username = "testUser";
        String password = "testPassword";

        User savedUser = new User(username,password);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(username, password);

        verify(userRepository,times(1)).save(any(User.class));
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
    }



}