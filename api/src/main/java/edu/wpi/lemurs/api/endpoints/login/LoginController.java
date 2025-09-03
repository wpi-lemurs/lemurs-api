/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.login;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.security.auth.jwt.JwtResponse;
import edu.wpi.lemurs.api.security.auth.jwt.JwtService;
import edu.wpi.lemurs.api.security.auth.microsoft.AuthMicrosoftService;
import edu.wpi.lemurs.api.security.auth.microsoft.MicrosoftLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private JwtService jwtService;
    private AuthMicrosoftService authMicrosoftService;

    /** Autowires the {@link LoginController}. */
    @Autowired
    public LoginController(
        JwtService jwtService,
        AuthMicrosoftService authMicrosoftService
    ) {
        this.jwtService = jwtService;
        this.authMicrosoftService = authMicrosoftService;
    }

    /**
     * The {@code /auth/login} {@code POST} endpoint receives user credentials and returns a JWT
     * token.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<JwtResponse> loginUserAccount(
        @RequestBody MicrosoftLoginDto microsoftLoginDto
    ) {
        try {
            Authentication tempAuthentication = authMicrosoftService.login(
                microsoftLoginDto.getAccessToken()
            );

            JwtResponse jwtAuthResponse = jwtService.getJwtResponse(
                tempAuthentication
            );

            return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
        } catch (BadCredentialsException | UnauthenticatedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
