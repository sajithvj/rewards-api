# Rewards API

Calculates customer reward points from retail transactions.

## Rules

- 2 points for every dollar spent **over $100** in a transaction
- 1 point for every dollar spent **between $50 and $100** in a transaction
- Example: a $120 purchase = 2×$20 + 1×$50 = **90 points**

## Run it

```bash
mvn spring-boot:run
```

Then:

```bash
curl http://localhost:8085/api/rewards
```

Returns each customer's points broken down by month, plus a running total:

```json
[
  {
    "customerId": "C001",
    "customerName": "Alice Nguyen",
    "monthlyRewards": [
      { "month": "2026-04", "points": 115 },
      { "month": "2026-05", "points": 250 },
      { "month": "2026-06", "points": 49 }
    ],
    "totalPoints": 414
  }
]
```

## Test it

```bash
mvn test
```

Covers the points formula at each tier boundary ($50, $100), the worked
example from the spec ($120 → 90 pts), a customer who never crosses $50
(zero points), and a full-stack MockMvc test on the endpoint.

## Design notes

- **`RewardService.calculatePoints`** is a small pure function — easy to
  unit test in isolation from HTTP/aggregation concerns.
- **`BigDecimal`** is used throughout for money instead of `double`, to
  avoid floating-point rounding errors on currency.
- Transactions are seeded in-memory (`TransactionRepository`) rather than
  backed by a real database, since the assignment calls for a made-up
  data set and a ~30 minute scope. Swapping in a JPA repository later
  would only touch that one class.
- Aggregation groups by customer, then by `YearMonth`, using a
  `TreeMap` to keep months in chronological order in the response.
