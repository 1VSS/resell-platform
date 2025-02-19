package br.com.vss.resell_platform.controller.dto;

import br.com.vss.resell_platform.util.Condition;

import java.math.BigDecimal;

public record FeedItemDto(String name,
                          String brand,
                          Condition condition,
                          BigDecimal price,
                          String size,
                          String sellerUsername) {
}
