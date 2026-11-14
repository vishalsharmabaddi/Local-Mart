package com.localmart.backend.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.localmart.backend.common.BadRequestException;
import com.localmart.backend.product.InsufficientStockException;
import com.localmart.backend.product.Product;
import com.localmart.backend.product.ProductNotFoundException;
import com.localmart.backend.product.ProductRepository;
import com.localmart.backend.security.CurrentUser;
import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopRepository;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;
import com.localmart.backend.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock ShopRepository shopRepository;
    @Mock UserRepository userRepository;

    @InjectMocks OrderService orderService;

    CurrentUser geeta = new CurrentUser(3L, Role.CUSTOMER);
    User geetaUser;
    Shop sharmaKirana;
    Product atta;    // stock 10, ₹260
    Product namak;   // stock 30, ₹28
    Product sabun;   // belongs to ANOTHER shop

    @BeforeEach
    void setUp() {
        geetaUser = withId(new User("Geeta", "9111111111", "hash", Role.CUSTOMER), 3L);
        User ramesh = withId(new User("Ramesh", "9876543210", "hash", Role.SHOP_OWNER), 2L);
        sharmaKirana = withId(new Shop("Sharma Kirana Store", "Baddi", "9876543210", ramesh), 1L);
        Shop otherShop = withId(new Shop("Verma Store", "Nalagarh", "9222222222", ramesh), 2L);

        atta = withId(new Product("Aashirvaad Atta 5kg", 260, 10, sharmaKirana), 10L);
        namak = withId(new Product("Tata Namak 1kg", 28, 30, sharmaKirana), 11L);
        sabun = withId(new Product("Lux Sabun", 35, 50, otherShop), 20L);
    }

    // DB normally sets ids; in unit tests we set them by hand
    static <T> T withId(T entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    PlaceOrderRequest order(OrderItemRequest... lines) {
        return new PlaceOrderRequest(1L, List.of(lines));
    }

    @Test
    void placeOrder_calculatesTotal_andReducesStock() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(sharmaKirana));
        when(userRepository.getReferenceById(3L)).thenReturn(geetaUser);
        when(productRepository.findById(10L)).thenReturn(Optional.of(atta));
        when(productRepository.findById(11L)).thenReturn(Optional.of(namak));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.placeOrder(
                order(new OrderItemRequest(10L, 2), new OrderItemRequest(11L, 1)), geeta);

        assertEquals(548, response.totalAmount());           // 2×260 + 1×28
        assertEquals(OrderStatus.PLACED, response.status());
        assertEquals(2, response.items().size());
        assertEquals(8, atta.getStock());                    // 10 - 2
        assertEquals(29, namak.getStock());                  // 30 - 1
    }

    @Test
    void placeOrder_whenStockTooLow_throws_andSavesNothing() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(sharmaKirana));
        when(userRepository.getReferenceById(3L)).thenReturn(geetaUser);
        when(productRepository.findById(11L)).thenReturn(Optional.of(namak));

        assertThrows(InsufficientStockException.class,
                () -> orderService.placeOrder(order(new OrderItemRequest(11L, 31)), geeta));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_withProductFromAnotherShop_isRejected() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(sharmaKirana));
        when(userRepository.getReferenceById(3L)).thenReturn(geetaUser);
        when(productRepository.findById(20L)).thenReturn(Optional.of(sabun));

        assertThrows(BadRequestException.class,
                () -> orderService.placeOrder(order(new OrderItemRequest(20L, 1)), geeta));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_withUnknownProduct_throwsNotFound() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(sharmaKirana));
        when(userRepository.getReferenceById(3L)).thenReturn(geetaUser);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> orderService.placeOrder(order(new OrderItemRequest(999L, 1)), geeta));
    }
}
