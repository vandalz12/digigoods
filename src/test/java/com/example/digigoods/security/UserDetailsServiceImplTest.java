package com.example.digigoods.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setPassword("encodedPassword");
  }

  @Test
  @DisplayName("Given existing username, when loadUserByUsername, then return UserDetails")
  void givenExistingUsername_whenLoadUserByUsername_thenReturnUserDetails() {
    // Arrange
    String username = "testuser";
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Assert
    assertNotNull(userDetails);
    assertEquals("testuser", userDetails.getUsername());
    assertEquals("encodedPassword", userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().isEmpty());
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isEnabled());

    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("Given non-existing username, when loadUserByUsername, then throw UsernameNotFoundException")
  void givenNonExistingUsername_whenLoadUserByUsername_thenThrowUsernameNotFoundException() {
    // Arrange
    String username = "nonexistentuser";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(username));

    assertEquals("User not found: nonexistentuser", exception.getMessage());
    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("Given null username, when loadUserByUsername, then throw UsernameNotFoundException")
  void givenNullUsername_whenLoadUserByUsername_thenThrowUsernameNotFoundException() {
    // Arrange
    when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

    // Act & Assert
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(null));

    assertEquals("User not found: null", exception.getMessage());
    verify(userRepository).findByUsername(null);
  }

  @Test
  @DisplayName("Given empty username, when loadUserByUsername, then throw UsernameNotFoundException")
  void givenEmptyUsername_whenLoadUserByUsername_thenThrowUsernameNotFoundException() {
    // Arrange
    String username = "";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(username));

    assertEquals("User not found: ", exception.getMessage());
    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("Given username with spaces, when loadUserByUsername, then handle correctly")
  void givenUsernameWithSpaces_whenLoadUserByUsername_thenHandleCorrectly() {
    // Arrange
    String username = "  testuser  ";
    User userWithSpaces = new User();
    userWithSpaces.setId(2L);
    userWithSpaces.setUsername("  testuser  ");
    userWithSpaces.setPassword("password");

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userWithSpaces));

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Assert
    assertNotNull(userDetails);
    assertEquals("  testuser  ", userDetails.getUsername());
    assertEquals("password", userDetails.getPassword());
    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("Given user with null password, when loadUserByUsername, then throw IllegalArgumentException")
  void givenUserWithNullPassword_whenLoadUserByUsername_thenThrowIllegalArgumentException() {
    // Arrange
    String username = "testuser";
    testUser.setPassword(null);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userDetailsService.loadUserByUsername(username));

    assertEquals("Cannot pass null or empty values to constructor", exception.getMessage());
    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("Given user with empty password, when loadUserByUsername, then return UserDetails with empty password")
  void givenUserWithEmptyPassword_whenLoadUserByUsername_thenReturnUserDetailsWithEmptyPassword() {
    // Arrange
    String username = "testuser";
    testUser.setPassword("");
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Assert
    assertNotNull(userDetails);
    assertEquals("testuser", userDetails.getUsername());
    assertEquals("", userDetails.getPassword());
    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("Given case sensitive username, when loadUserByUsername, then search exactly")
  void givenCaseSensitiveUsername_whenLoadUserByUsername_thenSearchExactly() {
    // Arrange
    String username = "TestUser";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(username));

    assertEquals("User not found: TestUser", exception.getMessage());
    verify(userRepository).findByUsername("TestUser");
  }
}
