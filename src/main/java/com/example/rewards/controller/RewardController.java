package com.example.rewards.controller;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.service.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")

public class RewardController {

    private final RewardService rewardService;

    private static final Logger log = LoggerFactory.getLogger(RewardController.class);


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
                        log.error("Failed to process order", ex);
                        throw new RuntimeException("Error occurred while fetching reward summaries", ex);
                    } else {
                        return ResponseEntity.ok(result);
                    }
                });

    }

    /**
     * GET /api/{customerId}/rewards
     * Returns, for customer with customer , reward points earned per
     * month plus the total across the whole period.
     */
    @GetMapping("/{customerId}/rewards")
    public ResponseEntity<CustomerRewardSummary> getRewardsByCustomerId(@PathVariable(value = "customerId") String customerId, @RequestParam(value = "month", defaultValue = "3") Integer months) {
        return new ResponseEntity<>(rewardService.getRewardSummaryByCustomerId(customerId,months), HttpStatus.OK);
    }


}
