package com.localmart.backend.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllWithShop().stream()   // JOIN FETCH → no N+1
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse addProduct(CreateProductRequest request) {
        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new ShopNotFoundException(request.shopId()));

        Product product = new Product(request.name(), request.price(), request.stock(), shop);
        return ProductResponse.from(productRepository.save(product));
    }
}
