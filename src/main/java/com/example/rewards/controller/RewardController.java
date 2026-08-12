package com.example.rewards.controller;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.service.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1")

public class RewardController {

    private final RewardService rewardService;

    private static final Logger log = LoggerFactory.getLogger(RewardController.class);


    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * GET /vi/calculateRewards
     * Returns, for every customer on record, reward points earned per
     * month plus the total across the whole period.
     */
    @GetMapping("/calculateRewards")
    public ResponseEntity<List<CustomerRewardSummary>> getRewards(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)  {
        return new ResponseEntity<>(rewardService.getRewardSummaries(startDate,endDate), HttpStatus.OK);
    }

    /**
     * GET /api/{customerId}/rewards
     * Returns, for customer with customer , reward points earned per
     * month plus the total across the whole period.
     */
    @GetMapping("/{customerId}/rewards")
    public ResponseEntity<CustomerRewardSummary> getRewardsByCustomerId(@PathVariable(value = "customerId") String customerId, @RequestParam(value = "months", defaultValue = "3") Integer months) {
        return new ResponseEntity<>(rewardService.getRewardSummaryByCustomerId(customerId,months), HttpStatus.OK);
    }


}
