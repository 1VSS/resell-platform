package br.com.vss.resell_platform.controller;

import br.com.vss.resell_platform.controller.dto.FeedDto;
import br.com.vss.resell_platform.controller.dto.FeedItemDto;
import br.com.vss.resell_platform.controller.dto.ItemRequest;
import br.com.vss.resell_platform.exceptions.InvalidOwnerException;
import br.com.vss.resell_platform.mapper.ItemMapper;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.service.ItemService;
import br.com.vss.resell_platform.service.TransactionService;
import br.com.vss.resell_platform.service.UserService;
import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.SubCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private ItemController itemController;

    private Authentication authentication;

    @Nested
    class postItem {

        @Test
        @DisplayName("Should post item successfully")
        void shouldPostItem() {


            User user = new User("username", "password", "email");
            ItemRequest itemRequest = new ItemRequest("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size",  user);

            var authentication = Mockito.mock(Authentication.class);

            Mockito.doReturn(user.getUsername()).when(authentication).getName();
            Mockito.doReturn(Optional.of(user)).when(userService).findByUsername(user.getUsername());
            Mockito.doReturn(item).when(itemMapper).toItem(itemRequest, user);
            Mockito.doReturn(item).when(itemService).save(item);

            var response = itemController.postItem(authentication, itemRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());

        }
    }

    @Nested
    class deleteItem {

        @Test
        @DisplayName("Should delete item successfully")
        void shouldDeleteItem() {

            User user = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size",  user);
            item.setId(1L);

            var authentication = Mockito.mock(Authentication.class);

            Mockito.doReturn(Optional.of(item)).when(itemService).findById(item.getId());
            Mockito.doReturn(user.getUsername()).when(authentication).getName();

            var response = itemController.deleteItem(authentication, item.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("Should throw invalid owner exception when user is different than item owner")
        void shouldThrowInvalidOwnerException() {

            User user = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size",  user);
            item.setId(1L);

            var authentication = Mockito.mock(Authentication.class);

            Mockito.doReturn(Optional.of(item)).when(itemService).findById(item.getId());
            Mockito.doReturn("new username").when(authentication).getName();

            InvalidOwnerException exception = assertThrows(InvalidOwnerException.class, () -> itemController.deleteItem(authentication, item.getId()));

            assertEquals(exception.getMessage(), "This user is not the owner of this Item.");

        }
    }

    @Nested
    class PurchaseItem {

        @Test
        @DisplayName("Should purchase item successfully")
        void shouldPurchaseItem() {

            User user = new User("username", "password", "email");
            User user2 = new User("username2", "password2", "email2");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size",  user);

            item.setId(1L);

            var authentication = Mockito.mock(Authentication.class);

            Mockito.doReturn(Optional.of(item)).when(itemService).findById(item.getId());
            Mockito.doReturn(user.getUsername()).when(authentication).getName();
            Mockito.doReturn(Optional.of(user)).when(userService).findByUsername(user.getUsername());
            Mockito.lenient().doNothing().when(transactionService).purchaseItem(user, user2, item);

            var response = itemController.purchaseItem(authentication, item.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());

        }

    }

    @Nested
    class editItem {

        @Test
        @DisplayName("Should edit item successfully")
        void shouldEditItem() {

            User user = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size",  user);
            ItemRequest editedItem = new ItemRequest("new name", "new brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.WORN, new BigDecimal("500"), "new size");
            item.setId(1L);

            var authentication = Mockito.mock(Authentication.class);

            Mockito.doReturn(Optional.of(item)).when(itemService).findById(item.getId());
            Mockito.doReturn(user.getUsername()).when(authentication).getName();
            Mockito.doReturn(item).when(itemService).save(item);

            var response = itemController.editItem(authentication, editedItem, item.getId());

            assertEquals(editedItem.name(), item.getName());
            assertEquals(editedItem.brand(), item.getBrand());
            assertEquals(editedItem.condition(), item.getCondition());
            assertEquals(editedItem.price(), item.getPrice());
            assertEquals(editedItem.size(), item.getSize());

            assertEquals(HttpStatus.OK, response.getStatusCode());

        }

        @Test
        @DisplayName("Should throw invalid user exception when user is different than item owner")
        void shouldThrowInvalidUserException() {

            User user = new User("username", "password", "email");
            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size",  user);
            ItemRequest editedItem = new ItemRequest("new name", "new brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.WORN, new BigDecimal("500"), "new size");
            item.setId(1L);

            var authentication = Mockito.mock(Authentication.class);

            Mockito.doReturn(Optional.of(item)).when(itemService).findById(item.getId());
            Mockito.doReturn("new username").when(authentication).getName();

            InvalidOwnerException exception = assertThrows(InvalidOwnerException.class, () -> itemController.editItem(authentication, editedItem, item.getId()));

            assertEquals("This user is not the owner of this Item.", exception.getMessage());

        }


    }

//    @Nested
//    class itemFeed {
//
//        @Test
//        @DisplayName("Should return feed with items")
//        void shouldReturnFeed() {
//
//            User user = new User("username", "password", "email");
//            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size",  user);
//            Item item2 = new Item("name2", "brand2", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size2",  user);
//            Item item3 = new Item("name3", "brand3", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size3",  user);
//
//            FeedItemDto itemDto = new FeedItemDto("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size",  user.getUsername());
//            FeedItemDto itemDto2 = new FeedItemDto("name2", "brand2", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size2",  user.getUsername());
//            FeedItemDto itemDto3 = new FeedItemDto("name3", "brand3", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size3",  user.getUsername());
//
//            Pageable pageable = PageRequest.of(0, 10);
//            Page<Item> itemPage = new PageImpl<>(List.of(item, item2, item3), pageable, 0);
//            SerializablePage<Item> serializablePage = new SerializablePage<>(itemPage);
//
//            FeedDto feedDto = new FeedDto(List.of(itemDto, itemDto2, itemDto3), pageable.getPageNumber(), pageable.getPageSize(), itemPage.getTotalElements());
//
//            Mockito.doReturn(itemPage).when(itemService).findAll(any((PageRequest.class)));
//
//            var output = itemController.filter(null, null, null, null, null, null, null, null,
//                                                pageable.getPageNumber(), pageable.getPageSize());
//
//            assertEquals(HttpStatus.OK, output.getStatusCode());
//            assertEquals(feedDto, output.getBody());
//
//        }
//    }

}