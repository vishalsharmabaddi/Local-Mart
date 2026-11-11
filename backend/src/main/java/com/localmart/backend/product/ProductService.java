package com.localmart.backend.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopNotFoundException;
import com.localmart.backend.shop.ShopRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    public ProductService(ProductRepository productRepository, ShopRepository shopRepository) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product addProduct(CreateProductRequest request) {
        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new ShopNotFoundException(request.shopId()));

        Product product = new Product(request.name(), request.price(), request.stock(), shop);
        return productRepository.save(product);
    }
}
