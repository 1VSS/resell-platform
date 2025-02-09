package br.com.vss.resell_platform.controller;

import br.com.vss.resell_platform.controller.dto.UserRequest;
import br.com.vss.resell_platform.mapper.UserMapper;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> postUser(@Valid @RequestBody UserRequest userRequest) {

        User newUser = userMapper.toUser(userRequest);
        newUser.setPassword(passwordEncoder.encode(userRequest.password()));
        userService.save(newUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
