package com.localmart.backend.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProductServiceTest {

    @Test
    void getProductById_whenProductExists_returnsProduct() {
        // Arrange
        ProductService service = new ProductService();

        // Act
        Product product = service.getProductById(1L);

        // Assert
        assertEquals("Aashirvaad Atta 5kg", product.getName());
    }

    @Test
    void getProductById_whenProductMissing_throwsNotFound() {
        ProductService service = new ProductService();

        assertThrows(ProductNotFoundException.class, () -> service.getProductById(99L));
    }

    @Test
    void addProduct_assignsNextIdAndStoresProduct() {
        ProductService service = new ProductService();   // starts with 3 products

        Product saved = service.addProduct(new CreateProductRequest("Dettol Soap", 45, 20));

        assertEquals(4L, saved.getId());
        assertEquals(4, service.getAllProducts().size());
    }
}
