package com.localmart.backend.product;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

class ProductServiceConcurrencyTest {

    @Test
    void addProduct_fromManyThreadsAtOnce_shouldNotLoseProducts() throws InterruptedException {
        ProductService service = new ProductService();   // starts with 3 products
        int requests = 10_000;

        ExecutorService pool = Executors.newFixedThreadPool(50);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requests);

        for (int i = 0; i < requests; i++) {
            pool.submit(() -> {
                try {
                    startSignal.await();
                    service.addProduct(new CreateProductRequest("Test", 10, 1));
                } finally {
                    done.countDown();
                }
                return null;
            });
        }

        startSignal.countDown();   // sab threads ek saath shuru
        done.await();              // sabke khatam hone ka intezaar
        pool.shutdown();

        assertEquals(3 + requests, service.getAllProducts().size());
    }
}
