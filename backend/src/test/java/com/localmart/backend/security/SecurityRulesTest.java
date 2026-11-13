package com.localmart.backend.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.localmart.backend.shop.Shop;
import com.localmart.backend.shop.ShopRepository;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;
import com.localmart.backend.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc   // gives a MockMvc: sends fake HTTP requests through the real security filters
@Transactional          // anything saved during a test is rolled back afterwards
class SecurityRulesTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ShopRepository shopRepository;

    User customer;
    User shopOwner;
    User otherShopOwner;
    Shop ownersShop;

    String newShopJson = """
            {"name":"Test Shop","town":"Baddi","phone":"9000000009"}
            """;

    @BeforeEach
    void setUp() {
        customer = userRepository.save(new User("Test Customer", "8000000001", "x", Role.CUSTOMER));
        shopOwner = userRepository.save(new User("Test Owner", "8000000002", "x", Role.SHOP_OWNER));
        otherShopOwner = userRepository.save(new User("Other Owner", "8000000003", "x", Role.SHOP_OWNER));
        ownersShop = shopRepository.save(new Shop("Owner Shop", "Baddi", "8000000002", shopOwner));
    }

    // a real signed token, same as login would give
    String tokenFor(User user) {
        return "Bearer " + jwtService.generateToken(user);
    }

    String productJson(Long shopId) {
        return """
                {"name":"Fake Atta","price":1,"stock":100,"shopId":%d}
                """.formatted(shopId);
    }

    @Test
    void anyoneCanViewProducts_withoutToken() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    void createShop_withoutToken_is401() throws Exception {
        mockMvc.perform(post("/shops").contentType(MediaType.APPLICATION_JSON).content(newShopJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void createShop_asCustomer_is403() throws Exception {
        mockMvc.perform(post("/shops")
                        .header("Authorization", tokenFor(customer))
                        .contentType(MediaType.APPLICATION_JSON).content(newShopJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void createShop_asShopOwner_is201_andOwnerComesFromToken() throws Exception {
        mockMvc.perform(post("/shops")
                        .header("Authorization", tokenFor(shopOwner))
                        .contentType(MediaType.APPLICATION_JSON).content(newShopJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Shop"))
                .andExpect(jsonPath("$.ownerId").value(shopOwner.getId()));
    }

    @Test
    void garbageToken_is401() throws Exception {
        mockMvc.perform(post("/shops")
                        .header("Authorization", "Bearer abc.def.ghi")
                        .contentType(MediaType.APPLICATION_JSON).content(newShopJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addProduct_toOwnShop_is201() throws Exception {
        mockMvc.perform(post("/products")
                        .header("Authorization", tokenFor(shopOwner))
                        .contentType(MediaType.APPLICATION_JSON).content(productJson(ownersShop.getId())))
                .andExpect(status().isCreated());
    }

    @Test
    void addProduct_toSomeoneElsesShop_is403() throws Exception {
        mockMvc.perform(post("/products")
                        .header("Authorization", tokenFor(otherShopOwner))
                        .contentType(MediaType.APPLICATION_JSON).content(productJson(ownersShop.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You can only add products to your own shop"));
    }
}
