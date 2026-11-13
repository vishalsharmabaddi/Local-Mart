package com.localmart.backend.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localmart.backend.common.ForbiddenException;
import com.localmart.backend.security.CurrentUser;
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
    public ProductResponse addProduct(CreateProductRequest request, CurrentUser currentUser) {
        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new ShopNotFoundException(request.shopId()));

        // role said "you are a shop owner"; this asks "is it YOUR shop?"
        if (!currentUser.isAdmin() && !shop.isOwnedBy(currentUser.id())) {
            throw new ForbiddenException("You can only add products to your own shop");
        }

        Product product = new Product(request.name(), request.price(), request.stock(), shop);
        return ProductResponse.from(productRepository.save(product));
    }
}
