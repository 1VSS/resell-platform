package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.EmailAlreadyTakenException;
import br.com.vss.resell_platform.exceptions.UsernameAlreadyTakenException;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyTakenException();
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyTakenException();
        }
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {

        Optional<User> newUser;

        return newUser = userRepository.findByUsername(username);

    }
}
