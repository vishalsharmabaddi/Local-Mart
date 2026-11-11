package com.localmart.backend.product;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopRepository;

@Component
public class ProductDataSeeder implements CommandLineRunner {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public ProductDataSeeder(ShopRepository shopRepository, ProductRepository productRepository) {
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (shopRepository.count() == 0) {
            Shop shop = shopRepository.save(new Shop("Sharma Kirana Store", "Baddi", "9876543210"));

            productRepository.saveAll(List.of(
                    new Product("Aashirvaad Atta 5kg", 260, 10, shop),
                    new Product("Fortune Sarson Tel 1L", 180, 5, shop),
                    new Product("Tata Namak 1kg", 28, 30, shop)));
        }
    }
}
