package br.com.vss.resell_platform.service.interfaces;

import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.util.Condition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Service interface for searching and filtering items
 */
public interface ItemSearchService {
    
    /**
     * Get a paginated feed of all available items
     * 
     * @param pageable pagination information
     * @return a Page of items
     */
    Page<Item> getFeed(Pageable pageable);
    
    /**
     * Search for items by name
     * 
     * @param name the name to search for
     * @param pageable pagination information
     * @return a Page of matching items
     */
    Page<Item> searchByName(String name, Pageable pageable);
    
    /**
     * Filter items by brand
     * 
     * @param brand the brand to filter by
     * @param pageable pagination information
     * @return a Page of matching items
     */
    Page<Item> filterByBrand(String brand, Pageable pageable);
    
    /**
     * Filter items by condition
     * 
     * @param condition the condition to filter by
     * @param pageable pagination information
     * @return a Page of matching items
     */
    Page<Item> filterByCondition(Condition condition, Pageable pageable);
    
    /**
     * Filter items by price range
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return a Page of matching items
     */
    Page<Item> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Filter items by size
     * 
     * @param size the size to filter by
     * @param pageable pagination information
     * @return a Page of matching items
     */
    Page<Item> filterBySize(String size, Pageable pageable);
} 