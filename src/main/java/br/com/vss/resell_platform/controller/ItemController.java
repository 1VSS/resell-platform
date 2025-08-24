package br.com.vss.resell_platform.controller;

import br.com.vss.resell_platform.controller.dto.FeedDto;
import br.com.vss.resell_platform.controller.dto.FeedItemDto;
import br.com.vss.resell_platform.controller.dto.ItemRequest;
import br.com.vss.resell_platform.exceptions.InvalidOwnerException;
import br.com.vss.resell_platform.mapper.ItemMapper;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.service.ItemService;
import br.com.vss.resell_platform.service.TransactionService;
import br.com.vss.resell_platform.service.UserService;
import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.SubCategory;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

//    @GetMapping("/feed")
//    public ResponseEntity<FeedDto> feed(@RequestParam (required = false) String name,
//                                        @RequestParam(value = "page", defaultValue = "0")int page,
//                                        @RequestParam(value = "pageSize", defaultValue = "10")int pageSize) {
//
//        if (name != null && !name.isEmpty()) {
//            var listings = itemService.filterByName(PageRequest.of(page, pageSize, Sort.Direction.DESC, "listedAt"), name)
//                    .map(item ->
//                            new FeedItemDto(
//                                    item.getName(),
//                                    item.getBrand(),
//                                    item.getCondition(),
//                                    item.getPrice(),
//                                    item.getSize(),
//                                    item.getSeller().getUsername()));
//
//            return ResponseEntity.status(HttpStatus.OK).body(new FeedDto(listings.toList(), page, pageSize, listings.getTotalElements()));
//        }
//
//        var listings = itemService.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "listedAt"))
//                .map(item ->
//                        new FeedItemDto(
//                                item.getName(),
//                                item.getBrand(),
//                                item.getCondition(),
//                                item.getPrice(),
//                                item.getSize(),
//                                item.getSeller().getUsername()));
//
//        return ResponseEntity.status(HttpStatus.OK).body(new FeedDto(listings.toList(), page, pageSize, listings.getTotalElements()));
//    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> filter(@RequestParam (required = false) String name,
                                          @RequestParam (required = false) String brand,
                                          @RequestParam (required = false) Category category,
                                          @RequestParam (required = false) SubCategory subCategory,
                                          @RequestParam (required = false) Condition condition,
                                          @RequestParam (required = false, defaultValue = "0") BigDecimal lowest,
                                          @RequestParam (required = false, defaultValue = "999999") BigDecimal highest,
                                          @RequestParam (required = false) String size,
                                          @RequestParam(value = "page", defaultValue = "0")int page,
                                          @RequestParam(value = "pageSize", defaultValue = "10")int pageSize) {

        SerializablePage<Item> serializablePage = itemService.findFiltered(PageRequest.of(page, pageSize, Sort.Direction.DESC, "listedAt"),
                        name, brand, category, subCategory, condition, lowest, highest, size);

        Page<Item> itemPage = serializablePage.toPage(PageRequest.of(page, pageSize, Sort.Direction.DESC, "listedAt"));

        var listings = itemPage
                .map(item ->
                        new FeedItemDto(
                                item.getName(),
                                item.getBrand(),
                                item.getCategory(),
                                item.getSubCategory(),
                                item.getCondition(),
                                item.getPrice(),
                                item.getSize(),
                                item.getSeller().getUsername()));

        return ResponseEntity.status(HttpStatus.OK).body(new FeedDto(listings.toList(), page, pageSize, listings.getTotalElements()));
    }

}
