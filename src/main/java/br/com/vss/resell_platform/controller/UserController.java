package br.com.vss.resell_platform.controller;

import br.com.vss.resell_platform.controller.dto.UserRequest;
import br.com.vss.resell_platform.mapper.UserMapper;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.UserRepository;
import br.com.vss.resell_platform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, UserService userService, UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> postUser(@RequestBody UserRequest userRequest) {

        Optional<User> user = userService.findByUsername(userRequest.username());

        if (user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        User newUser = userMapper.toUser(userRequest);
        newUser.setPassword(passwordEncoder.encode(userRequest.password()));
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }
}
