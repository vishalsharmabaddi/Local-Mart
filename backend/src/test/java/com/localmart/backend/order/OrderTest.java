package com.localmart.backend.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.localmart.backend.product.Product;
import com.localmart.backend.shop.Shop;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;

// plain Java test: no Spring, no DB, no mocks → runs in milliseconds
class OrderTest {

    User geeta = new User("Geeta", "9111111111", "hash", Role.CUSTOMER);
    User ramesh = new User("Ramesh", "9876543210", "hash", Role.SHOP_OWNER);
    Shop shop = new Shop("Sharma Kirana Store", "Baddi", "9876543210", ramesh);
    Product atta = new Product("Aashirvaad Atta 5kg", 260, 10, shop);
    Product namak = new Product("Tata Namak 1kg", 28, 30, shop);

    @Test
    void newOrder_startsAsPlaced_withZeroTotal() {
        Order order = new Order(geeta, shop);

        assertEquals(OrderStatus.PLACED, order.getStatus());
        assertEquals(0, order.getTotalAmount());
    }

    @Test
    void addItem_calculatesTotal() {
        Order order = new Order(geeta, shop);

        order.addItem(atta, 2);    // 2 × 260 = 520
        order.addItem(namak, 1);   // 1 × 28  = 28

        assertEquals(548, order.getTotalAmount());
        assertEquals(2, order.getItems().size());
    }

    @Test
    void item_keepsPriceSnapshot() {
        Order order = new Order(geeta, shop);
        order.addItem(atta, 1);

        OrderItem item = order.getItems().get(0);
        assertEquals("Aashirvaad Atta 5kg", item.getProductName());
        assertEquals(260, item.getUnitPrice());
    }

    @Test
    void cancel_putsStockBack() {
        Order order = new Order(geeta, shop);
        order.addItem(atta, 2);   // atta stock is 10 (addItem itself doesn't touch stock)

        order.changeStatus(OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(12, atta.getStock());   // 10 + 2 returned
    }

    @Test
    void illegalMove_throws_andStatusStaysTheSame() {
        Order order = new Order(geeta, shop);

        assertThrows(InvalidStatusChangeException.class, () -> order.changeStatus(OrderStatus.DELIVERED));
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void items_cannotBeChangedFromOutside() {
        Order order = new Order(geeta, shop);

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().clear());
    }
}
