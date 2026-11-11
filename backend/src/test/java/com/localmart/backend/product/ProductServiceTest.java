package com.localmart.backend.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopNotFoundException;
import com.localmart.backend.shop.ShopRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;   // fake repository, no real DB

    @Mock
    ShopRepository shopRepository;         // fake shop repository

    @InjectMocks
    ProductService productService;         // real service, with the fakes injected

    Shop shop = new Shop("Sharma Kirana Store", "Baddi", "9876543210");

    @Test
    void getProductById_whenProductExists_returnsProduct() {
        // Arrange: teach the fake what to return
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(new Product("Aashirvaad Atta 5kg", 260, 10, shop)));

        // Act
        Product product = productService.getProductById(1L);

        // Assert
        assertEquals("Aashirvaad Atta 5kg", product.getName());
    }

    @Test
    void getProductById_whenProductMissing_throwsNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void addProduct_savesProductWithRequestDataAndShop() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));   // return what was saved

        Product saved = productService.addProduct(new CreateProductRequest("Dettol Soap", 45, 20, 1L));

        assertEquals("Dettol Soap", saved.getName());
        assertEquals(45, saved.getPrice());
        assertEquals(20, saved.getStock());
        assertEquals(shop, saved.getShop());
        verify(productRepository).save(any(Product.class));   // was save() really called?
    }

    @Test
    void addProduct_whenShopMissing_throwsAndSavesNothing() {
        when(shopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ShopNotFoundException.class,
                () -> productService.addProduct(new CreateProductRequest("Dettol Soap", 45, 20, 99L)));

        verify(productRepository, never()).save(any(Product.class));   // nothing saved
    }
}
