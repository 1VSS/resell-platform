package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.ItemNotAvailableException;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.Transaction;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.TransactionRepository;
import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.ItemStatus;
import br.com.vss.resell_platform.util.SubCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CommissionService commissionService;

    @InjectMocks
    private TransactionService transactionService;

    @Captor
    private ArgumentCaptor<Transaction> transactionArgumentCaptor;

    @Nested
    class purchaseItem {

        @Test
        @DisplayName("Should buy item successfully")
        void shouldBuyItemSuccessfully() {

            var commissionRate = new BigDecimal("0.10");
            User user = new User("username", "password", "email");
            User user2 = new User("username2", "password2", "email2");


            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size", user);;


            Transaction transaction = new Transaction(user, user2, item);

            doReturn(item.getPrice().multiply(commissionRate)).when(commissionService).calculateCommission(item);
            doReturn(transaction).when(transactionRepository).save(transactionArgumentCaptor.capture());

            var payout = item.getPrice().subtract(item.getPrice().multiply(commissionRate));

            transactionService.purchaseItem(user, user2, item);

            assertEquals(user2.getBalance(), payout);
            assertEquals(item.getStatus(), ItemStatus.SOLD);
            verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());

        }

        @Test
        @DisplayName("Should throw ItemNotAvailableException when item is not available for purchase")
        void shouldThrowExceptionWhenItemIsNotAvailable() {

            var commissionRate = new BigDecimal("0.10");
            User user = new User("username", "password", "email");
            User user2 = new User("username2", "password2", "email2");


            Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal(1000), "size", user);

            item.setStatus(ItemStatus.SOLD);

            doReturn(item.getPrice().multiply(commissionRate)).when(commissionService).calculateCommission(item);

            assertThrows(ItemNotAvailableException.class, () -> transactionService.purchaseItem(user, user, item));
        }

    }
}