# Rewards API

Calculates customer reward points from retail transactions.

## Rules

- 2 points for every dollar spent **over $100** in a transaction
- 1 point for every dollar spent **between $50 and $100** in a transaction
- Example: a $120 purchase = 2×$20 + 1×$50 = **90 points**
  - a $50 purchase = 0 points
  - a $75 purchase = 25 points
  - a $200 purchase = 2×$100 + 1×$50 = 250 points
  - a $51 purchase = 1 point
  - a $100 purchase = 50 points

## Run it

```bash
mvn spring-boot:run
```

Then:

```bash
curl localhost:8080/v1/calculateRewards?startDate=2026-05-09&endDate=2026-08-07
```

Returns each customer's points broken down by month, plus a running total: for the date range May 9, 2026 to August 7, 2026:

```json
[
  {
    "customerId": "C001",
    "customerName": "Alice Job",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 365,
        "transactionIds": [
          {
            "transactionId": "T0001",
            "amount": 120.00
          },
          {
            "transactionId": "T0002",
            "amount": 75.50
          },
          {
            "transactionId": "T0003",
            "amount": 45.00
          },
          {
            "transactionId": "T0004",
            "amount": 200.00
          }
        ]
      },
      {
        "year": 2026,
        "month": "JULY",
        "points": 49,
        "transactionIds": [
          {
            "transactionId": "T0005",
            "amount": 99.99
          }
        ]
      }
    ],
    "totalPoints": 414
  },
  {
    "customerId": "C004",
    "customerName": "David John",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 0,
        "transactionIds": [
          {
            "transactionId": "T00011",
            "amount": 30.00
          },
          {
            "transactionId": "T00012",
            "amount": 49.99
          }
        ]
      }
    ],
    "totalPoints": 0
  },
  {
    "customerId": "C005",
    "customerName": "Nirmal Xavier",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 90,
        "transactionIds": [
          {
            "transactionId": "T00013",
            "amount": 120.00
          }
        ]
      },
      {
        "year": 2026,
        "month": "JULY",
        "points": 10,
        "transactionIds": [
          {
            "transactionId": "T00014",
            "amount": 60.00
          }
        ]
      }
    ],
    "totalPoints": 100
  },
  {
    "customerId": "C003",
    "customerName": "Priya Sharma",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 840,
        "transactionIds": [
          {
            "transactionId": "T0008",
            "amount": 310.00
          },
          {
            "transactionId": "T0009",
            "amount": 260.40
          }
        ]
      },
      {
        "year": 2026,
        "month": "JULY",
        "points": 210,
        "transactionIds": [
          {
            "transactionId": "T00010",
            "amount": 180.00
          }
        ]
      }
    ],
    "totalPoints": 1050
  },
  {
    "customerId": "C002",
    "customerName": "Sonu Venu",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 0,
        "transactionIds": [
          {
            "transactionId": "T0006",
            "amount": 50.00
          }
        ]
      },
      {
        "year": 2026,
        "month": "JULY",
        "points": 150,
        "transactionIds": [
          {
            "transactionId": "T0007",
            "amount": 150.75
          }
        ]
      }
    ],
    "totalPoints": 150
  }
]
```
```bash
curl localhost:8080/v1/calculateRewards
```
Returns each customer's points broken down by month, plus a running total: for the default date range of the last 3 months:

