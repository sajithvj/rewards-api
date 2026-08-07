package com.example.rewards.controller;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.service.RewardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")

public class RewardController {

    private final RewardService rewardService;


    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * GET /api/rewards
     * Returns, for every customer on record, reward points earned per
     * month plus the total across the whole period.
     */
    @GetMapping("/rewards")
    public CompletableFuture<ResponseEntity<List<CustomerRewardSummary>>> getRewards() {
        return rewardService.getRewardSummaries()
                .handle((result, ex) -> {
                    if (ex != null || result == null || result.isEmpty()) {
                        // Handle the exception and return an appropriate response
//                        log.error("Failed to process order", ex);
                        throw new RuntimeException("Error occurred while fetching reward summaries", ex);
                    } else {
                        return ResponseEntity.ok(result);
                    }
                });

    }

    /**
     * GET /api/{customerId}/rewards
     * Returns, for customer with customet id on record, reward points earned per
     * month plus the total across the whole period.
     */
    @GetMapping("/{customerId}/rewards")
    public ResponseEntity<CustomerRewardSummary> getRewardsByCustomerId(@PathVariable(value = "customerId") String customerId) {
        return new ResponseEntity<>(rewardService.getRewardSummaryByCustomerId(customerId), HttpStatus.OK);
    }


}
