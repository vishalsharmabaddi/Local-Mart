package com.localmart.backend.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

    protected Shop() {
    }

    public Shop(String name, String town, String phone) {
        this.name = name;
        this.town = town;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getTown() { return town; }
    public String getPhone() { return phone; }
}