```json
[
  {
    "customerId": "C001",
    "customerName": "Alice Job",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 365,
        "transactionIds": [
          {
            "transactionId": "T0001",
            "amount": 120.00
          },
          {
            "transactionId": "T0002",
            "amount": 75.50
          },
          {
            "transactionId": "T0003",
            "amount": 45.00
          },
          {
            "transactionId": "T0004",
            "amount": 200.00
          }
        ]
      },
      {
        "year": 2026,
        "month": "JULY",
        "points": 49,
        "transactionIds": [
          {
            "transactionId": "T0005",
            "amount": 99.99
          }
        ]
      }
    ],
    "totalPoints": 414
  },
  {
    "customerId": "C004",
    "customerName": "David John",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 0,
        "transactionIds": [
          {
            "transactionId": "T00011",
            "amount": 30.00
          },
          {
            "transactionId": "T00012",
            "amount": 49.99
          }
        ]
      }
    ],
    "totalPoints": 0
  },
  {
    "customerId": "C005",
    "customerName": "Nirmal Xavier",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 90,
        "transactionIds": [
          {
            "transactionId": "T00013",
            "amount": 120.00
          }
        ]
      },
      {
        "year": 2026,
        "month": "JULY",
        "points": 10,
        "transactionIds": [
          {
            "transactionId": "T00014",
            "amount": 60.00
          }
        ]
      },
      {
        "year": 2026,
        "month": "AUGUST",
        "points": 2,
        "transactionIds": [
          {
            "transactionId": "T00015",
            "amount": 52.00
          }
        ]
      }
    ],
    "totalPoints": 102
  },
  {
    "customerId": "C003",
    "customerName": "Priya Sharma",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 840,
        "transactionIds": [
          {
            "transactionId": "T0008",
            "amount": 310.00
          },
          {
            "transactionId": "T0009",
            "amount": 260.40
          }
        ]
      },
      {
        "year": 2026,
        "month": "JULY",
        "points": 210,
        "transactionIds": [
          {
            "transactionId": "T00010",
            "amount": 180.00
          }
        ]
      }
    ],
    "totalPoints": 1050
  },
  {
    "customerId": "C002",
    "customerName": "Sonu Venu",
    "monthlyRewards": [
      {
        "year": 2026,
        "month": "JUNE",
        "points": 0,
        "transactionIds": [
          {
            "transactionId": "T0006",
            "amount": 50.00
          }
        ]
      },
      {
        "year": 2026,
        "month": "JULY",
        "points": 150,
        "transactionIds": [
          {
            "transactionId": "T0007",
            "amount": 150.75
          }
        ]
      }
    ],
    "totalPoints": 150
  }
]
```
```bash
curl localhost:8080/v1/calculateRewards?startDate=2026-06-09
```
Returns an error message


```json
{
  "details": "Both start date and end date must be provided together or both must be null.",
  "statusCode": 400,
  "path": "/v1/calculateRewards",
  "timestamp": "2026-08-13T22:16:30.2366235"
}
```
If the startDate greater than 3 months will return error message
``` json
{
    "details": "Date range cannot exceed three months.",
    "statusCode": 400,
    "path": "/v1/calculateRewards",
    "timestamp": "2026-08-13T22:09:27.2026099"
}
```
```bash
curl localhost:8080/v1/calculateRewards?endDate=2026-08-07
````
Returns error message because startDate is not provided
```json
{
  "details": "Both start date and end date must be provided together or both must be null.",
  "statusCode": 400,
  "path": "/v1/calculateRewards",
  "timestamp": "2026-08-13T22:16:30.2366235"
}
```
```bash
curl localhost:8080/v1/calculateRewards?startDate=2026-08-09&endDate=2026-05-07
```
Returns an error because the start date is after the end date:

```json
{
  "details": "Start date must be before or equal to end date.",
  "statusCode": 400,
  "path": "/v1/calculateRewards",
  "timestamp": "2026-08-12T22:38:59.1651701"
}
```

## Test it

```bash
mvn test
```
``screnshots
![img_1.jpg](doc/img_1.jpg)

![img_5.jpg](doc/img_5.jpg)

![img_6.jpg](doc/img_6.jpg)

![img_2.jpg](doc/img_2.jpg)

![img_3.jpg](doc/img_3.jpg)

![img_4.jpg](doc/img_4.jpg)

![img_7.jpg](doc/img_7.jpg)

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
  avoid floating-point rounding errors on currency and truncated decimal points for accuracy.
- Transactions are seeded in-memory (`TransactionRepository`) rather than
  backed by a real database, since the assignment calls for a made-up
  data set . Swapping in a JPA repository later
  would only touch that one class.
- Aggregation groups by customer, then by `YearMonth`, using a
  `TreeMap` to keep months in chronological order in the response.
