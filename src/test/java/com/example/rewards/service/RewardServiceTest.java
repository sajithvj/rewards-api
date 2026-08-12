package com.example.rewards.service;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.dto.MonthlyReward;
import com.example.rewards.exception.AppException;
import com.example.rewards.exception.CustomerNotFoundException;
import com.example.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
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
        assertThat(rewardService.calculatePoints(new BigDecimal("75"))).isEqualTo(25);
    }

    @Test
    void purchaseExactly100Dollars_earns50Points() {
        assertThat(rewardService.calculatePoints(new BigDecimal("100"))).isEqualTo(50);
    }

    @Test
    void purchaseLittleGreater100Dollars_earns51Points() {
        assertThat(rewardService.calculatePoints(new BigDecimal("100.5"))).isEqualTo(51);
    }

    @Test
    void purchaseOver100Dollars_earnsTwoPointsPerDollarOverHundredPlusFiftyBase() {

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
    void summaries_areReturnedForEveryCustomerInRepository() throws ExecutionException, InterruptedException {
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();

        completableFutureTest = CompletableFuture.supplyAsync(() -> rewardService.getRewardSummaries(startDate, endDate));

        var summaries = completableFutureTest.get();

        assertThat(summaries).hasSize(5);
        assertThat(summaries).extracting("customerName")
                .containsExactlyInAnyOrder("Alice Job", "David John", "Nirmal Xavier", "Priya Sharma", "Sonu Venu");
    }

    //
    @Test
    void customerWithOnlySmallPurchases_hasZeroTotalPoints() throws ExecutionException, InterruptedException {
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();

        completableFutureTest = CompletableFuture.supplyAsync(() -> rewardService.getRewardSummaries(startDate, endDate));
        var summaries = completableFutureTest.get();
        var david = summaries.stream()
                .filter(s -> s.customerName().equals("David John"))
                .findFirst()
                .orElseThrow();

        assertThat(david.totalPoints()).isZero();
    }

    @Test
    void totalPoints_equalsSumOfMonthlyPoints() throws ExecutionException, InterruptedException {
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        completableFutureTest = CompletableFuture.supplyAsync(() -> rewardService.getRewardSummaries(startDate, endDate));
        var summaries = completableFutureTest.get();
        for (var summary : summaries) {
            int sumOfMonths = summary.monthlyRewards().stream()
                    .mapToInt(MonthlyReward::points)
                    .sum();
            assertThat(summary.totalPoints()).isEqualTo(sumOfMonths);
        }
    }


    @Test
    void summmaries_AppExceptionForInvalidDateRange() {
        LocalDate startDate = LocalDate.now().minusMonths(4);
        LocalDate endDate = LocalDate.now();
        LocalDate invalidStartDate = LocalDate.now().minusYears(1).minusMonths(4);
        LocalDate invalidEndDate = LocalDate.now().minusYears(1).minusMonths(2);
        assertThrows(AppException.class, () -> rewardService.getRewardSummaries(startDate, endDate));
        assertThrows(AppException.class, () -> rewardService.getRewardSummaries(endDate, startDate));
        assertThrows(AppException.class, () -> rewardService.getRewardSummaries(startDate, null));
        assertThrows(AppException.class, () -> rewardService.getRewardSummaries(null, endDate));
        assertThrows(AppException.class, () -> rewardService.getRewardSummaries(null, null));
        assertThrows(AppException.class, () -> rewardService.getRewardSummaries(invalidStartDate, invalidEndDate));
    }

    @Test
    void summmaries_CustomeNotFoundExceptionForInvalidDateRange() {
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now().minusMonths(4);

        assertThrows(CustomerNotFoundException.class, () -> rewardService.getRewardSummaries(startDate, endDate));

    }

}
