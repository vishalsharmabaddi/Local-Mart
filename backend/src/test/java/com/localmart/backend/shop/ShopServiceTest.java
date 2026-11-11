package com.localmart.backend.shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.localmart.backend.product.Product;
import com.localmart.backend.product.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    ShopRepository shopRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ShopService shopService;

    @Test
    void createShop_savesShopWithRequestData() {
        when(shopRepository.save(any(Shop.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shop saved = shopService.createShop(new CreateShopRequest("Gupta Medical", "Nalagarh", "9123456780"));

        assertEquals("Gupta Medical", saved.getName());
        assertEquals("Nalagarh", saved.getTown());
    }

    @Test
    void getProductsOfShop_whenShopExists_returnsItsProducts() {
        Shop shop = new Shop("Sharma Kirana Store", "Baddi", "9876543210");
        when(shopRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findByShop_Id(1L))
                .thenReturn(List.of(new Product("Tata Namak 1kg", 28, 30, shop)));

        List<Product> products = shopService.getProductsOfShop(1L);

        assertEquals(1, products.size());
        assertEquals("Tata Namak 1kg", products.get(0).getName());
    }

    @Test
    void getProductsOfShop_whenShopMissing_throwsNotFound() {
        when(shopRepository.existsById(99L)).thenReturn(false);

        assertThrows(ShopNotFoundException.class, () -> shopService.getProductsOfShop(99L));

        verify(productRepository, never()).findByShop_Id(anyLong());   // didn't even search products
    }
}
