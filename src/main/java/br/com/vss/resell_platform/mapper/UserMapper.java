package br.com.vss.resell_platform.mapper;

import br.com.vss.resell_platform.controller.dto.UserRequest;
import br.com.vss.resell_platform.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserRequest request) {
        return new User(request.username(), request.password(), request.email());
    }
}
