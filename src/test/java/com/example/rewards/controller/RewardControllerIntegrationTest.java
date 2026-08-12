package com.example.rewards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRewards_returnsOkAndNonEmptyCustomerList() throws Exception {


        mockMvc.perform(get("/v1/calculateRewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerName").exists())
                .andExpect(jsonPath("$[0].totalPoints").exists())
                .andExpect(jsonPath("$[0].monthlyRewards").isArray())
                .andExpect(jsonPath("$[0].monthlyRewards[0].transactionIds").isArray());
    }


}
