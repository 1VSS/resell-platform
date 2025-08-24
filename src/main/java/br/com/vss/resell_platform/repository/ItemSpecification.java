package br.com.vss.resell_platform.repository;

import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.SubCategory;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;


public class ItemSpecification {

    public static Specification<Item> likeName(String name) {
        if (name != null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("name"), "%" + name + "%");
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<Item> likeBrand(String brand) {
        if (brand != null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("brand"), "%" + brand + "%");
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

    }
    public static Specification<Item> byCondition(Condition condition) {
        if (condition != null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("condition"), condition);
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<Item> bySize(String size) {
        if (size!= null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("size"), size);
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<Item> byCategory(Category category) {
        if (category != null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), category);
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<Item> bySubcategory(SubCategory subCategory) {
        if (subCategory != null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("subCategory"), subCategory);
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<Item> betweenPrice(BigDecimal lower, BigDecimal highest) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), lower, highest);
    }

}
