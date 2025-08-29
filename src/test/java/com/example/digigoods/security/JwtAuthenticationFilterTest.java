package com.example.digigoods.security;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    userDetails = new User("testuser", "password", new ArrayList<>());
  }

  @Test
  @DisplayName("Given valid JWT token, when doFilterInternal, then set authentication in security context")
  void givenValidJwtToken_whenDoFilterInternal_thenSetAuthenticationInSecurityContext() throws ServletException, IOException {
    // Arrange
    String token = "valid-jwt-token";
    String authHeader = "Bearer " + token;
    String username = "testuser";

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtService.validateToken(token, username)).thenReturn(true);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService).extractUsername(token);
    verify(userDetailsService).loadUserByUsername(username);
    verify(jwtService).validateToken(token, username);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("Given no Authorization header, when doFilterInternal, then skip authentication")
  void givenNoAuthorizationHeader_whenDoFilterInternal_thenSkipAuthentication() throws ServletException, IOException {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn(null);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService, never()).extractUsername(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Given Authorization header without Bearer prefix, when doFilterInternal, then skip authentication")
  void givenAuthorizationHeaderWithoutBearerPrefix_whenDoFilterInternal_thenSkipAuthentication() throws ServletException, IOException {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn("Basic some-token");

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService, never()).extractUsername(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Given expired JWT token, when doFilterInternal, then skip authentication")
  void givenExpiredJwtToken_whenDoFilterInternal_thenSkipAuthentication() throws ServletException, IOException {
    // Arrange
    String token = "expired-jwt-token";
    String authHeader = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUsername(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService).extractUsername(token);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Given malformed JWT token, when doFilterInternal, then skip authentication")
  void givenMalformedJwtToken_whenDoFilterInternal_thenSkipAuthentication() throws ServletException, IOException {
    // Arrange
    String token = "malformed-jwt-token";
    String authHeader = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUsername(token)).thenThrow(new MalformedJwtException("Malformed token"));

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService).extractUsername(token);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Given JWT token with invalid signature, when doFilterInternal, then skip authentication")
  void givenJwtTokenWithInvalidSignature_whenDoFilterInternal_thenSkipAuthentication() throws ServletException, IOException {
    // Arrange
    String token = "invalid-signature-token";
    String authHeader = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUsername(token)).thenThrow(new SignatureException("Invalid signature"));

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService).extractUsername(token);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Given illegal argument in JWT token, when doFilterInternal, then skip authentication")
  void givenIllegalArgumentInJwtToken_whenDoFilterInternal_thenSkipAuthentication() throws ServletException, IOException {
    // Arrange
    String token = "illegal-argument-token";
    String authHeader = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUsername(token)).thenThrow(new IllegalArgumentException("Illegal argument"));

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService).extractUsername(token);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Given invalid JWT token, when doFilterInternal, then skip authentication")
  void givenInvalidJwtToken_whenDoFilterInternal_thenSkipAuthentication() throws ServletException, IOException {
    // Arrange
    String token = "invalid-jwt-token";
    String authHeader = "Bearer " + token;
    String username = "testuser";

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtService.validateToken(token, username)).thenReturn(false);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService).extractUsername(token);
    verify(userDetailsService).loadUserByUsername(username);
    verify(jwtService).validateToken(token, username);
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Given null username from token, when doFilterInternal, then skip authentication")
  void givenNullUsernameFromToken_whenDoFilterInternal_thenSkipAuthentication() throws ServletException, IOException {
    // Arrange
    String token = "token-with-null-username";
    String authHeader = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUsername(token)).thenReturn(null);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService).extractUsername(token);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Given existing authentication in security context, when doFilterInternal, then skip setting new authentication")
  void givenExistingAuthenticationInSecurityContext_whenDoFilterInternal_thenSkipSettingNewAuthentication() throws ServletException, IOException {
    // Arrange
    String token = "valid-jwt-token";
    String authHeader = "Bearer " + token;
    String username = "testuser";

    // Set existing authentication
    SecurityContextHolder.getContext().setAuthentication(
        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            "existinguser", null, new ArrayList<>()));

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUsername(token)).thenReturn(username);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(jwtService).extractUsername(token);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(jwtService, never()).validateToken(anyString(), anyString());
    verify(filterChain).doFilter(request, response);
  }
}
