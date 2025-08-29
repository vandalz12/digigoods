package com.example.digigoods.service;

import com.example.digigoods.dto.LoginRequest;
import com.example.digigoods.dto.LoginResponse;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Service for authentication operations.
 */
@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  public AuthService(AuthenticationManager authenticationManager,
                     JwtService jwtService,
                     UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  /**
   * Authenticate user and generate JWT token.
   *
   * @param loginRequest the login request
   * @return login response with JWT token
   * @throws AuthenticationException if authentication fails
   */
  public LoginResponse login(LoginRequest loginRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        )
    );

    User user = userRepository.findByUsername(loginRequest.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    String token = jwtService.generateToken(user.getId(), user.getUsername());

    return new LoginResponse(token, user.getId(), user.getUsername());
  }
}
