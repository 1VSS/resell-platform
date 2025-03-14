package br.com.vss.resell_platform.service.interfaces;

import br.com.vss.resell_platform.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing Item entities
 */
public interface ItemService {
    
    /**
     * Find an item by its ID
     * 
     * @param id the ID of the item to find
     * @return an Optional containing the item if found
     */
    Optional<Item> findById(Long id);
    
    /**
     * Save an item to the database
     * 
     * @param item the item to save
     */
    void save(Item item);
    
    /**
     * Delete an item by its ID
     * 
     * @param id the ID of the item to delete
     */
    void delete(Long id);
    
    /**
     * Find all items with pagination
     * 
     * @param pageable pagination information
     * @return a Page of items
     */
    Page<Item> findAll(Pageable pageable);
} 