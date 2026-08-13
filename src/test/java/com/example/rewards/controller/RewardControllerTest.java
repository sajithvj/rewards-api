package com.example.rewards.controller;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.dto.MonthlyReward;
import com.example.rewards.exception.DateRangeException;
import com.example.rewards.exception.InvalidDateFormatException;
import com.example.rewards.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RewardController.class)
class RewardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RewardService rewardService;

    @Test
    void getRewards_routesToServiceAndSerializesSuccessResult() throws Exception {
        CustomerRewardSummary alice = sampleSummary();
        given(rewardService.getRewardSummaries(isNull(), isNull()))
                .willReturn(List.of(alice));
        mockMvc.perform(get("/v1/calculateRewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerName").value("Alice Nguyen"))
                .andExpect(jsonPath("$[0].totalPoints").value(115))
                .andExpect(jsonPath("$[0].monthlyRewards").isArray())
                .andExpect(jsonPath("$[0].monthlyRewards[0].transactionIds").isArray());

    }

    @Test
    void getRewards_invalidStartDateThrowsException() throws Exception {
        given(rewardService.getRewardSummaries(any(), any()))
                .willThrow(new DateRangeException("Both start date and end date must be provided together or both must be null."));
        mockMvc.perform(get("/v1/calculateRewards?startDate=2026-06-09"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").value("Both start date and end date must be provided together or both must be null."))
                .andExpect(jsonPath("$.statusCode").exists());

    }

    @Test
    void getRewards_withValidDateRange() throws Exception {
        CustomerRewardSummary alice = sampleSummary();
        given(rewardService.getRewardSummaries(any(), any()))
                .willReturn(List.of(alice));
        mockMvc.perform(get("/v1/calculateRewards?startDate=2026-06-09&endDate=2026-08-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerName").value("Alice Nguyen"))
                .andExpect(jsonPath("$[0].totalPoints").value(115))
                .andExpect(jsonPath("$[0].monthlyRewards").isArray())
                .andExpect(jsonPath("$[0].monthlyRewards[0].transactionIds").isArray());


    }

    @Test
    void getRewards_invalidDateFormatThrowsException() throws Exception {
        given(rewardService.getRewardSummaries(any(), any()))
                .willThrow(new InvalidDateFormatException("startDate","09-08-2026"));
        mockMvc.perform(get("/v1/calculateRewards?startDate=09-08-2026&endDate=09-09-2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").value("Invalid value for parameter 'startDate': '09-08-2026' - expected format yyyy-MM-dd"))
                .andExpect(jsonPath("$.statusCode").exists());

    }

    @Test
    void getRewards_withStartDateGreaterThanEndDate() throws Exception {
        given(rewardService.getRewardSummaries(any(), any()))
                .willThrow(new DateRangeException("Start date must be before or equal to end date."));
        mockMvc.perform(get("/v1/calculateRewards?startDate=09-09-2026&endDate=09-08-2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").value("Start date must be before or equal to end date."))
                .andExpect(jsonPath("$.statusCode").exists());

    }



        private static CustomerRewardSummary sampleSummary() {
        MonthlyReward april = new MonthlyReward(2026,"AUG", 115, List.of("T0001", "T0002"));
        return new CustomerRewardSummary("C001", "Alice Nguyen", List.of(april), 115);
    }

}
