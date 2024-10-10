package com.example.WalletApplication.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    public void testUserCreation() {
        String userName = "testUserName";
        String password = "testPassword";
        Wallet wallet = new Wallet();
        assertDoesNotThrow(() -> {
            new User(userName,password,wallet);
        });
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
        user.setPassword("password");
        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        assertEquals(wallet, user.getWallet());

    }

    @Test
    public void testTwoUser(){
        User user = new User();
        User user2 = new User();

        assertNotEquals(user, user2);
    }

}