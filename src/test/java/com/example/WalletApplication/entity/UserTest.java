package com.example.WalletApplication.entity;

import com.example.WalletApplication.enums.CurrencyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    public void testUserCreation() {
        String userName = "testUserName";
        String password = "testPassword";
        CurrencyType currencyType = CurrencyType.USD;
        assertDoesNotThrow(() -> {
            new User(userName, password, currencyType);
        });
    }

    @Test
    public void testUserCreationExceptionWhenUserNameIsNull() {
        String password = "testPassword";
        CurrencyType currencyType = CurrencyType.USD;
        assertThrows(IllegalArgumentException.class, () -> {
            new User(null, password, currencyType);
        });
    }

    @Test
    public void testUserCreationExceptionWhenUserNameIsEmpty() {
        String userName = "";
        String password = "testPassword";
        CurrencyType currencyType = CurrencyType.USD;
        assertThrows(IllegalArgumentException.class, () -> {
            new User(userName, password, currencyType);
        });
    }

    @Test
    public void testUserCreationExceptionWhenPasswordIsEmpty() {
        String userName = "testUserName";
        String password = "";
        CurrencyType currencyType = CurrencyType.USD;
        assertThrows(IllegalArgumentException.class, () -> {
            new User(userName, password, currencyType);
        });
    }

    @Test
    public void testUserCreationExceptionWhenPasswordIsNull() {
        String userName = "testUserName";
        CurrencyType currencyType = CurrencyType.USD;
        assertThrows(IllegalArgumentException.class, () -> {
            new User(userName, null, currencyType);
        });
    }

    @Test
    public void testUserCreationExceptionWhenAllAreNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User(null, null, null);
        });
    }

    @Test
    public void testUserUpdateNameAndPassword() {
        User user = new User("username", "password", CurrencyType.USD);

        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
    }

    @Test
    public void testTwoUser() {
        String userName = "testUserName";
        String password = "testPassword";
        CurrencyType currencyType = CurrencyType.USD;
        String userName1 = "testUserName1";
        String password1 = "testPassword1";
        CurrencyType currencyType1 = CurrencyType.USD;
        User user = new User(userName, password, currencyType);
        User user2 = new User(userName1, password1, currencyType1);

        assertNotEquals(user, user2);
    }
}