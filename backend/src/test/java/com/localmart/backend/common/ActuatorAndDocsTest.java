package com.localmart.backend.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.localmart.backend.security.JwtService;
import com.localmart.backend.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ActuatorAndDocsTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;
    @Autowired UserRepository userRepository;

    String adminToken() {   // the dev-seeded admin
        return "Bearer " + jwtService.generateToken(userRepository.findByPhone("9999999999").orElseThrow());
    }

    @Test
    void health_isPublic_andShowsOnlyStatus() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").doesNotExist());   // no DB/disk details for strangers
    }

    @Test
    void health_forAdmin_showsDetails() throws Exception {
        mockMvc.perform(get("/actuator/health").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.db.status").value("UP"));
    }

    @Test
    void dangerousEndpoints_areNotExposed_evenForAdmin() throws Exception {
        mockMvc.perform(get("/actuator/env").header("Authorization", adminToken()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/actuator/heapdump").header("Authorization", adminToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiDocs_areAvailableInDev() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("LocalMart API"));
    }
}
