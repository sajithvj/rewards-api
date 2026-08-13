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

    @Test
    void getRewards_returnsOkAndNonEmptyCustomerListWithProvidedStartDateAndEndDate() throws Exception {


        mockMvc.perform(get("/v1/calculateRewards?startDate=2026-05-09&endDate=2026-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerName").exists())
                .andExpect(jsonPath("$[0].totalPoints").exists())
                .andExpect(jsonPath("$[0].monthlyRewards").isArray())
                .andExpect(jsonPath("$[0].monthlyRewards[0].transactionIds").isArray());
    }

    @Test
    void getRewards_returns404ErrorWithProvidedStartDateOnly() throws Exception {


        mockMvc.perform(get("/v1/calculateRewards?startDate=2026-06-09"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.statusCode").exists());
    }

    @Test
    void getRewards_returns404ErrorWithProvidedWithProvidedEndDateOnly() throws Exception {


        mockMvc.perform(get("/v1/calculateRewards?endDate=2026-06-09"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.statusCode").exists());

    }

    @Test
    void getRewards_returns404ErrorWithProvidedWithInvalidDateFormat() throws Exception {

        mockMvc.perform(get("/v1/calculateRewards?startDate=09-05-2026&endDate=2026-06-30"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.statusCode").exists());

    }


}
