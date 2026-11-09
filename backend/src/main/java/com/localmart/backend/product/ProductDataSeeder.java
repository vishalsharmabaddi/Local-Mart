package com.localmart.backend.product;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductDataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductDataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            productRepository.saveAll(List.of(
                    new Product("Aashirvaad Atta 5kg", 260, 10),
                    new Product("Fortune Sarson Tel 1L", 180, 5),
                    new Product("Tata Namak 1kg", 28, 30)));
        }
    }
}
