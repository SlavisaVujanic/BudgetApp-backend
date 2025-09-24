package com.slavisa.budgetapp.config;

import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private Account account;
    private String secret;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        secret = Base64.getEncoder().encodeToString("supersecretkeysupersecretkey123456".getBytes());
        ReflectionTestUtils.setField(jwtService, "secret", secret);

        account = new Account();
        account.setAccountID(1);
        account.setUsername("slavisa");
        account.setPassword("password");

        Role role = new Role();
        role.setRoleID(1);
        role.setRoleName("ADMIN");
        account.setRole(role);
    }

    @Test
    void testGenerateAndExtractUsername() {
        String token = jwtService.generateToken(account);

        String username = jwtService.extractUsername(token);

        assertEquals("slavisa", username);
    }

    @Test
    void testIsTokenValidWithValidToken() {
        String token = jwtService.generateToken(account);

        boolean valid = jwtService.isTokenValid(token, account);

        assertTrue(valid, "Token should be valid for the given account");
    }

    @Test
    void testIsTokenValidWrongUser() {
        String token = jwtService.generateToken(account);

        Account anotherAccount = new Account();
        anotherAccount.setAccountID(2);
        anotherAccount.setUsername("otheruser");
        anotherAccount.setPassword("password");

        Role role = new Role();
        role.setRoleID(2);
        role.setRoleName("USER");
        anotherAccount.setRole(role);

        boolean valid = jwtService.isTokenValid(token, anotherAccount);

        assertFalse(valid, "Token should be invalid for another user");
    }

    @Test
    void testIsTokenValidExpiredToken() {
        String expiredToken = createExpiredToken();

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, account), "Expired token should throw ExpiredJwtException");
    }

    @Test
    void testIsTokenExpiredBehaviorCheck() {
        String validToken = jwtService.generateToken(account);

        assertTrue(jwtService.isTokenValid(validToken, account));

        Date expiration = jwtService.extractClaim(validToken, Claims::getExpiration);
        assertTrue(expiration.after(new Date()), "Valid token should not be expired");
    }

    @Test
    void testIsTokenValidExpiredTokenAndWrongUser() {
        String expiredToken = createExpiredToken();

        Account anotherAccount = new Account();
        anotherAccount.setAccountID(2);
        anotherAccount.setUsername("otheruser");
        anotherAccount.setPassword("password");

        Role role = new Role();
        role.setRoleID(2);
        role.setRoleName("USER");
        anotherAccount.setRole(role);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, anotherAccount), "Expired token should throw ExpiredJwtException regardless of user");
    }

    @Test
    void testTokenExpiration() {
        String token = jwtService.generateToken(account);

        assertTrue(jwtService.isTokenValid(token, account));

        Date exp = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(exp);
        assertTrue(exp.after(new Date()), "Expiration should be in the future");
    }

    @Test
    void testExtractClaimsFromExpiredToken() {
        String expiredToken = createExpiredToken();

        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsername(expiredToken));
    }

    @Test
    void testExtractCustomClaims() {
        String token = jwtService.generateToken(account);

        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ADMIN", role);

        Integer accountID = jwtService.extractClaim(token, claims -> claims.get("accountID", Integer.class));
        assertEquals(1, accountID);
    }

    private String createExpiredToken() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(account.getUsername())
                .claim("role", account.getAuthorities().iterator().next().getAuthority())
                .claim("accountID", account.getAccountID())
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 120))
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }
}