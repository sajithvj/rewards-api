package com.example.rewards.service;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.exception.CustomerNotFoundException;
import com.example.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RewardServiceTest {

    private final RewardService rewardService = new RewardService(new TransactionRepository());
    CompletableFuture<List<CustomerRewardSummary>> completableFutureTest;

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
        assertThat(rewardService.calculatePoints(new BigDecimal("75"))).isEqualTo(50);
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

//    @Test
//    void summaries_areReturnedForEveryCustomerInRepository() throws ExecutionException, InterruptedException {
//        completableFutureTest = rewardService.getRewardSummaries();
//        var summaries =  completableFutureTest.get();
//
//        assertThat(summaries).hasSize(5);
//        assertThat(summaries).extracting("customerName")
//                .containsExactlyInAnyOrder("Alice Job", "David John","Nirmal Xavier", "Priya Sharma", "Sonu Venu");
//    }
//
//    @Test
//    void customerWithOnlySmallPurchases_hasZeroTotalPoints() throws ExecutionException, InterruptedException {
//        completableFutureTest = rewardService.getRewardSummaries();
//        var summaries =  completableFutureTest.get();
//        var david = summaries.stream()
//                .filter(s -> s.getCustomerName().equals("David John"))
//                .findFirst()
//                .orElseThrow();
//
//        assertThat(david.getTotalPoints()).isZero();
//    }
//
//    @Test
//    void totalPoints_equalsSumOfMonthlyPoints() throws ExecutionException, InterruptedException {
//        completableFutureTest = rewardService.getRewardSummaries();
//        var summaries =  completableFutureTest.get();
//        for (var summary : summaries) {
//            int sumOfMonths = summary.getMonthlyRewards().stream()
//                    .mapToInt(m -> m.getPoints())
//                    .sum();
//            assertThat(summary.getTotalPoints()).isEqualTo(sumOfMonths);
//        }
//    }
//    @Test
//    void summary_areReturnedForSingleCustomerInRepository() {
//
//        var summary=  rewardService.getRewardSummaryByCustomerId("C001",1);
//
//        assertThat(summary.getCustomerId()).isEqualTo("C001");
//        assertThat(summary.getCustomerName()).isEqualTo("Alice Job");
//        assertThat(summary.getTotalPoints()).isNotZero();
//    }
    @Test
    void summary_illegalArgumentExceptionNullCustomerId() {
        assertThrows(IllegalArgumentException.class, () -> rewardService.getRewardSummaryByCustomerId(null,1));
    }

    @Test
    void summary_illegalArgumentExceptionZeroMonth() {
        assertThrows(IllegalArgumentException.class, () -> rewardService.getRewardSummaryByCustomerId("C001",0));
    }

    @Test
    void summary_notFoundForNonExistentCustomerId() {
        assertThrows(CustomerNotFoundException.class, () -> rewardService.getRewardSummaryByCustomerId("C999",2));
    }
}
