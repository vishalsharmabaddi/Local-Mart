package com.localmart.backend.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localmart.backend.common.BadRequestException;
import com.localmart.backend.product.Product;
import com.localmart.backend.product.ProductNotFoundException;
import com.localmart.backend.product.ProductRepository;
import com.localmart.backend.security.CurrentUser;
import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopNotFoundException;
import com.localmart.backend.shop.ShopRepository;
import com.localmart.backend.user.User;
import com.localmart.backend.user.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        ShopRepository shopRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    // ALL or NOTHING: if any line fails, every stock change and the order itself are rolled back
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request, CurrentUser currentUser) {
        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new ShopNotFoundException(request.shopId()));
        User customer = userRepository.getReferenceById(currentUser.id());   // from the token

        Order order = new Order(customer, shop);

        for (OrderItemRequest line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ProductNotFoundException(line.productId()));

            if (!product.getShop().getId().equals(shop.getId())) {
                throw new BadRequestException(product.getName() + " is not sold by " + shop.getName());
            }

            product.decreaseStock(line.quantity());   // no save() needed: Hibernate notices the change
            order.addItem(product, line.quantity());   // price comes from the DB, not the client
        }

        return OrderResponse.from(orderRepository.save(order));   // cascade saves the items too
    }
}
