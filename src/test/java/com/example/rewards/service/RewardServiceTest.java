package com.example.rewards.service;

import com.example.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RewardServiceTest {

    private final RewardService rewardService = new RewardService(new TransactionRepository());

    @Test
    void exampleFromSpec_120DollarPurchase_earns90Points() {
        assertThat(rewardService.calculatePoints(new BigDecimal("120"))).isEqualTo(90);
    }

    @Test
    void purchaseUnder50Dollars_earnsZeroPoints() {
        assertThat(rewardService.calculatePoints(new BigDecimal("49.99"))).isEqualTo(0);
    }

    @Test
    void purchaseExactly50Dollars_earnsZeroPoints() {
        // Boundary is exclusive: points start accruing strictly above $50.
        assertThat(rewardService.calculatePoints(new BigDecimal("50"))).isEqualTo(0);
    }

    @Test
    void purchaseBetween50And100_earnsOnePointPerDollarOverFifty() {
        assertThat(rewardService.calculatePoints(new BigDecimal("75"))).isEqualTo(25);
    }

    @Test
    void purchaseExactly100Dollars_earns50Points() {
        assertThat(rewardService.calculatePoints(new BigDecimal("100"))).isEqualTo(50);
    }

    @Test
    void purchaseOver100Dollars_earnsTwoPointsPerDollarOverHundredPlusFiftyBase() {
        // $310 -> 2x$210 + 1x$50 = 420 + 50 = 470
        assertThat(rewardService.calculatePoints(new BigDecimal("310"))).isEqualTo(470);
    }

    @Test
    void zeroOrNegativeAmount_earnsZeroPoints() {
        assertThat(rewardService.calculatePoints(BigDecimal.ZERO)).isEqualTo(0);
        assertThat(rewardService.calculatePoints(new BigDecimal("-10"))).isEqualTo(0);
    }

    @Test
    void nullAmount_earnsZeroPoints() {
        assertThat(rewardService.calculatePoints(null)).isEqualTo(0);
    }

    @Test
    void summaries_areReturnedForEveryCustomerInRepository() {
        var summaries = rewardService.getRewardSummaries();

        assertThat(summaries).hasSize(4);
        assertThat(summaries).extracting("customerName")
                .containsExactlyInAnyOrder("Alice Nguyen", "Ben Carter", "Priya Sharma", "David Kim");
    }

    @Test
    void customerWithOnlySmallPurchases_hasZeroTotalPoints() {
        var david = rewardService.getRewardSummaries().stream()
                .filter(s -> s.getCustomerName().equals("David Kim"))
                .findFirst()
                .orElseThrow();

        assertThat(david.getTotalPoints()).isZero();
    }

    @Test
    void totalPoints_equalsSumOfMonthlyPoints() {
        for (var summary : rewardService.getRewardSummaries()) {
            int sumOfMonths = summary.getMonthlyRewards().stream()
                    .mapToInt(m -> m.getPoints())
                    .sum();
            assertThat(summary.getTotalPoints()).isEqualTo(sumOfMonths);
        }
    }
}
