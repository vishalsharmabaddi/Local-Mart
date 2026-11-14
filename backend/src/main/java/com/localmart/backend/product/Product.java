package com.localmart.backend.product;

import com.localmart.backend.shop.Shop;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)   // many products -> one shop
    @JoinColumn(name = "shop_id")                           // foreign key column in product table
    private Shop shop;

    protected Product() {
        // JPA needs an empty constructor to create objects from DB rows
    }

    public Product(String name, int price, int stock, Shop shop) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.shop = shop;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getStock() { return stock; }

    public Shop getShop() { return shop; }

    // the product guards its own rule: stock can never go below 0
    public void decreaseStock(int quantity) {
        if (quantity > stock) {
            throw new InsufficientStockException(name, stock, quantity);
        }
        stock -= quantity;
    }
}
