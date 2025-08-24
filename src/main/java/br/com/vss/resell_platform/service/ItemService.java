package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.controller.SerializablePage;
import br.com.vss.resell_platform.exceptions.ItemNotFoundException;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.repository.ItemSpecification;
import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.SubCategory;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Optional<Item> findById(Long id){

        if (itemRepository.findById(id).isEmpty()) {
            throw new ItemNotFoundException();
        }
        return itemRepository.findById(id);
    }

    @Transactional
    @CachePut(cacheNames = "Items")
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    @CacheEvict(cacheNames = "Items")
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public Page<Item> findAll(Pageable pageable){
        return itemRepository.findAll(pageable);
    }

    @Cacheable(cacheNames = "Items")
    public SerializablePage<Item> findFiltered(Pageable page, String name, String brand, Category category, SubCategory subCategory,
                                   Condition condition, BigDecimal lowest, BigDecimal highest, String size) {
        List<Item> matches = itemRepository.findAll(ItemSpecification.likeName(name)
                .and(ItemSpecification.likeBrand(brand)
                .and(ItemSpecification.byCondition(condition)
                .and(ItemSpecification.bySize(size))))
                .and(ItemSpecification.byCategory(category))
                .and(ItemSpecification.bySubcategory(subCategory)
                .and(ItemSpecification.betweenPrice(lowest, highest))), page).toList();

        Page<Item> pageResult = new PageImpl<>(matches, page, matches.size());
        return new SerializablePage<>(pageResult);
    }

}
