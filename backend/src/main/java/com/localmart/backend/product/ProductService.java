package com.localmart.backend.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final Map<Long, Product> products = new HashMap<>();
    private long nextId = 1;

    public ProductService() {
        addProduct(new CreateProductRequest("Aashirvaad Atta 5kg", 260, 10));
        addProduct(new CreateProductRequest("Fortune Sarson Tel 1L", 180, 5));
        addProduct(new CreateProductRequest("Tata Namak 1kg", 28, 30));
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

    public Product addProduct(CreateProductRequest request) {
        Long id = nextId++;
        Product product = new Product(id, request.name(), request.price(), request.stock());
        products.put(id, product);
        return product;
    }
}
