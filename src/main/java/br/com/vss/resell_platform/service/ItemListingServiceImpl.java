package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.InvalidOwnerException;
import br.com.vss.resell_platform.exceptions.ResourceNotFoundException;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.service.interfaces.ItemListingService;
import br.com.vss.resell_platform.service.interfaces.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ItemListingServiceImpl implements ItemListingService {

    private final ItemRepository itemRepository;
    private final ItemService itemService;

    public ItemListingServiceImpl(ItemRepository itemRepository, ItemService itemService) {
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    @Override
    @Transactional
    public Item createListing(Item item, User seller) {
        item.setSeller(seller);
        item.setListedAt(LocalDateTime.now());
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateListing(Long id, Item updatedItem, String currentUsername) {
        Item existingItem = itemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        
        if (!isItemOwner(existingItem, currentUsername)) {
            throw new InvalidOwnerException();
        }
        
        // Update the item fields
        existingItem.setName(updatedItem.getName());
        existingItem.setBrand(updatedItem.getBrand());
        existingItem.setCondition(updatedItem.getCondition());
        existingItem.setPrice(updatedItem.getPrice());
        existingItem.setSize(updatedItem.getSize());
        
        return itemRepository.save(existingItem);
    }

    @Override
    @Transactional
    public void deleteListing(Long id, String currentUsername) {
        Item existingItem = itemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        
        if (!isItemOwner(existingItem, currentUsername)) {
            throw new InvalidOwnerException();
        }
        
        itemService.delete(id);
    }

    @Override
    public boolean isItemOwner(Item item, String username) {
        return Objects.equals(item.getSeller().getUsername(), username);
    }
} 