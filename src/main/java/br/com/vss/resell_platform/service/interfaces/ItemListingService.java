package br.com.vss.resell_platform.service.interfaces;

import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;

/**
 * Service interface for managing item listings
 */
public interface ItemListingService {
    
    /**
     * Create a new item listing
     * 
     * @param item the item to be listed
     * @param seller the user listing the item
     * @return the created item
     */
    Item createListing(Item item, User seller);
    
    /**
     * Update an existing item listing
     * 
     * @param id the ID of the item to update
     * @param updatedItem the updated item data
     * @param currentUsername the username of the current user
     * @return the updated item
     */
    Item updateListing(Long id, Item updatedItem, String currentUsername);
    
    /**
     * Delete an item listing
     * 
     * @param id the ID of the item to delete
     * @param currentUsername the username of the current user
     */
    void deleteListing(Long id, String currentUsername);
    
    /**
     * Check if the user is the owner of the item
     * 
     * @param item the item to check
     * @param username the username to compare against
     * @return true if the user is the owner, false otherwise
     */
    boolean isItemOwner(Item item, String username);
} 