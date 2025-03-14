package br.com.vss.resell_platform.repository;

import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * Find items by status with pagination
     */
    Page<Item> findByStatus(ItemStatus status, Pageable pageable);
    
    /**
     * Find items by name (case insensitive, partial match) and status
     */
    Page<Item> findByNameContainingIgnoreCaseAndStatus(String name, ItemStatus status, Pageable pageable);
    
    /**
     * Find items by brand (case insensitive) and status
     */
    Page<Item> findByBrandIgnoreCaseAndStatus(String brand, ItemStatus status, Pageable pageable);
    
    /**
     * Find items by condition and status
     */
    Page<Item> findByConditionAndStatus(Condition condition, ItemStatus status, Pageable pageable);
    
    /**
     * Find items by price range and status
     */
    Page<Item> findByPriceBetweenAndStatus(BigDecimal minPrice, BigDecimal maxPrice, ItemStatus status, Pageable pageable);
    
    /**
     * Find items by size (case insensitive) and status
     */
    Page<Item> findBySizeIgnoreCaseAndStatus(String size, ItemStatus status, Pageable pageable);
    
    /**
     * Find item by exact name (for testing purposes)
     */
    Optional<Item> findByName(String name);
}
