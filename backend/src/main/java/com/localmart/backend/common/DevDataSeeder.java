package com.localmart.backend.common;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.localmart.backend.product.Product;
import com.localmart.backend.product.ProductRepository;
import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopRepository;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;
import com.localmart.backend.user.UserRepository;

// LOCAL DEV demo data only. These passwords must never exist in production.
@Component
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(UserRepository userRepository, ShopRepository shopRepository,
                         ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;   // already seeded
        }

        userRepository.save(new User("LocalMart Admin", "9999999999", passwordEncoder.encode("admin123"), Role.ADMIN));
        User ramesh = userRepository.save(
                new User("Ramesh Sharma", "9876543210", passwordEncoder.encode("secret123"), Role.SHOP_OWNER));
        userRepository.save(new User("Geeta Devi", "9111111111", passwordEncoder.encode("secret123"), Role.CUSTOMER));

        Shop shop = shopRepository.save(new Shop("Sharma Kirana Store", "Baddi", "9876543210", ramesh));
        productRepository.saveAll(List.of(
                new Product("Aashirvaad Atta 5kg", 260, 10, shop),
                new Product("Fortune Sarson Tel 1L", 180, 5, shop),
                new Product("Tata Namak 1kg", 28, 30, shop)));
    }
}
