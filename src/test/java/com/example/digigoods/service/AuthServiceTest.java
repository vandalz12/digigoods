package com.example.digigoods.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.dto.LoginRequest;
import com.example.digigoods.dto.LoginResponse;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AuthService authService;

  private User testUser;
  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setPassword("password");

    loginRequest = new LoginRequest();
    loginRequest.setUsername("testuser");
    loginRequest.setPassword("password");
  }

  @Test
  @DisplayName("Given valid credentials, when login, then return login response with token")
  void givenValidCredentials_whenLogin_thenReturnLoginResponseWithToken() {
    // Arrange
    String expectedToken = "jwt-token";
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(jwtService.generateToken(1L, "testuser")).thenReturn(expectedToken);

    // Act
    LoginResponse response = authService.login(loginRequest);

    // Assert
    assertNotNull(response);
    assertEquals(expectedToken, response.getToken());
    assertEquals(1L, response.getUserId());
    assertEquals("testuser", response.getUsername());
    assertEquals("Bearer", response.getType());

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userRepository).findByUsername("testuser");
    verify(jwtService).generateToken(1L, "testuser");
  }

  @Test
  @DisplayName("Given invalid credentials, when login, then throw BadCredentialsException")
  void givenInvalidCredentials_whenLogin_thenThrowBadCredentialsException() {
    // Arrange
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @DisplayName("Given valid credentials but user not found, when login, then throw RuntimeException")
  void givenValidCredentialsButUserNotFound_whenLogin_thenThrowRuntimeException() {
    // Arrange
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, 
        () -> authService.login(loginRequest));
    assertEquals("User not found", exception.getMessage());

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userRepository).findByUsername("testuser");
  }

  @Test
  @DisplayName("Given null login request, when login, then throw exception")
  void givenNullLoginRequest_whenLogin_thenThrowException() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> authService.login(null));
  }

  @Test
  @DisplayName("Given login request with null username, when login, then authentication fails")
  void givenLoginRequestWithNullUsername_whenLogin_thenAuthenticationFails() {
    // Arrange
    loginRequest.setUsername(null);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
  }

  @Test
  @DisplayName("Given login request with null password, when login, then authentication fails")
  void givenLoginRequestWithNullPassword_whenLogin_thenAuthenticationFails() {
    // Arrange
    loginRequest.setPassword(null);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
  }
}
