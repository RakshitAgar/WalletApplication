package com.example.WalletApplication.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    public void testUserCreation() {
        assertDoesNotThrow(() -> {
            new User();
        });
    }

    @Test
    public void testUserUpdateName() {
        User user = new User();
        user.setUsername("username");

        assertEquals("username", user.getUsername());
    }

    @Test
    public void testUserUpdateNameAndPassword() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");

        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
    }

    @Test
    public void testUserSetWallet() {
        User user = new User();
        user.setUsername("username");
        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        assertEquals(wallet, user.getWallet());

    }

    @Test
    public void testTwoUser(){
        User user = new User();
        User user2 = new User();

        assertEquals(user, user2);
    }

}