package br.com.vss.resell_platform.util;

import br.com.vss.resell_platform.controller.dto.ItemRequest;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Factory class for creating test data
 */
public class TestDataFactory {

    // Constants for creating consistent test data
    public static final String DEFAULT_USERNAME = "testuser";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final String DEFAULT_EMAIL = "test@example.com";
    
    public static final String DEFAULT_ITEM_NAME = "Test Item";
    public static final String DEFAULT_BRAND = "Test Brand";
    public static final Condition DEFAULT_CONDITION = Condition.NEW;
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("99.99");
    public static final String DEFAULT_SIZE = "M";
    
    /**
     * Create a test user
     * 
     * @return User instance for testing
     */
    public static User createTestUser() {
        return createTestUser(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_EMAIL);
    }
    
    /**
     * Create a test user with specific values
     */
    public static User createTestUser(String username, String password, String email) {
        User user = new User(username, password, email);
        return user;
    }
    
    /**
     * Create a test item
     * 
     * @param seller The user who is selling the item
     * @return Item instance for testing
     */
    public static Item createTestItem(User seller) {
        return createTestItem(DEFAULT_ITEM_NAME, DEFAULT_BRAND, DEFAULT_CONDITION, DEFAULT_PRICE, DEFAULT_SIZE, seller);
    }
    
    /**
     * Create a test item with specific values
     */
    public static Item createTestItem(String name, String brand, Condition condition, BigDecimal price, String size, User seller) {
        Item item = new Item(name, brand, condition, price, size, seller);
        item.setStatus(ItemStatus.AVAILABLE);
        item.setListedAt(LocalDateTime.now());
        return item;
    }
    
    /**
     * Create a test item request DTO
     */
    public static ItemRequest createTestItemRequest() {
        return new ItemRequest(DEFAULT_ITEM_NAME, DEFAULT_BRAND, DEFAULT_CONDITION, DEFAULT_PRICE, DEFAULT_SIZE);
    }
    
    /**
     * Create a list of test items
     * 
     * @param count Number of items to create
     * @param seller The user who is selling the items
     * @return List of Item instances for testing
     */
    public static List<Item> createTestItems(int count, User seller) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Item item = createTestItem("Item " + i, "Brand " + i, DEFAULT_CONDITION, 
                            DEFAULT_PRICE.add(BigDecimal.valueOf(i * 10)), "Size " + i, seller);
                    item.setId((long) (i + 1));
                    return item;
                })
                .collect(Collectors.toList());
    }
} 