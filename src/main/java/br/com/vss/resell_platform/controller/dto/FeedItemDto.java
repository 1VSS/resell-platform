package br.com.vss.resell_platform.controller.dto;

import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.SubCategory;

import java.io.Serializable;
import java.math.BigDecimal;

public record FeedItemDto(String name,
                          String brand,
                          Category category,
                          SubCategory subCategory,
                          Condition condition,
                          BigDecimal price,
                          String size,
                          String sellerUsername) implements Serializable {
}
