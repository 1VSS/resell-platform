package br.com.vss.resell_platform.integration.controller;

import br.com.vss.resell_platform.controller.dto.ItemRequest;
import br.com.vss.resell_platform.integration.BaseIntegrationTest;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.repository.UserRepository;
import br.com.vss.resell_platform.service.JwtService;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.ItemStatus;
import br.com.vss.resell_platform.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class ItemControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private User otherUser;
    private String testUserToken;
    private String otherUserToken;
    private Item testItem;

    @BeforeEach
    void setup() {
        // Clean up repositories
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser = TestDataFactory.createTestUser();
        testUser.setPassword(passwordEncoder.encode(TestDataFactory.DEFAULT_PASSWORD));
        testUser = userRepository.save(testUser);

        otherUser = TestDataFactory.createTestUser("otheruser", "password123", "other@example.com");
        otherUser.setPassword(passwordEncoder.encode("password123"));
        otherUser = userRepository.save(otherUser);

        // Generate tokens
        testUserToken = jwtService.generateJwtToken(testUser.getUsername());
        otherUserToken = jwtService.generateJwtToken(otherUser.getUsername());

        // Create a test item
        testItem = TestDataFactory.createTestItem(testUser);
        testItem = itemRepository.save(testItem);
    }

    @Nested
    @DisplayName("GET /feed")
    class GetFeedTests {

        @Test
        @DisplayName("Should return feed with all available items")
        void shouldReturnFeed() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/feed")
                    .param("page", "0")
                    .param("pageSize", "10"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].name", is(testItem.getName())))
                    .andExpect(jsonPath("$.items[0].brand", is(testItem.getBrand())))
                    .andExpect(jsonPath("$.items[0].condition", is(testItem.getCondition().toString())))
                    .andExpect(jsonPath("$.items[0].price", is(testItem.getPrice().doubleValue())))
                    .andExpect(jsonPath("$.items[0].size", is(testItem.getSize())))
                    .andExpect(jsonPath("$.items[0].username", is(testUser.getUsername())));
        }

        @Test
        @DisplayName("Should return empty feed when no items are available")
        void shouldReturnEmptyFeedWhenNoItems() throws Exception {
            // Given
            itemRepository.deleteAll();

            // When
            ResultActions result = mockMvc.perform(get("/feed")
                    .param("page", "0")
                    .param("pageSize", "10"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.items", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));
        }
    }

    @Nested
    @DisplayName("POST /items")
    class CreateItemTests {

        @Test
        @DisplayName("Should create a new item")
        void shouldCreateNewItem() throws Exception {
            // Given
            ItemRequest itemRequest = new ItemRequest(
                    "New Item", 
                    "New Brand", 
                    Condition.NEW, 
                    new BigDecimal("199.99"),
                    "L"
            );

            // When
            ResultActions result = mockMvc.perform(post("/items")
                    .header("Authorization", "Bearer " + testUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemRequest)));

            // Then
            result.andExpect(status().isOk());

            // Verify the item was saved in the database
            assertEquals(2, itemRepository.count());
            Item savedItem = itemRepository.findByName("New Item").orElse(null);
            assertNotNull(savedItem);
            assertEquals("New Brand", savedItem.getBrand());
            assertEquals(Condition.NEW, savedItem.getCondition());
            assertEquals(0, new BigDecimal("199.99").compareTo(savedItem.getPrice()));
            assertEquals("L", savedItem.getSize());
            assertEquals(testUser.getId(), savedItem.getSeller().getId());
            assertEquals(ItemStatus.AVAILABLE, savedItem.getStatus());
        }

        @Test
        @DisplayName("Should return 401 when no authentication provided")
        void shouldReturn401WhenNoAuth() throws Exception {
            // Given
            ItemRequest itemRequest = new ItemRequest(
                    "New Item", 
                    "New Brand", 
                    Condition.NEW, 
                    new BigDecimal("199.99"),
                    "L"
            );

            // When
            ResultActions result = mockMvc.perform(post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemRequest)));

            // Then
            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /items/{id}")
    class UpdateItemTests {

        @Test
        @DisplayName("Should update an existing item")
        void shouldUpdateItem() throws Exception {
            // Given
            ItemRequest updateRequest = new ItemRequest(
                    "Updated Item", 
                    "Updated Brand", 
                    Condition.USED, 
                    new BigDecimal("149.99"),
                    "XL"
            );

            // When
            ResultActions result = mockMvc.perform(put("/items/" + testItem.getId())
                    .header("Authorization", "Bearer " + testUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)));

            // Then
            result.andExpect(status().isOk());

            // Verify the item was updated in the database
            Item updatedItem = itemRepository.findById(testItem.getId()).orElse(null);
            assertNotNull(updatedItem);
            assertEquals("Updated Item", updatedItem.getName());
            assertEquals("Updated Brand", updatedItem.getBrand());
            assertEquals(Condition.USED, updatedItem.getCondition());
            assertEquals(0, new BigDecimal("149.99").compareTo(updatedItem.getPrice()));
            assertEquals("XL", updatedItem.getSize());
        }

        @Test
        @DisplayName("Should return 403 when user is not the owner")
        void shouldReturn403WhenNotOwner() throws Exception {
            // Given
            ItemRequest updateRequest = new ItemRequest(
                    "Updated Item", 
                    "Updated Brand", 
                    Condition.USED, 
                    new BigDecimal("149.99"),
                    "XL"
            );

            // When (using other user's token)
            ResultActions result = mockMvc.perform(put("/items/" + testItem.getId())
                    .header("Authorization", "Bearer " + otherUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)));

            // Then
            result.andExpect(status().isUnauthorized());

            // Verify the item was not updated
            Item item = itemRepository.findById(testItem.getId()).orElse(null);
            assertNotNull(item);
            assertEquals(testItem.getName(), item.getName());
        }
    }

    @Nested
    @DisplayName("DELETE /items/{id}")
    class DeleteItemTests {

        @Test
        @DisplayName("Should delete an item")
        void shouldDeleteItem() throws Exception {
            // When
            ResultActions result = mockMvc.perform(delete("/items/" + testItem.getId())
                    .header("Authorization", "Bearer " + testUserToken));

            // Then
            result.andExpect(status().isOk());

            // Verify the item was deleted
            assertFalse(itemRepository.existsById(testItem.getId()));
        }

        @Test
        @DisplayName("Should return 403 when user is not the owner")
        void shouldReturn403WhenNotOwner() throws Exception {
            // When (using other user's token)
            ResultActions result = mockMvc.perform(delete("/items/" + testItem.getId())
                    .header("Authorization", "Bearer " + otherUserToken));

            // Then
            result.andExpect(status().isUnauthorized());

            // Verify the item still exists
            assertTrue(itemRepository.existsById(testItem.getId()));
        }
    }

    @Nested
    @DisplayName("GET /items/search")
    class SearchItemsTests {

        @Test
        @DisplayName("Should search items by name")
        void shouldSearchItemsByName() throws Exception {
            // Given - Add a few more items with different names
            Item item1 = TestDataFactory.createTestItem("Special Sneakers", "Nike", Condition.NEW, 
                    new BigDecimal("120.00"), "43", testUser);
            Item item2 = TestDataFactory.createTestItem("Regular Shirt", "Adidas", Condition.USED, 
                    new BigDecimal("50.00"), "M", testUser);
            itemRepository.save(item1);
            itemRepository.save(item2);

            // When
            ResultActions result = mockMvc.perform(get("/items/search")
                    .param("query", "Special")
                    .param("page", "0")
                    .param("pageSize", "10"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].name", containsString("Special")))
                    .andExpect(jsonPath("$.totalElements", is(1)));
        }

        @Test
        @DisplayName("Should return empty result for non-matching search")
        void shouldReturnEmptyResultForNonMatchingSearch() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/items/search")
                    .param("query", "NonExistingItem")
                    .param("page", "0")
                    .param("pageSize", "10"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.items", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));
        }
    }
} 