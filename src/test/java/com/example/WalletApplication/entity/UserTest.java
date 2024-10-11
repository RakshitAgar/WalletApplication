package com.example.WalletApplication.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    public void testUserCreation() {
        String userName = "testUserName";
        String password = "testPassword";
        assertDoesNotThrow(() -> {
            new User(userName,password);
        });
    }

    @Test
    public void testUserCreationExceptionWhenUserNameIsNull() {
        String password = "testPassword";
        assertThrows(IllegalArgumentException.class, () -> {
            new User(null,password);
        });
    }

    @Test
    public void testUserCreationExceptionWhenUserNameIsEmpty() {
        String userName = "";
        String password = "testPassword";
        assertThrows(IllegalArgumentException.class, () -> {
            new User(userName,password);
        });
    }

    @Test
    public void testUserCreationExceptionWhenPasswordIsEmpty() {
        String userName = "testUserName";
        String password = "";
        assertThrows(IllegalArgumentException.class, () -> {
            new User(userName,password);
        });
    }

    @Test
    public void testUserCreationExceptionWhenPasswordIsNull() {
        String userName = "testUserName";
        assertThrows(IllegalArgumentException.class, () -> {
            new User(userName, null);
        });
    }

    @Test
    public void testUserCreationExceptionWhenAllAreNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User(null, null);
        });
    }


    @Test
    public void testUserUpdateNameAndPassword() {
        User user = new User("username","password");

        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
    }

    @Test
    public void testTwoUser(){
        String userName = "testUserName";
        String password = "testPassword";
        String userName1 = "testUserName1";
        String password1 = "testPassword1";
        User user = new User(userName,password);
        User user2 = new User(userName1,password1);

        assertNotEquals(user, user2);
    }

}