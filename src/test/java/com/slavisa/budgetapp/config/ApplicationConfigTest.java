package com.slavisa.budgetapp.config;

import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.repository.AccountRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private AccountRepo accountRepo;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void testUserDetailsService() {
        Account account = new Account();
        account.setUsername("slavisa");

        when(accountRepo.findByUsername("slavisa")).thenReturn(Optional.of(account));

        UserDetails userDetails = applicationConfig.userDetailsService().loadUserByUsername("slavisa");

        assertNotNull(userDetails);
        assertEquals("slavisa", userDetails.getUsername());
    }

    @Test
    void testUserDetailsServiceNotFound() {
        when(accountRepo.findByUsername("unknown")).thenReturn(Optional.empty());
        var userDetailsService = applicationConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("rawPassword", encoder.encode("rawPassword")));
    }

    @Test
    void testAuthenticationProvider() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserDetailsService userDetailsService = username ->
                User.withUsername("testUser")
                        .password(passwordEncoder.encode("testPass"))
                        .roles("USER")
                        .build();

        AuthenticationProvider provider = applicationConfig.authenticationProvider(userDetailsService);

        assertNotNull(provider);
        assertInstanceOf(DaoAuthenticationProvider.class, provider);


        Authentication authRequest =
                new UsernamePasswordAuthenticationToken("testUser", "testPass");

        Authentication authResult = provider.authenticate(authRequest);

        assertNotNull(authResult);
        assertTrue(authResult.isAuthenticated());
        assertEquals("testUser", authResult.getName());
    }

    @Test
    void testAuthenticationManager() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result = applicationConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(result);
        assertEquals(authenticationManager, result);
    }
}
