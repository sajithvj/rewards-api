package com.example.rewards.controller;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.service.RewardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")

public class RewardController {

    private final RewardService rewardService;


    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * GET /vi/calculateRewards
     * Returns, for every customer on record, reward points earned per
     * month plus the total across the whole period.
     */
    @GetMapping("/calculateRewards")
    public ResponseEntity<List<CustomerRewardSummary>> getRewards(@RequestParam(required = false, name = "startDate") String startDateStr, @RequestParam(required = false, name = "endDate") String endDateStr) {

        return new ResponseEntity<>(rewardService.getRewardSummaries(startDateStr, endDateStr), HttpStatus.OK);
    }


}
