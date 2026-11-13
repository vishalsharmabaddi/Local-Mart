package com.localmart.backend.shop;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.localmart.backend.product.ProductResponse;
import com.localmart.backend.security.CurrentUser;

@RestController
@RequestMapping("/shops")   // every URL in this class starts with /shops
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShopResponse createShop(@Valid @RequestBody CreateShopRequest request,
                                   @AuthenticationPrincipal Jwt jwt) {
        return shopService.createShop(request, CurrentUser.from(jwt).id());
    }

    @GetMapping
    public List<ShopResponse> getAllShops() {
        return shopService.getAllShops();
    }

    @GetMapping("/{id}")
    public ShopResponse getShopById(@PathVariable Long id) {
        return shopService.getShopById(id);
    }

    @GetMapping("/{id}/products")
    public List<ProductResponse> getProductsOfShop(@PathVariable Long id) {
        return shopService.getProductsOfShop(id);
    }
}
