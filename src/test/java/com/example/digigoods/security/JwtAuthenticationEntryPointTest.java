package com.example.digigoods.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationEntryPoint Tests")
class JwtAuthenticationEntryPointTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private AuthenticationException authException;

  @Mock
  private PrintWriter printWriter;

  @InjectMocks
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @BeforeEach
  void setUp() throws IOException {
    when(response.getWriter()).thenReturn(printWriter);
  }

  @Test
  @DisplayName("Given authentication exception, when commence, then send unauthorized error")
  void givenAuthenticationException_whenCommence_thenSendUnauthorizedError() throws ServletException, IOException {
    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }

  @Test
  @DisplayName("Given null authentication exception, when commence, then send unauthorized error")
  void givenNullAuthenticationException_whenCommence_thenSendUnauthorizedError() throws ServletException, IOException {
    // Act
    jwtAuthenticationEntryPoint.commence(request, response, null);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }

  @Test
  @DisplayName("Given authentication exception with null message, when commence, then send unauthorized error")
  void givenAuthenticationExceptionWithNullMessage_whenCommence_thenSendUnauthorizedError() throws ServletException, IOException {
    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }

  @Test
  @DisplayName("Given authentication exception with empty message, when commence, then send unauthorized error")
  void givenAuthenticationExceptionWithEmptyMessage_whenCommence_thenSendUnauthorizedError() throws ServletException, IOException {
    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }

  @Test
  @DisplayName("Given authentication exception with custom message, when commence, then send unauthorized error")
  void givenAuthenticationExceptionWithCustomMessage_whenCommence_thenSendUnauthorizedError() throws ServletException, IOException {
    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }

  @Test
  @DisplayName("Given IOException when getting writer, when commence, then throw IOException")
  void givenIOExceptionWhenGettingWriter_whenCommence_thenThrowIOException() throws ServletException, IOException {
    // Arrange
    when(response.getWriter()).thenThrow(new IOException("Writer error"));

    // Act & Assert
    assertThrows(IOException.class, () ->
        jwtAuthenticationEntryPoint.commence(request, response, authException));

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }

  @Test
  @DisplayName("Given request with different paths, when commence, then always send unauthorized")
  void givenRequestWithDifferentPaths_whenCommence_thenAlwaysSendUnauthorized() throws ServletException, IOException {
    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }

  @Test
  @DisplayName("Given request with query parameters, when commence, then send unauthorized error")
  void givenRequestWithQueryParameters_whenCommence_thenSendUnauthorizedError() throws ServletException, IOException {
    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }

  @Test
  @DisplayName("Given request with headers, when commence, then send unauthorized error")
  void givenRequestWithHeaders_whenCommence_thenSendUnauthorizedError() throws ServletException, IOException {
    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).getWriter();
  }
}
