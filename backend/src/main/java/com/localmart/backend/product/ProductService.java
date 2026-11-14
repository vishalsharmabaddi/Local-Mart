package com.localmart.backend.product;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localmart.backend.common.BadRequestException;
import com.localmart.backend.common.ForbiddenException;
import com.localmart.backend.common.PageResponse;
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

    // only these fields may be used in ?sort=...  (anything else → 400)
    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "name", "price", "stock");

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Cannot sort by '" + order.getProperty()
                        + "'. Allowed: " + String.join(", ", SORTABLE_FIELDS.stream().sorted().toList()));
            }
        }
        return PageResponse.from(productRepository.findAllWithShop(pageable), ProductResponse::from);
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
