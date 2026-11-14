package com.localmart.backend.order;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.localmart.backend.product.Product;
import com.localmart.backend.shop.Shop;
import com.localmart.backend.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "orders")   // "order" is a reserved SQL word (ORDER BY)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalAmount;

    @Column(nullable = false)
    private Instant createdAt;

    // customer cancels while the shop accepts at the same moment → only one may win
    @Version
    private long version;

    // one order → many items. "mappedBy" = the FK lives in OrderItem.order (order_id column)
    // cascade ALL = saving the order also saves its items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(User customer, Shop shop) {
        this.customer = customer;
        this.shop = shop;
        this.status = OrderStatus.PLACED;
        this.createdAt = Instant.now();
    }

    // the ONLY way to add items → total can never go out of sync
    public void addItem(Product product, int quantity) {
        OrderItem item = new OrderItem(this, product, quantity);
        items.add(item);
        totalAmount += item.getLineTotal();
    }

    // the ONLY way to change status → the state machine can't be bypassed
    public void changeStatus(OrderStatus next) {
        if (!status.canMoveTo(next)) {
            throw new InvalidStatusChangeException("Cannot change order from " + status + " to " + next);
        }
        if (next == OrderStatus.CANCELLED) {
            for (OrderItem item : items) {
                item.getProduct().increaseStock(item.getQuantity());   // put stock back
            }
        }
        status = next;
    }

    public Long getId() { return id; }
    public User getCustomer() { return customer; }
    public Shop getShop() { return shop; }
    public OrderStatus getStatus() { return status; }
    public int getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }   // read-only view
}
