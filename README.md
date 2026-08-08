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
curl http://localhost:8080/api/rewards
```

Returns each customer's points broken down by month, plus a running total:

```json
[
  {
    "customerId": "C001",
    "customerName": "Alice Job",
    "monthlyRewards": [
      {
        "month": "2026-05",
        "points": 50,
        "transactionIds": [
          "T0002"
        ]
      },
      {
        "month": "2026-06",
        "points": 250,
        "transactionIds": [
          "T0003",
          "T0004"
        ]
      },
      {
        "month": "2026-07",
        "points": 50,
        "transactionIds": [
          "T0005"
        ]
      }
    ],
    "totalPoints": 350
  }
]
```
```bash
curl http://localhost:8080/api/C005/rewards
```
Returns a single customer's points broken down by month, plus a running total:

```json
{
  "customerId": "C005",
  "customerName": "Eve Adams",
  "monthlyRewards": [
    {
      "month": "2026-05",
      "points": 0,
      "transactionIds": [
        "T0013"
      ]
    },
    {
      "month": "2026-06",
      "points": 0,
      "transactionIds": [
        "T0014"
      ]
    },
    {
      "month": "2026-07",
      "points": 0,
      "transactionIds": [
        "T0015"
      ]
    }
  ],
  "totalPoints": 0
}
```
```bash
curl http://localhost:8080/api/C001/rewards?months=6
```
Returns a single customer's points for the last 6 months, plus a running total:

```json
{
  "customerId": "C001",
  "customerName": "Alice Job",
  "monthlyRewards": [
    {
      "month": "2026-06",
      "points": 250,
      "transactionIds": [
        "T0003",
        "T0004"
      ]
    },
    {
      "month": "2026-07",
      "points": 50,
      "transactionIds": [
        "T0005"
      ]
    }
  ],
  "totalPoints": 300
}
```
## Test it

```bash
mvn test
```
...
## Health check

```bash
curl http://localhost:8080/actuator/health
```
## Prometheus metrics

```bash
curl http://localhost:8080/actuator/prometheus
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
  data set . Swapping in a JPA repository later
  would only touch that one class.
- Aggregation groups by customer, then by `YearMonth`, using a
  `TreeMap` to keep months in chronological order in the response.
