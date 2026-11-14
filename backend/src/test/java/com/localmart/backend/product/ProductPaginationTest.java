package com.localmart.backend.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProductPaginationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void noParams_givesFirstPageOf20() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void pageSize_isCappedAt50() throws Exception {
        mockMvc.perform(get("/products").param("size", "1000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50));
    }

    @Test
    void customPageSize_isRespected() throws Exception {
        mockMvc.perform(get("/products").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void sortByAllowedField_works() throws Exception {
        mockMvc.perform(get("/products").param("sort", "price,desc"))
                .andExpect(status().isOk());
    }

    @Test
    void sortByUnknownField_is400() throws Exception {
        mockMvc.perform(get("/products").param("sort", "shop.owner.password"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
