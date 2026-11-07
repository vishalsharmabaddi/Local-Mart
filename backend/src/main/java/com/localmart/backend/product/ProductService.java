package com.localmart.backend.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final Map<Long, Product> products = new HashMap<>();

    public ProductService() {
        products.put(1L, new Product(1L, "Aashirvaad Atta 5kg", 260, 10));
        products.put(2L, new Product(2L, "Fortune Sarson Tel 1L", 180, 5));
        products.put(3L, new Product(3L, "Tata Namak 1kg", 28, 30));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product getProductById(Long id) {
        Product product = products.get(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }
        return product;
    }
}
