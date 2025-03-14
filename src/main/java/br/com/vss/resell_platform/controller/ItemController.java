package br.com.vss.resell_platform.controller;

import br.com.vss.resell_platform.controller.dto.FeedDto;
import br.com.vss.resell_platform.controller.dto.FeedItemDto;
import br.com.vss.resell_platform.controller.dto.ItemRequest;
import br.com.vss.resell_platform.mapper.ItemMapper;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.service.interfaces.ItemListingService;
import br.com.vss.resell_platform.service.interfaces.ItemSearchService;
import br.com.vss.resell_platform.service.interfaces.ItemService;
import br.com.vss.resell_platform.service.TransactionService;
import br.com.vss.resell_platform.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Tag(name = "Items", description = "Item management endpoints")
public class ItemController {

    private final ItemService itemService;
    private final ItemListingService itemListingService;
    private final ItemSearchService itemSearchService;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final TransactionService transactionService;

    public ItemController(
            ItemService itemService,
            ItemListingService itemListingService,
            ItemSearchService itemSearchService,
            ItemMapper itemMapper,
            UserService userService,
            TransactionService transactionService) {
        this.itemService = itemService;
        this.itemListingService = itemListingService;
        this.itemSearchService = itemSearchService;
        this.itemMapper = itemMapper;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @PostMapping("/items")
    @Operation(summary = "Create a new item listing", description = "Creates a new item for sale on the platform")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item successfully created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> postItem(
            @Parameter(description = "User authentication token") Authentication authentication, 
            @Parameter(description = "Item details") @Valid @RequestBody ItemRequest itemRequest) {

        Optional<User> user = userService.findByUsername(authentication.getName());
        Item newItem = itemMapper.toItem(itemRequest, user.get());
        itemListingService.createListing(newItem, user.get());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Edit an item listing", description = "Updates an existing item listing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item successfully updated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user is not the owner of the item"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> editItem(
            @Parameter(description = "User authentication token") Authentication authentication, 
            @Parameter(description = "Updated item details") @Valid @RequestBody ItemRequest itemRequest,
            @Parameter(description = "Item ID") @PathVariable Long id) {

        Item updatedItem = itemMapper.toItem(itemRequest, null);
        itemListingService.updateListing(id, updatedItem, authentication.getName());
        
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Delete an item listing", description = "Removes an item listing from the platform")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user is not the owner of the item"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "User authentication token") Authentication authentication, 
            @Parameter(description = "Item ID") @PathVariable Long id) {

        itemListingService.deleteListing(id, authentication.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/item/{id}/buy")
    @Operation(summary = "Purchase an item", description = "Buys an item listed on the platform")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item successfully purchased"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "400", description = "Item not available for purchase")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> purchaseItem(
            @Parameter(description = "User authentication token") Authentication authentication, 
            @Parameter(description = "Item ID") @PathVariable Long id) {

        var item = itemService.findById(id).get();
        Optional<User> buyer = userService.findByUsername(authentication.getName());

        transactionService.purchaseItem(buyer.get(), item.getSeller(), item);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/feed")
    @Operation(summary = "Get item feed", description = "Returns a paginated feed of items listed for sale")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved feed")
    })
    public ResponseEntity<FeedDto> feed(
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        var listings = itemSearchService.getFeed(PageRequest.of(page, pageSize, Sort.Direction.DESC, "listedAt"))
                .map(item ->
                        new FeedItemDto(
                                item.getName(),
                                item.getBrand(),
                                item.getCondition(),
                                item.getPrice(),
                                item.getSize(),
                                item.getSeller().getUsername()));

        return ResponseEntity.status(HttpStatus.OK).body(new FeedDto(listings.toList(), page, pageSize, listings.getTotalElements()));
    }
    
    @GetMapping("/items/search")
    @Operation(summary = "Search for items", description = "Searches for items by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    })
    public ResponseEntity<FeedDto> searchItems(
            @Parameter(description = "Search query") @RequestParam("query") String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        var listings = itemSearchService.searchByName(query, PageRequest.of(page, pageSize, Sort.Direction.DESC, "listedAt"))
                .map(item ->
                        new FeedItemDto(
                                item.getName(),
                                item.getBrand(),
                                item.getCondition(),
                                item.getPrice(),
                                item.getSize(),
                                item.getSeller().getUsername()));

        return ResponseEntity.status(HttpStatus.OK).body(new FeedDto(listings.toList(), page, pageSize, listings.getTotalElements()));
    }
}
