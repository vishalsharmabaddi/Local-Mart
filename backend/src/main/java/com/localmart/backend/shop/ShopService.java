package com.localmart.backend.shop;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localmart.backend.product.ProductRepository;
import com.localmart.backend.product.ProductResponse;
import com.localmart.backend.user.User;
import com.localmart.backend.user.UserRepository;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ShopService(ShopRepository shopRepository, ProductRepository productRepository,
                       UserRepository userRepository) {
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShopResponse createShop(CreateShopRequest request, Long ownerId) {
        // owner comes from the TOKEN, never from the request body → nobody can create a shop "for" someone else
        User owner = userRepository.getReferenceById(ownerId);   // just a reference, no SELECT needed
        Shop shop = new Shop(request.name(), request.town(), request.phone(), owner);
        return ShopResponse.from(shopRepository.save(shop));
    }

    @Transactional(readOnly = true)
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(ShopResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShopResponse getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ShopNotFoundException(id));
        return ShopResponse.from(shop);
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
