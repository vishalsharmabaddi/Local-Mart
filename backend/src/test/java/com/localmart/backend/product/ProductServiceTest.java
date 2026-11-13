package com.localmart.backend.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.localmart.backend.common.ForbiddenException;
import com.localmart.backend.security.CurrentUser;
import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopNotFoundException;
import com.localmart.backend.shop.ShopRepository;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;   // fake repository, no real DB

    @Mock
    ShopRepository shopRepository;         // fake shop repository

    @InjectMocks
    ProductService productService;         // real service, with the fakes injected

    Shop rameshShop;
    CurrentUser ramesh = new CurrentUser(7L, Role.SHOP_OWNER);
    CurrentUser suresh = new CurrentUser(8L, Role.SHOP_OWNER);   // another shop owner
    CurrentUser admin = new CurrentUser(1L, Role.ADMIN);

    @BeforeEach
    void setUp() {
        User owner = new User("Ramesh", "9876543210", "hash", Role.SHOP_OWNER);
        ReflectionTestUtils.setField(owner, "id", 7L);   // normally the DB sets the id
        rameshShop = new Shop("Sharma Kirana Store", "Baddi", "9876543210", owner);
    }

    @Test
    void getProductById_whenProductExists_returnsProduct() {
        // Arrange: teach the fake what to return
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(new Product("Aashirvaad Atta 5kg", 260, 10, rameshShop)));

        // Act
        ProductResponse product = productService.getProductById(1L);

        // Assert
        assertEquals("Aashirvaad Atta 5kg", product.name());
        assertEquals("Sharma Kirana Store", product.shopName());
    }

    @Test
    void getProductById_whenProductMissing_throwsNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void addProduct_byShopOwner_toOwnShop_saves() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(rameshShop));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));   // return what was saved

        ProductResponse saved = productService.addProduct(
                new CreateProductRequest("Dettol Soap", 45, 20, 1L), ramesh);

        assertEquals("Dettol Soap", saved.name());
        assertEquals(45, saved.price());
        assertEquals(20, saved.stock());
        assertEquals("Sharma Kirana Store", saved.shopName());
        verify(productRepository).save(any(Product.class));   // was save() really called?
    }

    @Test
    void addProduct_byOtherShopOwner_isForbidden_andSavesNothing() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(rameshShop));

        assertThrows(ForbiddenException.class, () -> productService.addProduct(
                new CreateProductRequest("Fake Atta", 1, 100, 1L), suresh));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void addProduct_byAdmin_toAnyShop_saves() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(rameshShop));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.addProduct(new CreateProductRequest("Dettol Soap", 45, 20, 1L), admin);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProduct_whenShopMissing_throwsAndSavesNothing() {
        when(shopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ShopNotFoundException.class, () -> productService.addProduct(
                new CreateProductRequest("Dettol Soap", 45, 20, 99L), ramesh));

        verify(productRepository, never()).save(any(Product.class));
    }
}
