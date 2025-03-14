package br.com.vss.resell_platform.integration.service;

import br.com.vss.resell_platform.exceptions.ItemNotAvailableException;
import br.com.vss.resell_platform.integration.BaseIntegrationTest;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.Transaction;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.repository.TransactionRepository;
import br.com.vss.resell_platform.repository.UserRepository;
import br.com.vss.resell_platform.service.TransactionService;
import br.com.vss.resell_platform.util.ItemStatus;
import br.com.vss.resell_platform.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User seller;
    private User buyer;
    private Item item;

    @BeforeEach
    void setup() {
        // Clean up repositories
        transactionRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        seller = TestDataFactory.createTestUser("seller", "password", "seller@example.com");
        seller = userRepository.save(seller);

        buyer = TestDataFactory.createTestUser("buyer", "password", "buyer@example.com");
        buyer = userRepository.save(buyer);

        // Create a test item
        item = TestDataFactory.createTestItem(seller);
        item = itemRepository.save(item);
    }

    @Nested
    @DisplayName("purchaseItem tests")
    class PurchaseItemTests {

        @Test
        @DisplayName("Should successfully purchase an item")
        void shouldPurchaseItem() {
            // When
            transactionService.purchaseItem(buyer, seller, item);

            // Then
            Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
            assertEquals(ItemStatus.SOLD, updatedItem.getStatus());

            List<Transaction> transactions = transactionRepository.findAll();
            assertEquals(1, transactions.size());

            Transaction transaction = transactions.get(0);
            assertEquals(buyer.getId(), transaction.getBuyer().getId());
            assertEquals(seller.getId(), transaction.getSeller().getId());
            assertEquals(item.getId(), transaction.getItem().getId());
            assertEquals(item.getPrice(), transaction.getAmount());
            assertNotNull(transaction.getDate());
        }

        @Test
        @DisplayName("Should calculate commission correctly")
        void shouldCalculateCommissionCorrectly() {
            // Given
            BigDecimal expectedCommission = item.getPrice().multiply(new BigDecimal("0.1")); // 10% commission

            // When
            transactionService.purchaseItem(buyer, seller, item);

            // Then
            Transaction transaction = transactionRepository.findAll().get(0);
            assertTrue(transaction.getCommission().compareTo(expectedCommission) == 0);
        }

        @Test
        @DisplayName("Should throw exception when trying to purchase an already sold item")
        void shouldThrowExceptionWhenItemIsSold() {
            // Given
            item.setStatus(ItemStatus.SOLD);
            itemRepository.save(item);

            // When/Then
            assertThrows(ItemNotAvailableException.class, () -> 
                transactionService.purchaseItem(buyer, seller, item));
            
            // Verify no transaction was created
            assertEquals(0, transactionRepository.count());
        }

        @Test
        @DisplayName("Should throw exception when buyer tries to purchase their own item")
        void shouldThrowExceptionWhenBuyerIsSeller() {
            // When/Then
            assertThrows(ItemNotAvailableException.class, () -> 
                transactionService.purchaseItem(seller, seller, item));
            
            // Verify no transaction was created and item status is unchanged
            assertEquals(0, transactionRepository.count());
            Item unchangedItem = itemRepository.findById(item.getId()).orElseThrow();
            assertEquals(ItemStatus.AVAILABLE, unchangedItem.getStatus());
        }
    }
} 