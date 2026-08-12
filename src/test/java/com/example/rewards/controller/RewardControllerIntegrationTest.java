package com.example.rewards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRewards_returnsOkAndNonEmptyCustomerList() throws Exception {
        // The controller resolves via CompletableFuture, so the request is
        // processed in two stages under MockMvc: kick off the async
        // dispatch, then assert on the result once it completes.
//        MvcResult asyncResult = mockMvc.perform(get("/api/rewards"))
//                .andExpect(request().asyncStarted())
//                .andReturn();

        mockMvc.perform(get("/v1/calculateRewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerName").exists())
                .andExpect(jsonPath("$[0].totalPoints").exists())
                .andExpect(jsonPath("$[0].monthlyRewards").isArray())
                .andExpect(jsonPath("$[0].monthlyRewards[0].transactionIds").isArray());
    }

    @Test
    void getRewards_returnOkAndCustomerSummaryWithCustomerId() throws Exception {
        //Checking for a specific customerId, assuming "C001" is a valid customerId in the test database

        mockMvc.perform(get("/v1/C001/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").exists())
                .andExpect(jsonPath("$.totalPoints").exists())
                .andExpect(jsonPath("$.monthlyRewards").isArray())
                .andExpect(jsonPath("$.monthlyRewards[0].transactionIds").isArray());
    }

    @Test
    void getRewards_returnNotFoundForInvalidCustomerId() throws Exception {
        //Checking for an invalid customerId, assuming "INVALID_ID" does not exist in the test database

        mockMvc.perform(get("/api/INVALID_ID/rewards"))
                .andExpect(status().isNotFound());
    }

}
