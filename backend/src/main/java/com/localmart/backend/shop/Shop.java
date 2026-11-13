package com.localmart.backend.shop;

import com.localmart.backend.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String town;

    @Column(nullable = false, length = 10)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)   // one owner can have many shops
    @JoinColumn(name = "owner_id")
    private User owner;

    protected Shop() {
    }

    public Shop(String name, String town, String phone, User owner) {
        this.name = name;
        this.town = town;
        this.phone = phone;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getTown() { return town; }
    public String getPhone() { return phone; }
    public User getOwner() { return owner; }

    public boolean isOwnedBy(Long userId) {
        return owner.getId().equals(userId);
    }
}
