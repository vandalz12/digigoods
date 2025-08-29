package com.example.digigoods.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.digigoods.dto.LoginRequest;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    // Set up MockMvc
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // Clear and set up test data
    // Create test user
    User testUser = new User();
    testUser.setUsername("testuser");
    testUser.setPassword(passwordEncoder.encode("password"));
    userRepository.save(testUser);
  }

  @Test
  @DisplayName("Given valid credentials, when logging in, then return JWT token")
  void givenValidCredentials_whenLoggingIn_thenReturnJwtToken() throws Exception {
    // Arrange
    LoginRequest loginRequest = new LoginRequest("testuser", "password");

    // Act & Assert
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.type").value("Bearer"))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.userId").exists());
  }

  @Test
  @DisplayName("Given invalid credentials, when logging in, then return unauthorized")
  void givenInvalidCredentials_whenLoggingIn_thenReturnUnauthorized() throws Exception {
    // Arrange
    LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");

    // Act & Assert
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid username or password"));
  }

  @Test
  @DisplayName("Given missing username, when logging in, then return bad request")
  void givenMissingUsername_whenLoggingIn_thenReturnBadRequest() throws Exception {
    // Arrange
    LoginRequest loginRequest = new LoginRequest("", "password");

    // Act & Assert
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username is required"));
  }

  @Test
  @DisplayName("Given missing password, when logging in, then return bad request")
  void givenMissingPassword_whenLoggingIn_thenReturnBadRequest() throws Exception {
    // Arrange
    LoginRequest loginRequest = new LoginRequest("testuser", "");

    // Act & Assert
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Password is required"));
  }
}
