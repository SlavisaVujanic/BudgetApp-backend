package com.slavisa.budgetapp.service;

import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.model.Role;
import com.slavisa.budgetapp.repository.AccountRepo;
import com.slavisa.budgetapp.repository.RoleRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testGetAllAccounts() {
        Account account1 = new Account();
        account1.setFirstName("Ben");

        Account account2 = new Account();
        account2.setFirstName("Dave");

        when(accountRepo.findAll()).thenReturn(Arrays.asList(account1,account2));

        List<Account> accounts = accountService.getAllAccounts();

        assertEquals(2,accounts.size());
        verify(accountRepo).findAll();
    }

    @Test
    void testSuccessfullyGetAccountById() {
        Account account = new Account();
        account.setAccountID(12);
        account.setFirstName("John");

        when(accountRepo.findById(account.getAccountID())).thenReturn(Optional.of(account));

        Optional<Account> acc = accountService.getAccountById(12);

        assertTrue(acc.isPresent());
        assertEquals("John",acc.get().getFirstName());
    }

    @Test
    void testFailedGetAccountById(){
        int accountID = 3;

        when(accountRepo.findById(accountID)).thenReturn(Optional.empty());

        Optional<Account> account = accountService.getAccountById(accountID);

        assertTrue(account.isEmpty());
    }

    @Test
    void testDeleteAccount() {
        Account account = new Account();
        account.setAccountID(43);
        account.setFirstName("Name");

        accountService.deleteAccount(43);

        Mockito.verify(accountRepo, Mockito.times(1)).deleteById(43);
    }

    @Test
    void updateAccountBasicFields() {
        Account existing = new Account();
        existing.setAccountID(1);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("old@mail.com");

        Account input = new Account();
        input.setFirstName("New");
        input.setLastName("User");
        input.setEmail("new@mail.com");

        when(accountRepo.findById(1)).thenReturn(Optional.of(existing));
        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.updateAccount(1,input);

        assertEquals("New", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("new@mail.com", result.getEmail());

        verify(accountRepo).save(existing);
    }

    @Test
    void updateAccountShouldUpdateRole(){
        Account existing = new Account();
        existing.setAccountID(1);

        Role role = new Role();
        role.setRoleID(10);
        role.setRoleName("ADMIN");

        Account input = new Account();
        input.setRole(role);

        when(accountRepo.findById(1)).thenReturn(Optional.of(existing));
        when(roleRepo.findById(10)).thenReturn(Optional.of(role));
        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.updateAccount(1, input);

        assertEquals(role,result.getRole());
        verify(roleRepo).findById(10);
    }

    @Test
    void updateAccountNotUpdateRole() {
        Account existing = new Account();
        existing.setAccountID(1);
        Role oldRole = new Role();
        oldRole.setRoleID(5);
        existing.setRole(oldRole);

        Account input = new Account();

        when(accountRepo.findById(1)).thenReturn(Optional.of(existing));
        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.updateAccount(1, input);

        assertEquals(oldRole, result.getRole());
        verify(roleRepo, never()).findById(Mockito.anyInt());
    }

    @Test
    void updateAccountNotUpdateRoleIsNull() {
        Account existing = new Account();
        existing.setAccountID(1);
        Role oldRole = new Role();
        oldRole.setRoleID(5);
        existing.setRole(oldRole);

        Account input = new Account();
        input.setRole(null);

        when(accountRepo.findById(1)).thenReturn(Optional.of(existing));
        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.updateAccount(1, input);

        assertEquals(oldRole, result.getRole());
        verify(roleRepo, never()).findById(Mockito.anyInt());
    }

    @Test
    void updateAccountNotUpdateRoleWhenRoleIdIsNull() {
        Account existing = new Account();
        existing.setAccountID(1);
        Role oldRole = new Role();
        oldRole.setRoleID(5);
        existing.setRole(oldRole);

        Role inputRole = new Role();
        Account input = new Account();
        input.setRole(inputRole);

        when(accountRepo.findById(1)).thenReturn(Optional.of(existing));
        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.updateAccount(1, input);

        assertEquals(oldRole, result.getRole());
        verify(roleRepo, never()).findById(Mockito.anyInt());
    }



    @Test
    void updateAccountThrowExceptionAccountNotFound() {
        when(accountRepo.findById(99)).thenReturn(Optional.empty());

        Account accountToUpdate = new Account();

        assertThrows(RuntimeException.class,
                () -> accountService.updateAccount(99, accountToUpdate));
    }



    @Test
    void updateAccountUpdatePassword() {
        Account existing = new Account();
        existing.setAccountID(1);

        Account input = new Account();
        input.setPassword("plainPass");

        when(accountRepo.findById(1)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.updateAccount(1, input);

        assertEquals("encodedPass", result.getPassword());
        verify(passwordEncoder).encode("plainPass");
    }

    @Test
    void updateAccountNotUpdatePassword() {
        Account existing = new Account();
        existing.setAccountID(1);
        existing.setPassword("oldPass");

        Account input = new Account();
        input.setPassword("");

        when(accountRepo.findById(1)).thenReturn(Optional.of(existing));
        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.updateAccount(1, input);

        assertEquals("oldPass", result.getPassword());
        verify(passwordEncoder, never()).encode(Mockito.anyString());
    }

    @Test
    void updateAccountThrowExceptionRoleNotFound() {
        Account existing = new Account();
        existing.setAccountID(1);

        Role inputRole = new Role();
        inputRole.setRoleID(99);

        Account input = new Account();
        input.setRole(inputRole);

        when(accountRepo.findById(1)).thenReturn(Optional.of(existing));
        when(roleRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.updateAccount(1, input));

        assertEquals("Role not found", ex.getMessage());
        verify(roleRepo).findById(99);
        verify(accountRepo, never()).save(Mockito.any());
    }



    @Test
    void addAccountRoleIsNull() {
        Account input = new Account();
        input.setPassword("plainPass");
        input.setRole(null);

        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(accountRepo.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.addAccount(input);

        assertEquals("encodedPass", result.getPassword());
        assertNull(result.getRole());
        verify(roleRepo, never()).findById(Mockito.anyInt());
        verify(accountRepo).save(input);
    }

    @Test
    void addAccountSaveWithRole() {
        Account input = new Account();
        input.setPassword("plainPass");

        Role inputRole = new Role();
        inputRole.setRoleID(1);
        input.setRole(inputRole);

        Role dbRole = new Role();
        dbRole.setRoleID(1);
        dbRole.setRoleName("USER");

        when(roleRepo.findById(1)).thenReturn(Optional.of(dbRole));
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.addAccount(input);

        assertEquals("encodedPass", result.getPassword());
        assertEquals(dbRole, result.getRole());
        verify(roleRepo).findById(1);
        verify(accountRepo).save(input);
    }

    @Test
    void addAccountThrowExceptionRoleNotFound() {
        Account input = new Account();
        input.setPassword("plainPass");

        Role inputRole = new Role();
        inputRole.setRoleID(2);
        input.setRole(inputRole);

        when(roleRepo.findById(2)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.addAccount(input));

        assertEquals("Role not found", ex.getMessage());
        verify(accountRepo, never()).save(Mockito.any());
    }

    @Test
    void addAccountSkipRoleIdIsNull() {
        Account input = new Account();
        input.setPassword("plainPassword");

        Role role = new Role();
        input.setRole(role);

        when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.addAccount(input);

        assertSame(role, result.getRole());
        assertNotEquals("plainPassword", result.getPassword());
        verify(roleRepo, never()).findById(Mockito.any());
        verify(accountRepo).save(input);
    }
}