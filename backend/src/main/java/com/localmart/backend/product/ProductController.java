package com.localmart.backend.product;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.localmart.backend.common.PageResponse;
import com.localmart.backend.security.CurrentUser;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    // ?page=0&size=20&sort=price,desc   (no params → page 0, 20 items, sorted by id)
    public PageResponse<ProductResponse> getAllProducts(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/products/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse addProduct(@Valid @RequestBody CreateProductRequest request,
                                      @AuthenticationPrincipal Jwt jwt) {
        return productService.addProduct(request, CurrentUser.from(jwt));
    }
}
