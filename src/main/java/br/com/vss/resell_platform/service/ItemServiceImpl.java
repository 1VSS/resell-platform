package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.ResourceNotFoundException;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.service.interfaces.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id)));
    }

    @Override
    @Transactional
    public void save(Item item) {
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Check if item exists before deleting
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item", "id", id);
        }
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }
} 