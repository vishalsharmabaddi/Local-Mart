package com.localmart.backend.product;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.localmart.backend.shop.ShopRepository;

@SpringBootTest   // starts the full app with the real Postgres DB
class ProductServiceConcurrencyTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ShopRepository shopRepository;

    @Test
    void addProduct_fromManyThreadsAtOnce_givesEveryProductAUniqueId() throws InterruptedException {
        int requests = 500;
        Long shopId = shopRepository.findAll().get(0).getId();   // the seeded shop
        Queue<Long> savedIds = new ConcurrentLinkedQueue<>();

        ExecutorService pool = Executors.newFixedThreadPool(20);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requests);

        for (int i = 0; i < requests; i++) {
            pool.submit(() -> {
                try {
                    startSignal.await();
                    ProductResponse saved = productService.addProduct(new CreateProductRequest("Test", 10, 1, shopId));
                    savedIds.add(saved.id());
                } finally {
                    done.countDown();
                }
                return null;
            });
        }

        startSignal.countDown();
        done.await();
        pool.shutdown();

        try {
            List<Long> ids = List.copyOf(savedIds);
            assertEquals(requests, ids.size());                                  // every save returned
            assertEquals(requests, ids.stream().distinct().count());             // no duplicate ids
            assertEquals(requests, productRepository.findAllById(ids).size());   // all really in DB
        } finally {
            productRepository.deleteAllById(savedIds);   // clean up test data
        }
    }
}
