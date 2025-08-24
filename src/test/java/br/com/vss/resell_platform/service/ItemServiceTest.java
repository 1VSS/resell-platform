package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.ItemNotFoundException;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.ItemRepository;
import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.SubCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Nested
    class createItem {

        @Test
        @DisplayName("Should create an item with success")
        void shouldCreateItem(){

            User seller = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size", seller);

            doReturn(item).when(itemRepository).save(itemArgumentCaptor.capture());

            itemService.save(item);

            verify(itemRepository, Mockito.times(1)).save(itemArgumentCaptor.getValue());

        }

    }

    @Nested
    class findItemById {

        @Test
        @DisplayName("Should return the item by id")
        void shouldReturnItemById() {

            User seller = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size", seller);

            doReturn(Optional.of(item)).when(itemRepository).findById(longArgumentCaptor.capture());

            var output = itemService.findById(item.getId());

            assertTrue(output.isPresent());
            assertEquals(item.getId(), longArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("Should throw ItemNotFoundException when item is not found")
        void shouldThrowExceptionWhenItemIsNotFound() {

            User seller = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size", seller);

            doReturn(Optional.empty()).when(itemRepository).findById(longArgumentCaptor.capture());


            assertThrows(ItemNotFoundException.class, () -> itemService.findById(item.getId()));

        }
    }

    @Nested
    class deleteItem {

        @Test
        @DisplayName("should delete user with success")
        void shouldDeleteUser() {

            User seller = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size", seller);

            doNothing().when(itemRepository).deleteById(longArgumentCaptor.capture());

            itemService.delete(item.getId());

            assertEquals(item.getId(), longArgumentCaptor.getValue());

            verify(itemRepository, times(1)).deleteById(longArgumentCaptor.getValue());

        }


    }

    @Nested
    class findAllItems {

        @Test
        @DisplayName("should return a list with all items")
        void shouldFindAllItems() {

            User seller = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size", seller);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Item> itemPage = new PageImpl<>(List.of(item), pageable, 0);


            doReturn(itemPage).when(itemRepository).findAll(any(PageRequest.class));

            var output = itemService.findAll(PageRequest.of(0, 10, Sort.Direction.DESC, "listedAt"));

            assertNotNull(output);
            assertEquals(1, output.getTotalElements());
        }

    }

}
