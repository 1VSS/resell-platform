package br.com.vss.resell_platform.controller;

import br.com.vss.resell_platform.controller.dto.ItemRequest;
import br.com.vss.resell_platform.mapper.ItemMapper;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.service.ItemService;
import br.com.vss.resell_platform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;

    public ItemController(ItemService itemService, ItemMapper itemMapper, UserService userService) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.userService = userService;
    }

    @PostMapping("/items")
    public ResponseEntity<Void> postItem(Authentication authentication, @RequestBody ItemRequest itemRequest) {

        Optional<User> user = userService.findByUsername(authentication.getName());

        Item newItem = itemMapper.toItem(itemRequest, user.get());
        itemService.save(newItem);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

}
