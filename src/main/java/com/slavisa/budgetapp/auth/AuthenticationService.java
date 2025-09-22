package com.slavisa.budgetapp.auth;

import com.slavisa.budgetapp.config.JwtService;
import com.slavisa.budgetapp.repository.AccountRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AccountRepo accountRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(AccountRepo accountRepo, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.accountRepo = accountRepo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  request.getUsername(),
                  request.getPassword()
          )
        );
        var user = accountRepo.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
