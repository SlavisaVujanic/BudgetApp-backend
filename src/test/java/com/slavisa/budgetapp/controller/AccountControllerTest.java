package com.slavisa.budgetapp.controller;

import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setAccountID(1);
        testAccount.setFirstName("John");
        testAccount.setLastName("Doe");
        testAccount.setUsername("johndoe");
        testAccount.setEmail("john@example.com");
        testAccount.setPassword("password123");
    }

    @Test
    void testGetAllAccounts() {
        Account secondAccount = new Account();
        secondAccount.setAccountID(2);
        secondAccount.setFirstName("Jane");
        secondAccount.setLastName("Smith");
        secondAccount.setUsername("janesmith");
        secondAccount.setEmail("jane@example.com");

        when(accountService.getAllAccounts()).thenReturn(List.of(testAccount, secondAccount));

        List<Account> result = accountController.getAllAccounts();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        assertEquals("johndoe", result.get(0).getUsername());
        assertEquals("janesmith", result.get(1).getUsername());
        verify(accountService).getAllAccounts();
    }

    @Test
    void testGetAllAccountsEmptyList() {
        when(accountService.getAllAccounts()).thenReturn(List.of());

        List<Account> result = accountController.getAllAccounts();

        assertTrue(result.isEmpty());
        verify(accountService).getAllAccounts();
    }

    @Test
    void testSuccessfullyGetAccountByID() {
        when(accountService.getAccountById(1)).thenReturn(Optional.of(testAccount));

        Optional<Account> result = accountController.getAccountByID(1);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals("johndoe", result.get().getUsername());
        assertEquals("john@example.com", result.get().getEmail());
        assertEquals(1, result.get().getAccountID());
        verify(accountService).getAccountById(1);
    }

    @Test
    void testFailedGetAccountByID() {
        when(accountService.getAccountById(999)).thenReturn(Optional.empty());

        Optional<Account> result = accountController.getAccountByID(999);

        assertFalse(result.isPresent());
        verify(accountService).getAccountById(999);
    }

    @Test
    void testDeleteAccountByID() {
        accountController.deleteAccountByID(1);

        verify(accountService).deleteAccount(1);
    }

    @Test
    void testAddAccount() {
        Account newAccount = new Account();
        newAccount.setFirstName("Alice");
        newAccount.setLastName("Johnson");
        newAccount.setUsername("alicejohnson");
        newAccount.setEmail("alice@example.com");
        newAccount.setPassword("password456");

        Account savedAccount = new Account();
        savedAccount.setAccountID(3);
        savedAccount.setFirstName("Alice");
        savedAccount.setLastName("Johnson");
        savedAccount.setUsername("alicejohnson");
        savedAccount.setEmail("alice@example.com");
        savedAccount.setPassword("password456");

        when(accountService.addAccount(any(Account.class))).thenReturn(savedAccount);

        Account result = accountController.addAccount(newAccount);

        assertEquals(3, result.getAccountID());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Johnson", result.getLastName());
        assertEquals("alicejohnson", result.getUsername());
        assertEquals("alice@example.com", result.getEmail());
        verify(accountService).addAccount(newAccount);
    }

    @Test
    void testUpdateAccount() {
        Account updateAccount = new Account();
        updateAccount.setFirstName("John");
        updateAccount.setLastName("Updated");
        updateAccount.setUsername("johnupdated");
        updateAccount.setEmail("john.updated@example.com");

        Account updatedAccount = new Account();
        updatedAccount.setAccountID(1);
        updatedAccount.setFirstName("John");
        updatedAccount.setLastName("Updated");
        updatedAccount.setUsername("johnupdated");
        updatedAccount.setEmail("john.updated@example.com");

        when(accountService.updateAccount(anyInt(), any(Account.class))).thenReturn(updatedAccount);

        Account result = accountController.updateAccount(1, updateAccount);

        assertEquals(1, result.getAccountID());
        assertEquals("John", result.getFirstName());
        assertEquals("Updated", result.getLastName());
        assertEquals("johnupdated", result.getUsername());
        assertEquals("john.updated@example.com", result.getEmail());
        verify(accountService).updateAccount(1, updateAccount);
    }
}