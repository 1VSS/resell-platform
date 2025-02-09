package br.com.vss.resell_platform.controller.dto;

import br.com.vss.resell_platform.util.Condition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemRequest (
        @NotNull(message = "Title cant be empty.")
        @NotBlank(message = "Title cant be blank.")
        String name,
        String brand,
        Condition condition,
        @Min(value = 1, message = "Price must be above 0.")
        BigDecimal price,
        String size
) {}
