package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.service.interfaces.ItemSearchService;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.ItemStatus;
import br.com.vss.resell_platform.util.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemSearchServiceTest {

    @Mock
    private ItemRepository itemRepository;
    
    @InjectMocks
    private ItemSearchServiceImpl itemSearchService;
    
    @Captor
    private ArgumentCaptor<ItemStatus> statusCaptor;
    
    private User seller;
    private Item item;
    private Pageable pageable;
    private Page<Item> itemPage;
    
    @BeforeEach
    void setUp() {
        seller = TestDataFactory.createTestUser();
        seller.setId(1L);
        
        item = TestDataFactory.createTestItem(seller);
        item.setId(1L);
        
        pageable = PageRequest.of(0, 10);
        itemPage = new PageImpl<>(List.of(item), pageable, 1);
    }
    
    @Nested
    @DisplayName("getFeed tests")
    class GetFeedTests {
        
        @Test
        @DisplayName("Should return feed with available items")
        void shouldReturnFeedWithAvailableItems() {
            // Given
            when(itemRepository.findByStatus(any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(itemPage);
            
            // When
            Page<Item> result = itemSearchService.getFeed(pageable);
            
            // Then
            verify(itemRepository).findByStatus(statusCaptor.capture(), eq(pageable));
            assertEquals(ItemStatus.AVAILABLE, statusCaptor.getValue());
            
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(item, result.getContent().get(0));
        }
        
        @Test
        @DisplayName("Should return empty feed when no items are available")
        void shouldReturnEmptyFeedWhenNoItemsAvailable() {
            // Given
            Page<Item> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(itemRepository.findByStatus(any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(emptyPage);
            
            // When
            Page<Item> result = itemSearchService.getFeed(pageable);
            
            // Then
            verify(itemRepository).findByStatus(eq(ItemStatus.AVAILABLE), eq(pageable));
            
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
        }
    }
    
    @Nested
    @DisplayName("searchByName tests")
    class SearchByNameTests {
        
        @Test
        @DisplayName("Should search items by name")
        void shouldSearchItemsByName() {
            // Given
            String searchName = "Test";
            when(itemRepository.findByNameContainingIgnoreCaseAndStatus(anyString(), any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(itemPage);
            
            // When
            Page<Item> result = itemSearchService.searchByName(searchName, pageable);
            
            // Then
            verify(itemRepository).findByNameContainingIgnoreCaseAndStatus(eq(searchName), eq(ItemStatus.AVAILABLE), eq(pageable));
            
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
        
        @Test
        @DisplayName("Should handle empty search query")
        void shouldHandleEmptySearchQuery() {
            // Given
            String emptySearch = "";
            when(itemRepository.findByNameContainingIgnoreCaseAndStatus(anyString(), any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));
            
            // When
            Page<Item> result = itemSearchService.searchByName(emptySearch, pageable);
            
            // Then
            verify(itemRepository).findByNameContainingIgnoreCaseAndStatus(eq(emptySearch), eq(ItemStatus.AVAILABLE), eq(pageable));
            
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
        }
    }
    
    @Nested
    @DisplayName("filterByBrand tests")
    class FilterByBrandTests {
        
        @Test
        @DisplayName("Should filter items by brand")
        void shouldFilterItemsByBrand() {
            // Given
            String brand = "Test Brand";
            when(itemRepository.findByBrandIgnoreCaseAndStatus(anyString(), any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(itemPage);
            
            // When
            Page<Item> result = itemSearchService.filterByBrand(brand, pageable);
            
            // Then
            verify(itemRepository).findByBrandIgnoreCaseAndStatus(eq(brand), eq(ItemStatus.AVAILABLE), eq(pageable));
            
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }
    
    @Nested
    @DisplayName("filterByCondition tests")
    class FilterByConditionTests {
        
        @ParameterizedTest
        @EnumSource(Condition.class)
        @DisplayName("Should filter items by condition")
        void shouldFilterItemsByCondition(Condition condition) {
            // Given
            when(itemRepository.findByConditionAndStatus(any(Condition.class), any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(itemPage);
            
            // When
            Page<Item> result = itemSearchService.filterByCondition(condition, pageable);
            
            // Then
            verify(itemRepository).findByConditionAndStatus(eq(condition), eq(ItemStatus.AVAILABLE), eq(pageable));
            
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }
    
    @Nested
    @DisplayName("filterByPriceRange tests")
    class FilterByPriceRangeTests {
        
        @Test
        @DisplayName("Should filter items by price range")
        void shouldFilterItemsByPriceRange() {
            // Given
            BigDecimal minPrice = new BigDecimal("50.00");
            BigDecimal maxPrice = new BigDecimal("150.00");
            when(itemRepository.findByPriceBetweenAndStatus(any(BigDecimal.class), any(BigDecimal.class), any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(itemPage);
            
            // When
            Page<Item> result = itemSearchService.filterByPriceRange(minPrice, maxPrice, pageable);
            
            // Then
            verify(itemRepository).findByPriceBetweenAndStatus(eq(minPrice), eq(maxPrice), eq(ItemStatus.AVAILABLE), eq(pageable));
            
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
        
        @Test
        @DisplayName("Should handle negative minimum price")
        void shouldHandleNegativeMinimumPrice() {
            // Given
            BigDecimal minPrice = new BigDecimal("-10.00");
            BigDecimal maxPrice = new BigDecimal("100.00");
            when(itemRepository.findByPriceBetweenAndStatus(any(BigDecimal.class), any(BigDecimal.class), any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(itemPage);
            
            // When
            Page<Item> result = itemSearchService.filterByPriceRange(minPrice, maxPrice, pageable);
            
            // Then
            verify(itemRepository).findByPriceBetweenAndStatus(eq(minPrice), eq(maxPrice), eq(ItemStatus.AVAILABLE), eq(pageable));
        }
        
        @Test
        @DisplayName("Should handle minPrice greater than maxPrice")
        void shouldHandleMinPriceGreaterThanMaxPrice() {
            // Given
            BigDecimal minPrice = new BigDecimal("200.00");
            BigDecimal maxPrice = new BigDecimal("100.00");
            when(itemRepository.findByPriceBetweenAndStatus(any(BigDecimal.class), any(BigDecimal.class), any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));
            
            // When
            Page<Item> result = itemSearchService.filterByPriceRange(minPrice, maxPrice, pageable);
            
            // Then
            verify(itemRepository).findByPriceBetweenAndStatus(eq(minPrice), eq(maxPrice), eq(ItemStatus.AVAILABLE), eq(pageable));
            
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
        }
    }
    
    @Nested
    @DisplayName("filterBySize tests")
    class FilterBySizeTests {
        
        @Test
        @DisplayName("Should filter items by size")
        void shouldFilterItemsBySize() {
            // Given
            String size = "M";
            when(itemRepository.findBySizeIgnoreCaseAndStatus(anyString(), any(ItemStatus.class), any(Pageable.class)))
                .thenReturn(itemPage);
            
            // When
            Page<Item> result = itemSearchService.filterBySize(size, pageable);
            
            // Then
            verify(itemRepository).findBySizeIgnoreCaseAndStatus(eq(size), eq(ItemStatus.AVAILABLE), eq(pageable));
            
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }
} 