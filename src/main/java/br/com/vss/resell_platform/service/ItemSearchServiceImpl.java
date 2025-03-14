package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.service.interfaces.ItemSearchService;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    private final ItemRepository itemRepository;
    
    public ItemSearchServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> getFeed(Pageable pageable) {
        return itemRepository.findByStatus(ItemStatus.AVAILABLE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> searchByName(String name, Pageable pageable) {
        return itemRepository.findByNameContainingIgnoreCaseAndStatus(name, ItemStatus.AVAILABLE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> filterByBrand(String brand, Pageable pageable) {
        return itemRepository.findByBrandIgnoreCaseAndStatus(brand, ItemStatus.AVAILABLE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> filterByCondition(Condition condition, Pageable pageable) {
        return itemRepository.findByConditionAndStatus(condition, ItemStatus.AVAILABLE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return itemRepository.findByPriceBetweenAndStatus(minPrice, maxPrice, ItemStatus.AVAILABLE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> filterBySize(String size, Pageable pageable) {
        return itemRepository.findBySizeIgnoreCaseAndStatus(size, ItemStatus.AVAILABLE, pageable);
    }
} 