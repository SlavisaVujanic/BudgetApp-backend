package com.slavisa.budgetapp.config;

import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountID(1);
        account.setUsername("slavisa");
        account.setPassword("password");

        Role role = new Role();
        role.setRoleID(1);
        role.setRoleName("ADMIN");
        account.setRole(role);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternalNoAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalHeaderNotBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic xyz");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalUsernameNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtService.extractUsername("token123")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalTokenValid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtService.extractUsername("token123")).thenReturn("slavisa");
        when(userDetailsService.loadUserByUsername("slavisa")).thenReturn(account);
        when(jwtService.isTokenValid("token123", account)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(account.getUsername(), SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void testDoFilterInternalTokenInvalidDoesNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtService.extractUsername("token123")).thenReturn("slavisa");
        when(userDetailsService.loadUserByUsername("slavisa")).thenReturn(account);
        when(jwtService.isTokenValid("token123", account)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalUsernameNotNullAuthenticationAlreadySet() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtService.extractUsername("token123")).thenReturn("slavisa");

        SecurityContextHolder.getContext().setAuthentication(mock(org.springframework.security.core.Authentication.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

}
