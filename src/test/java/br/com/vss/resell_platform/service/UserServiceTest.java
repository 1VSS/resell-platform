package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.EmailAlreadyTakenException;
import br.com.vss.resell_platform.exceptions.UsernameAlreadyTakenException;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Nested
    class createUser {

        @Test
        @DisplayName("Should create a user with success")
        void shouldCreateUser() {

            User user = new User("username", "password", "email@email.com");


            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            User input = new User("username", "password", "email@email.com");

            userService.save(input);

            verify(userRepository,times(1)).save(userArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("Should throw UsernameAlreadyTaken exception when username is taken")
        void ShouldThrowUsernameException() {

            User input = new User("username", "password", "email@email.com");

            doReturn(Optional.of(input)).when(userRepository).findByUsername(input.getUsername());

            assertThrows(UsernameAlreadyTakenException.class, () -> userService.save(input));

        }

        @Test
        @DisplayName("Should throw EmailAlreadyTaken exception when email is taken")
        void shouldThrowEmailException() {

            User input = new User("username", "password", "email@email.com");

            doReturn(Optional.of(input)).when(userRepository).findByEmail(input.getEmail());

            assertThrows(EmailAlreadyTakenException.class, () -> userService.save(input));
        }
    }

    @Nested
    class listUsers {

        @Test
        @DisplayName("Should list all users with sucess")
        void shouldReturnAllUsersWithSuccess() {

            User user = new User("username", "password", "email@email.com");
            var userList = List.of(user);
            doReturn(userList).when(userRepository).findAll();

            var output = userService.findAll();

            assertNotNull(output);
            assertEquals(userList.size(), output.size());
        }
    }

    @Nested
    class findUser {

        @Test
        @DisplayName("Should return user by username with success")
        void shouldReturnUserByUsername() {

            User user = new User("username", "password", "email@email.com");

            doReturn(Optional.of(user)).when(userRepository).findByUsername(stringArgumentCaptor.capture());

            var output = userService.findByUsername(user.getUsername());

            assertTrue(output.isPresent());
            assertEquals(user.getUsername(), stringArgumentCaptor.getValue());
        }

    }

}