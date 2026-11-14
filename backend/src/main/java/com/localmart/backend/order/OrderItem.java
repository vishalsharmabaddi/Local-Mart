package com.localmart.backend.order;

import com.localmart.backend.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)   // many items → one order
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)   // which product (for reports/links)
    @JoinColumn(name = "product_id")
    private Product product;

    // SNAPSHOT: copied at order time, so later price/name changes don't rewrite old bills
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int unitPrice;

    @Column(nullable = false)
    private int quantity;

    protected OrderItem() {
    }

    OrderItem(Order order, Product product, int quantity) {   // package-private: only Order creates items
        this.order = order;
        this.product = product;
        this.productName = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
    }

    public int getLineTotal() {
        return unitPrice * quantity;
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public String getProductName() { return productName; }
    public int getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
}
