package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.ItemNotFoundException;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public void save(Item item) {
        itemRepository.save(item);
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public Page<Item> findAll(Pageable pageable){
        return itemRepository.findAll(pageable);
    }
}
