package com.localmart.backend.shop;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.localmart.backend.product.Product;

@SpringBootTest   // real app + real Postgres: catches query mistakes that mocks can't
class ShopServiceIntegrationTest {

    @Autowired
    ShopService shopService;

    @Autowired
    ShopRepository shopRepository;

    @Test
    void getProductsOfShop_runsRealQueryAndReturnsSeededProducts() {
        Long seededShopId = shopRepository.findAll().get(0).getId();   // Sharma Kirana Store

        List<Product> products = shopService.getProductsOfShop(seededShopId);

        assertFalse(products.isEmpty());
    }
}
