package com.localmart.backend.shop;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localmart.backend.product.ProductRepository;
import com.localmart.backend.product.ProductResponse;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public ShopService(ShopRepository shopRepository, ProductRepository productRepository) {
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    public Shop createShop(CreateShopRequest request) {
        return shopRepository.save(new Shop(request.name(), request.town(), request.phone()));
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new ShopNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsOfShop(Long shopId) {
        if (!shopRepository.existsById(shopId)) {
            throw new ShopNotFoundException(shopId);   // 404, not an empty list
        }
        return productRepository.findByShop_Id(shopId).stream()
                .map(ProductResponse::from)
                .toList();
    }
}
