package com.localmart.backend.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localmart.backend.common.BadRequestException;
import com.localmart.backend.common.ForbiddenException;
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

    // WHO may change status:
    //   shop owner / admin → any move the state machine allows
    //   the customer       → only CANCEL, and only while still PLACED (shop hasn't accepted yet)
    //   anyone else        → 404 (same as viewing)
    @Transactional
    public OrderResponse changeStatus(Long orderId, OrderStatus newStatus, CurrentUser currentUser) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        boolean isShopSide = currentUser.isAdmin() || order.getShop().isOwnedBy(currentUser.id());
        boolean isCustomer = order.getCustomer().getId().equals(currentUser.id());

        if (!isShopSide && !isCustomer) {
            throw new OrderNotFoundException(orderId);
        }
        if (!isShopSide) {
            if (newStatus != OrderStatus.CANCELLED) {
                throw new ForbiddenException("Customers can only cancel an order");
            }
            if (order.getStatus() == OrderStatus.ACCEPTED) {   // only this case gets the special message
                throw new InvalidStatusChangeException("The shop has already accepted this order, it can't be cancelled now");
            }
            // other states (OUT_FOR_DELIVERY, DELIVERED, CANCELLED) → the state machine below gives the right message
        }

        order.changeStatus(newStatus);   // state machine + restock on cancel; dirty checking saves it
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(CurrentUser currentUser) {
        return orderRepository.findByCustomer_IdOrderByCreatedAtDesc(currentUser.id()).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getShopOrders(Long shopId, CurrentUser currentUser) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId));

        // shops are public anyway, so 403 reveals nothing new
        if (!currentUser.isAdmin() && !shop.isOwnedBy(currentUser.id())) {
            throw new ForbiddenException("You can only see orders of your own shop");
        }
        return orderRepository.findByShop_IdOrderByCreatedAtDesc(shopId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId, CurrentUser currentUser) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        boolean allowed = currentUser.isAdmin()
                || order.getCustomer().getId().equals(currentUser.id())   // the customer who ordered
                || order.getShop().isOwnedBy(currentUser.id());           // the shop that sells

        if (!allowed) {
            // 404, NOT 403: a stranger shouldn't even learn that order #id exists
            throw new OrderNotFoundException(orderId);
        }
        return OrderResponse.from(order);
    }
}
