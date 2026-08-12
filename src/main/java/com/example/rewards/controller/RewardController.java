package com.example.rewards.controller;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.service.RewardService;
import com.example.rewards.util.DateParameterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
    public ResponseEntity<List<CustomerRewardSummary>> getRewards(@RequestParam(required = false,name = "startDate") String startDateStr,
                                                                  @RequestParam(required = false,name = "endDate") String endDateStr) {
        LocalDate startDate;
        LocalDate endDate;
        startDate= DateParameterParser.parse("startDate", startDateStr);
        endDate=DateParameterParser.parse("endDate", endDateStr);
        if (startDate == null && endDate == null) {

            startDate = LocalDate.now().minusMonths(3);
            endDate = LocalDate.now();
        }

        return new ResponseEntity<>(rewardService.getRewardSummaries(startDate, endDate), HttpStatus.OK);
    }




}
