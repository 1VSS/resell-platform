package br.com.vss.resell_platform.model;

import br.com.vss.resell_platform.util.Category;
import br.com.vss.resell_platform.util.Condition;
import br.com.vss.resell_platform.util.ItemStatus;
import br.com.vss.resell_platform.util.SubCategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ITEMS")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
    String brand;
    Category category;
    SubCategory subCategory;
    Condition condition;
    BigDecimal price;
    String size;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User seller;
    ItemStatus status;
    @OneToOne(mappedBy = "item")
    Transaction transaction;
    LocalDateTime listedAt;

    public Item(String name, String brand, Category category, SubCategory subCategory, Condition condition, BigDecimal price, String size, User seller) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.subCategory = subCategory;
        this.condition = condition;
        this.price = price;
        this.size = size;
        this.seller = seller;
        this.status = ItemStatus.AVAILABLE;
        this.listedAt = LocalDateTime.now();
    }

    public Item(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public LocalDateTime getListedAt() {
        return listedAt;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", condition=" + condition +
                ", price=" + price +
                ", size='" + size + '\'' +
                ", seller=" + seller +
                ", status=" + status +
                ", listedAt=" + listedAt +
                '}';
    }
}
