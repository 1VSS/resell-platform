package br.com.vss.resell_platform.controller;

import br.com.vss.resell_platform.controller.dto.ItemRequest;
import br.com.vss.resell_platform.exceptions.InvalidOwnerException;
import br.com.vss.resell_platform.mapper.ItemMapper;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.service.ItemService;
import br.com.vss.resell_platform.service.TransactionService;
import br.com.vss.resell_platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final TransactionService transactionService;

    public ItemController(ItemService itemService, ItemMapper itemMapper, UserService userService, TransactionService transactionService) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @PostMapping("/items")
    public ResponseEntity<Void> postItem(Authentication authentication, @RequestBody ItemRequest itemRequest) {

        Optional<User> user = userService.findByUsername(authentication.getName());

        Item newItem = itemMapper.toItem(itemRequest, user.get());
        itemService.save(newItem);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @PutMapping("/items/{id}")
    public ResponseEntity<Void> editItem(Authentication authentication, @Valid @RequestBody ItemRequest itemRequest,
                                        @PathVariable Long id) {

        var item = itemService.findById(id).get();

        if (Objects.equals(item.getSeller().getUsername(), authentication.getName())) {
            item.setName(itemRequest.name());
            item.setBrand(itemRequest.brand());
            item.setCondition(itemRequest.condition());
            item.setPrice(itemRequest.price());
            item.setSize(itemRequest.size());

            itemService.save(item);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        throw new InvalidOwnerException();
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(Authentication authentication, @PathVariable Long id) {

        var item = itemService.findById(id).get();

        if (Objects.equals(item.getSeller().getUsername(), authentication.getName())) {
            itemService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        throw new InvalidOwnerException();
    }

    @PostMapping("/item/{id}/buy")
    public ResponseEntity<Void> purchaseItem(Authentication authentication, @PathVariable Long id) {

        var item = itemService.findById(id).get();
        Optional<User> buyer = userService.findByUsername(authentication.getName());

        transactionService.purchaseItem(buyer.get(), item.getSeller(), item);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
