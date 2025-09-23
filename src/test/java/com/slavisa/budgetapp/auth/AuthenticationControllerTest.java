package com.slavisa.budgetapp.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {


    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void testLoginSuccessfully() {
        AuthenticationRequest request = new AuthenticationRequest("slavisa", "password");
        AuthenticationResponse mockResponse = AuthenticationResponse.builder()
                .token("mocked-jwt-token")
                .build();

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(mockResponse);

        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.login(request);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals("mocked-jwt-token", responseEntity.getBody().getToken());

        ArgumentCaptor<AuthenticationRequest> captor = ArgumentCaptor.forClass(AuthenticationRequest.class);
        verify(authenticationService).authenticate(captor.capture());
        AuthenticationRequest captured = captor.getValue();
        assertEquals("slavisa", captured.getUsername());
        assertEquals("password", captured.getPassword());
    }

    @Test
    void testLoginServiceThrowsException() {
        AuthenticationRequest request = new AuthenticationRequest("user", "wrongpassword");
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationController.login(request));
        assertEquals("Authentication failed", exception.getMessage());

        verify(authenticationService).authenticate(request);
    }
}
