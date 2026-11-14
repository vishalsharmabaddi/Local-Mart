package com.localmart.backend.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.localmart.backend.product.Product;
import com.localmart.backend.product.ProductRepository;
import com.localmart.backend.security.CurrentUser;
import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopRepository;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;
import com.localmart.backend.user.UserRepository;

// 10 customers try to buy the LAST packet at the same moment. Exactly ONE must succeed.
@SpringBootTest   // no @Transactional: every thread needs its own real transaction
class LastItemRaceTest {

    static final int CUSTOMERS = 10;

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Autowired ProductRepository productRepository;
    @Autowired ShopRepository shopRepository;
    @Autowired UserRepository userRepository;

    User owner;
    Shop shop;
    Product lastAtta;
    List<User> customers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User("Race Owner", "7000000099", "x", Role.SHOP_OWNER));
        shop = shopRepository.save(new Shop("Race Shop", "Baddi", "7000000099", owner));
        lastAtta = productRepository.save(new Product("Last Atta", 260, 1, shop));   // stock = 1
        for (int i = 0; i < CUSTOMERS; i++) {
            customers.add(userRepository.save(new User("Customer " + i, "70000000" + (10 + i), "x", Role.CUSTOMER)));
        }
    }

    @AfterEach
    void cleanUp() {   // remove everything this test created
        orderRepository.deleteAll(orderRepository.findByShop_Id(shop.getId()));
        productRepository.deleteById(lastAtta.getId());   // by id: our lastAtta object is stale (old version)
        shopRepository.delete(shop);
        userRepository.deleteAll(customers);
        userRepository.delete(owner);
    }

    @Test
    void tenCustomersBuyLastItemTogether_onlyOneSucceeds() throws InterruptedException {
        AtomicInteger successes = new AtomicInteger();
        ConcurrentHashMap<String, AtomicInteger> failures = new ConcurrentHashMap<>();   // error type → count

        ExecutorService pool = Executors.newFixedThreadPool(CUSTOMERS);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(CUSTOMERS);

        for (User customer : customers) {
            pool.submit(() -> {
                try {
                    startSignal.await();   // everyone waits for the whistle
                    orderService.placeOrder(
                            new PlaceOrderRequest(shop.getId(), List.of(new OrderItemRequest(lastAtta.getId(), 1))),
                            new CurrentUser(customer.getId(), Role.CUSTOMER));
                    successes.incrementAndGet();
                } catch (Exception e) {
                    failures.computeIfAbsent(e.getClass().getSimpleName(), k -> new AtomicInteger()).incrementAndGet();
                } finally {
                    done.countDown();
                }
                return null;
            });
        }

        startSignal.countDown();   // 🏁 GO!
        done.await();
        pool.shutdown();

        int ordersInDb = orderRepository.findByShop_Id(shop.getId()).size();
        int stockInDb = productRepository.findById(lastAtta.getId()).orElseThrow().getStock();
        System.out.println("RACE RESULT → successes=" + successes + ", orders in DB=" + ordersInDb
                + ", stock in DB=" + stockInDb + ", failures=" + failures);

        assertEquals(1, successes.get(), "only one customer can get the last packet");
        assertEquals(1, ordersInDb);
        assertEquals(0, stockInDb);
    }
}
