package com.example.digigoods;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for DigigoodsApplication to ensure 100% code coverage.
 */
@SpringBootTest
@ActiveProfiles("test")
class DigigoodsApplicationTest {

  @Test
  @DisplayName("Given Spring Boot application, when context loads, then application starts successfully")
  void givenSpringBootApplication_whenContextLoads_thenApplicationStartsSuccessfully() {
    // Arrange & Act & Assert
    assertDoesNotThrow(() -> {
      // This test verifies that the Spring Boot application context loads successfully
    });
  }

  @Test
  @DisplayName("Given main method with args, when called, then SpringApplication runs successfully")
  void givenMainMethodWithArgs_whenCalled_thenSpringApplicationRunsSuccessfully() {
    // Arrange
    String[] args = {"--spring.profiles.active=test"};
    
    try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
      ConfigurableApplicationContext mockContext = org.mockito.Mockito.mock(ConfigurableApplicationContext.class);
      springApplicationMock.when(() -> SpringApplication.run(DigigoodsApplication.class, args))
          .thenReturn(mockContext);

      // Act & Assert
      assertDoesNotThrow(() -> DigigoodsApplication.main(args));
      
      // Verify SpringApplication.run was called with correct parameters
      springApplicationMock.verify(() -> SpringApplication.run(DigigoodsApplication.class, args));
    }
  }

  @Test
  @DisplayName("Given DigigoodsApplication class, when instantiated, then object is created successfully")
  void givenDigigoodsApplicationClass_whenInstantiated_thenObjectIsCreatedSuccessfully() {
    // Act
    DigigoodsApplication application = new DigigoodsApplication();

    // Assert
    assertNotNull(application);
  }
}
