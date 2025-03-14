package br.com.vss.resell_platform.integration.controller;

import br.com.vss.resell_platform.controller.dto.RegisterRequest;
import br.com.vss.resell_platform.integration.BaseIntegrationTest;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.UserRepository;
import br.com.vss.resell_platform.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // Clean up repository
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /register")
    class RegisterTests {

        @Test
        @DisplayName("Should register a new user successfully")
        void shouldRegisterUser() throws Exception {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "newuser", 
                    "password123", 
                    "newuser@example.com"
            );

            // When
            ResultActions result = mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)));

            // Then
            result.andExpect(status().isOk());

            // Verify the user was created in the database
            User savedUser = userRepository.findByUsername("newuser").orElse(null);
            assertNotNull(savedUser);
            assertEquals("newuser@example.com", savedUser.getEmail());
            assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        }

        @Test
        @DisplayName("Should return error when username is already taken")
        void shouldReturnErrorWhenUsernameExists() throws Exception {
            // Given
            User existingUser = TestDataFactory.createTestUser();
            existingUser.setPassword(passwordEncoder.encode(TestDataFactory.DEFAULT_PASSWORD));
            userRepository.save(existingUser);

            RegisterRequest registerRequest = new RegisterRequest(
                    existingUser.getUsername(),  // Same username
                    "password123", 
                    "different@example.com"
            );

            // When
            ResultActions result = mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)));

            // Then
            result.andExpect(status().isUnprocessableEntity());

            // Verify no new user was created
            assertEquals(1, userRepository.count());
        }

        @Test
        @DisplayName("Should return error when email is already taken")
        void shouldReturnErrorWhenEmailExists() throws Exception {
            // Given
            User existingUser = TestDataFactory.createTestUser();
            existingUser.setPassword(passwordEncoder.encode(TestDataFactory.DEFAULT_PASSWORD));
            userRepository.save(existingUser);

            RegisterRequest registerRequest = new RegisterRequest(
                    "differentuser",
                    "password123", 
                    existingUser.getEmail()  // Same email
            );

            // When
            ResultActions result = mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)));

            // Then
            result.andExpect(status().isUnprocessableEntity());

            // Verify no new user was created
            assertEquals(1, userRepository.count());
        }

        @Test
        @DisplayName("Should return error for empty username")
        void shouldReturnErrorForEmptyUsername() throws Exception {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "",  // Empty username
                    "password123", 
                    "validemail@example.com"
            );

            // When
            ResultActions result = mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)));

            // Then
            result.andExpect(status().isUnprocessableEntity());

            // Verify no user was created
            assertEquals(0, userRepository.count());
        }

        @Test
        @DisplayName("Should return error for invalid email format")
        void shouldReturnErrorForInvalidEmail() throws Exception {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "validuser",
                    "password123", 
                    "invalid-email"  // Invalid email format
            );

            // When
            ResultActions result = mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)));

            // Then
            result.andExpect(status().isUnprocessableEntity());

            // Verify no user was created
            assertEquals(0, userRepository.count());
        }
    }

    @Nested
    @DisplayName("POST /authenticate")
    class AuthenticateTests {

        @Test
        @DisplayName("Should authenticate user and return token")
        void shouldAuthenticateUser() throws Exception {
            // Given
            User user = TestDataFactory.createTestUser();
            user.setPassword(passwordEncoder.encode(TestDataFactory.DEFAULT_PASSWORD));
            userRepository.save(user);

            String authRequest = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\"}",
                    user.getUsername(), TestDataFactory.DEFAULT_PASSWORD);

            // When
            ResultActions result = mockMvc.perform(post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(authRequest));

            // Then
            result.andExpect(status().isOk());
            // Note: In a real test, we might also want to verify the token structure
        }

        @Test
        @DisplayName("Should return unauthorized for invalid password")
        void shouldReturnUnauthorizedForInvalidPassword() throws Exception {
            // Given
            User user = TestDataFactory.createTestUser();
            user.setPassword(passwordEncoder.encode(TestDataFactory.DEFAULT_PASSWORD));
            userRepository.save(user);

            String authRequest = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\"}",
                    user.getUsername(), "wrong_password");

            // When
            ResultActions result = mockMvc.perform(post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(authRequest));

            // Then
            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return unauthorized for non-existent user")
        void shouldReturnUnauthorizedForNonExistentUser() throws Exception {
            // Given
            String authRequest = "{\"username\":\"nonexistent\",\"password\":\"password\"}";

            // When
            ResultActions result = mockMvc.perform(post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(authRequest));

            // Then
            result.andExpect(status().isUnauthorized());
        }
    }
} 