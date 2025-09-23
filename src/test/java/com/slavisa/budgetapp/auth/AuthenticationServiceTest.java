package com.slavisa.budgetapp.auth;

import com.slavisa.budgetapp.config.JwtService;
import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.model.Role;
import com.slavisa.budgetapp.repository.AccountRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testSuccessfullyAuthenticate() {
        AuthenticationRequest request = new AuthenticationRequest("slavisa", "password");

        Account account = new Account();
        account.setUsername("slavisa");
        Role role = new Role();
        role.setRoleID(1);
        role.setRoleName("ADMIN");
        account.setRole(role);

        when(accountRepo.findByUsername("slavisa")).thenReturn(Optional.of(account));
        when(jwtService.generateToken(account)).thenReturn("mocked-jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());

        UsernamePasswordAuthenticationToken captured = authCaptor.getValue();
        assertEquals("slavisa", captured.getPrincipal());
        assertEquals("password", captured.getCredentials());

        verify(jwtService).generateToken(account);
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
    }

    @Test
    void testAuthenticateUserNotFound() {
        AuthenticationRequest request = new AuthenticationRequest("unknown", "password");

        when(accountRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> authenticationService.authenticate(request));

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor
                .forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());

        UsernamePasswordAuthenticationToken captured = authCaptor.getValue();
        assertEquals("unknown", captured.getPrincipal());
        assertEquals("password", captured.getCredentials());
    }

    @Test
    void testAuthenticateAuthenticationFails() {
        AuthenticationRequest request = new AuthenticationRequest("slavisa", "wrongpassword");

        doThrow(new AuthenticationException("Bad credentials") {}).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(request));

        verify(accountRepo, never()).findByUsername(anyString());
        verify(jwtService, never()).generateToken(any(Account.class));
    }
}
