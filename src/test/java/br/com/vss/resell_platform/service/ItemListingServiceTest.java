package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.InvalidOwnerException;
import br.com.vss.resell_platform.exceptions.ResourceNotFoundException;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.service.interfaces.ItemService;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemListingServiceTest {

    @Mock
    private ItemRepository itemRepository;
    
    @Mock
    private ItemService itemService;
    
    @InjectMocks
    private ItemListingServiceImpl itemListingService;
    
    @Captor
    private ArgumentCaptor<Item> itemCaptor;
    
    private User seller;
    private Item item;
    
    @BeforeEach
    void setUp() {
        seller = TestDataFactory.createTestUser();
        seller.setId(1L);
        
        item = TestDataFactory.createTestItem(seller);
        item.setId(1L);
    }
    
    @Nested
    @DisplayName("createListing tests")
    class CreateListingTests {
        
        @Test
        @DisplayName("Should successfully create a new item listing")
        void shouldCreateNewItemListing() {
            // Given
            when(itemRepository.save(any(Item.class))).thenReturn(item);
            
            // When
            Item createdItem = itemListingService.createListing(item, seller);
            
            // Then
            verify(itemRepository).save(itemCaptor.capture());
            Item capturedItem = itemCaptor.getValue();
            
            assertNotNull(createdItem);
            assertEquals(seller, capturedItem.getSeller());
            assertNotNull(capturedItem.getListedAt());
        }
        
        @Test
        @DisplayName("Should set ListedAt timestamp when creating a listing")
        void shouldSetListedAtTimestamp() {
            // Given
            LocalDateTime beforeTest = LocalDateTime.now();
            when(itemRepository.save(any(Item.class))).thenReturn(item);
            
            // When
            Item result = itemListingService.createListing(item, seller);
            
            // Then
            verify(itemRepository).save(itemCaptor.capture());
            LocalDateTime listedAt = itemCaptor.getValue().getListedAt();
            
            assertNotNull(listedAt);
            // Check that listedAt is set to current time (within a small margin)
            assertTrue(listedAt.isAfter(beforeTest.minusSeconds(1)));
            assertTrue(listedAt.isBefore(LocalDateTime.now().plusSeconds(1)));
        }
    }
    
    @Nested
    @DisplayName("updateListing tests")
    class UpdateListingTests {
        
        @Test
        @DisplayName("Should successfully update an item listing")
        void shouldUpdateItemListing() {
            // Given
            Item updatedItem = new Item("Updated Name", "Updated Brand", 
                    Condition.USED, new BigDecimal("199.99"), "L", seller);
            
            when(itemService.findById(anyLong())).thenReturn(Optional.of(item));
            when(itemRepository.save(any(Item.class))).thenReturn(item);
            
            // When
            Item result = itemListingService.updateListing(1L, updatedItem, seller.getUsername());
            
            // Then
            verify(itemRepository).save(itemCaptor.capture());
            Item savedItem = itemCaptor.getValue();
            
            assertEquals("Updated Name", savedItem.getName());
            assertEquals("Updated Brand", savedItem.getBrand());
            assertEquals(Condition.USED, savedItem.getCondition());
            assertEquals(new BigDecimal("199.99"), savedItem.getPrice());
            assertEquals("L", savedItem.getSize());
        }
        
        @Test
        @DisplayName("Should throw ResourceNotFoundException when item doesn't exist")
        void shouldThrowResourceNotFoundExceptionWhenItemDoesntExist() {
            // Given
            when(itemService.findById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Item", "id", 999L));
            
            // When/Then
            assertThrows(ResourceNotFoundException.class, () -> 
                itemListingService.updateListing(999L, item, seller.getUsername()));
            
            verify(itemRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Should throw InvalidOwnerException when user is not the item owner")
        void shouldThrowInvalidOwnerExceptionWhenUserIsNotOwner() {
            // Given
            String differentUsername = "different_user";
            
            when(itemService.findById(anyLong())).thenReturn(Optional.of(item));
            
            // When/Then
            assertThrows(InvalidOwnerException.class, () -> 
                itemListingService.updateListing(1L, item, differentUsername));
            
            verify(itemRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("deleteListing tests")
    class DeleteListingTests {
        
        @Test
        @DisplayName("Should successfully delete an item listing")
        void shouldDeleteItemListing() {
            // Given
            when(itemService.findById(anyLong())).thenReturn(Optional.of(item));
            doNothing().when(itemService).delete(anyLong());
            
            // When
            itemListingService.deleteListing(1L, seller.getUsername());
            
            // Then
            verify(itemService).delete(1L);
        }
        
        @Test
        @DisplayName("Should throw ResourceNotFoundException when item doesn't exist")
        void shouldThrowResourceNotFoundExceptionWhenItemDoesntExist() {
            // Given
            when(itemService.findById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Item", "id", 999L));
            
            // When/Then
            assertThrows(ResourceNotFoundException.class, () -> 
                itemListingService.deleteListing(999L, seller.getUsername()));
            
            verify(itemService, never()).delete(anyLong());
        }
        
        @Test
        @DisplayName("Should throw InvalidOwnerException when user is not the item owner")
        void shouldThrowInvalidOwnerExceptionWhenUserIsNotOwner() {
            // Given
            String differentUsername = "different_user";
            
            when(itemService.findById(anyLong())).thenReturn(Optional.of(item));
            
            // When/Then
            assertThrows(InvalidOwnerException.class, () -> 
                itemListingService.deleteListing(1L, differentUsername));
            
            verify(itemService, never()).delete(anyLong());
        }
    }
    
    @Nested
    @DisplayName("isItemOwner tests")
    class IsItemOwnerTests {
        
        @Test
        @DisplayName("Should return true when user is the item owner")
        void shouldReturnTrueWhenUserIsOwner() {
            // When
            boolean result = itemListingService.isItemOwner(item, seller.getUsername());
            
            // Then
            assertTrue(result);
        }
        
        @Test
        @DisplayName("Should return false when user is not the item owner")
        void shouldReturnFalseWhenUserIsNotOwner() {
            // When
            boolean result = itemListingService.isItemOwner(item, "different_user");
            
            // Then
            assertFalse(result);
        }
        
        @Test
        @DisplayName("Should handle null username")
        void shouldHandleNullUsername() {
            // When
            boolean result = itemListingService.isItemOwner(item, null);
            
            // Then
            assertFalse(result);
        }
        
        @Test
        @DisplayName("Should handle item with null seller")
        void shouldHandleItemWithNullSeller() {
            // Given
            Item itemWithNullSeller = new Item();
            
            // When
            boolean result = itemListingService.isItemOwner(itemWithNullSeller, seller.getUsername());
            
            // Then
            assertFalse(result);
        }
    }
} 