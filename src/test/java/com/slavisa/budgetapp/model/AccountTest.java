package com.slavisa.budgetapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setUsername("slavisa");
        account.setPassword("password123");
    }

    @Test
    void testAuthoritiesWithRole() {
        Role role = new Role();
        role.setRoleName("ADMIN");
        account.setRole(role);

        Collection<? extends GrantedAuthority> authorities = account.getAuthorities();

        assertEquals(1, authorities.size());
        assertEquals("ADMIN", authorities.iterator().next().getAuthority());
    }

    @Test
    void testAuthoritiesWithoutRole() {
        account.setRole(null);

        Collection<? extends GrantedAuthority> authorities = account.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(account.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(account.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(account.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(account.isEnabled());
    }
}
