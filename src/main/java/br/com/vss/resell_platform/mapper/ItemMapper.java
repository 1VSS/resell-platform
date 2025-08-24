package br.com.vss.resell_platform.mapper;

import br.com.vss.resell_platform.controller.dto.ItemRequest;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {


    public Item toItem(ItemRequest itemRequest, User user) {

        return new Item(itemRequest.name(), itemRequest.brand(), itemRequest.category(), itemRequest.subCategory(), itemRequest.condition(),
                itemRequest.price(), itemRequest.size(), user);
    }

}
