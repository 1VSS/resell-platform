package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.SubCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommissionServiceTest {

    @InjectMocks
    private CommissionService commissionService;

    @Test
    @DisplayName("Should calculate commission correctly")
    void shouldCalculateCommission() {

        User seller = new User("username", "password", "email");
        Item item = new Item("name", "brand", Category.BOTTOMS, SubCategory.TROUSERS, Condition.NEW, new BigDecimal("1000"), "size", seller);

        BigDecimal commissionPercentage = new BigDecimal("0.10");

        var output = commissionService.calculateCommission(item);

        assertEquals(item.getPrice().multiply(commissionPercentage), output);
    }

}