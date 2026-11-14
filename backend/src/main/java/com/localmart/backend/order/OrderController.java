package com.localmart.backend.order;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.localmart.backend.security.CurrentUser;

@RestController   // no class-level path: one URL here starts with /shops
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@Valid @RequestBody PlaceOrderRequest request,
                                    @AuthenticationPrincipal Jwt jwt) {
        return orderService.placeOrder(request, CurrentUser.from(jwt));
    }

    @GetMapping("/orders/my")
    public List<OrderResponse> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        return orderService.getMyOrders(CurrentUser.from(jwt));
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getOrder(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return orderService.getOrder(id, CurrentUser.from(jwt));
    }

    @GetMapping("/shops/{shopId}/orders")
    public List<OrderResponse> getShopOrders(@PathVariable Long shopId, @AuthenticationPrincipal Jwt jwt) {
        return orderService.getShopOrders(shopId, CurrentUser.from(jwt));
    }
}
