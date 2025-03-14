package br.com.vss.resell_platform.controller;

import br.com.vss.resell_platform.controller.dto.UserRequest;
import br.com.vss.resell_platform.mapper.UserMapper;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @Captor
    private ArgumentCaptor<UserRequest> userRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;


    @Nested
    class postUser {

        @Test
        @DisplayName("Should post user successfully")
        void shouldPostUser() {

            UserRequest userRequest = new UserRequest("username", "password", "email");
            User user = new User("username", "password", "email");

            Mockito.doReturn(user).when(userMapper).toUser(userRequestArgumentCaptor.capture());
            Mockito.doNothing().when(userService).save(userArgumentCaptor.capture());
            Mockito.doReturn(user.getPassword()).when(passwordEncoder).encode(userRequest.password());


            var response = userController.postUser(userRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

}