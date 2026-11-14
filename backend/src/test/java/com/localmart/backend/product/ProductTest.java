package com.localmart.backend.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.localmart.backend.shop.Shop;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;

class ProductTest {

    Shop shop = new Shop("Sharma Kirana Store", "Baddi", "9876543210",
            new User("Ramesh", "9876543210", "hash", Role.SHOP_OWNER));

    @Test
    void decreaseStock_reducesStock() {
        Product atta = new Product("Aashirvaad Atta 5kg", 260, 10, shop);

        atta.decreaseStock(3);

        assertEquals(7, atta.getStock());
    }

    @Test
    void decreaseStock_moreThanAvailable_throws_andStockUnchanged() {
        Product atta = new Product("Aashirvaad Atta 5kg", 260, 2, shop);

        assertThrows(InsufficientStockException.class, () -> atta.decreaseStock(3));
        assertEquals(2, atta.getStock());   // never goes negative
    }
}
