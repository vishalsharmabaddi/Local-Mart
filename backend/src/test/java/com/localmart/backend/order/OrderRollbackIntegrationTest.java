package com.localmart.backend.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.localmart.backend.product.InsufficientStockException;
import com.localmart.backend.product.Product;
import com.localmart.backend.product.ProductRepository;
import com.localmart.backend.security.CurrentUser;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.UserRepository;

// NOT @Transactional on purpose: we want the service's OWN transaction to commit or roll back for real
@SpringBootTest
class OrderRollbackIntegrationTest {

    @Autowired OrderService orderService;
    @Autowired ProductRepository productRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired UserRepository userRepository;

    Product seeded(String name) {
        return productRepository.findAll().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst().orElseThrow();
    }

    @Test
    void whenSecondItemFails_firstItemsStockIsNotReduced_andNoOrderIsSaved() {
        Product atta = seeded("Aashirvaad Atta 5kg");
        Product namak = seeded("Tata Namak 1kg");
        Long geetaId = userRepository.findByPhone("9111111111").orElseThrow().getId();

        int attaStockBefore = atta.getStock();
        long ordersBefore = orderRepository.count();

        // line 1 (atta) is fine, line 2 (namak) asks for more than the stock → whole order must fail
        PlaceOrderRequest request = new PlaceOrderRequest(atta.getShop().getId(), List.of(
                new OrderItemRequest(atta.getId(), 2),
                new OrderItemRequest(namak.getId(), namak.getStock() + 1)));

        assertThrows(InsufficientStockException.class,
                () -> orderService.placeOrder(request, new CurrentUser(geetaId, Role.CUSTOMER)));

        assertEquals(attaStockBefore, productRepository.findById(atta.getId()).orElseThrow().getStock());
        assertEquals(ordersBefore, orderRepository.count());
    }
}
