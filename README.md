# Inventory Movement Dashboard

A one-page inventory movement dashboard built with React, Vite, Material UI, Recharts, and Spring Boot.

The app loads mock warehouse stock movement data, filters it by date range and movement type, shows the same filtered dataset in charts and a paginated table, and exports the full filtered result as CSV.

## Features

- Required date range filters: `from` and `to`
- Movement type filter: `All`, `IN`, `OUT`
- Paginated stock movement table with 10 rows per page
- Pie chart for total quantity `IN` vs `OUT`
- Daily time-series chart with separate `IN` and `OUT` series
- CSV export for the entire filtered dataset, not only the visible table page
- Backend validation for invalid date ranges and query parameter values

## Project Structure

```text
inventory-dashboard/
  backend/    Spring Boot REST API
  frontend/   React/Vite dashboard
```

## Requirements

- Java 21+
- Maven
- Node.js 18+
- npm

## Run Backend

```bash
cd backend
mvn spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

Main endpoint:

```text
GET /api/movements?from=2026-03-01&to=2026-05-12&type=IN
GET /api/movements?from=2026-03-01&to=2026-05-12&export=true
```

`type` is optional and accepts `IN` or `OUT`. When `export=true` is provided, the endpoint returns CSV.

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on:

```text
http://localhost:5173
```

By default, the frontend calls `http://localhost:8080`. To override the backend URL, create `frontend/.env`:

```text
VITE_API_BASE_URL=http://localhost:8080
```

## Build And Test

Frontend:

```bash
cd frontend
npm run build
```

Backend:

```bash
cd backend
mvn test
```

## Data Source

The backend loads mock movement data from:

```text
backend/src/main/resources/mock_movements.json
```

Each record follows this shape:

```json
{
  "id": "mv1001",
  "timestamp": "2026-03-10T17:46:00Z",
  "sku": "SKU003",
  "movementType": "IN",
  "quantity": 48
}
```

## Trade-offs And Notes

- The optional warehouse dropdown bonus is not implemented.
- The mock data file contains 10,000 records, which is larger than the suggested 300-1,000 range but useful for exercising pagination and filtering.
- The frontend keeps chart aggregation client-side because the filtered dataset is already returned by the backend.
