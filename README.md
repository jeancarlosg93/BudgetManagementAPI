# Budget Management API - Spring Boot

## Final Project - Transactional Web Applications

### Kevin James Britten - Jean Carlos Guzman

## CORE FEATURES

- User Management:

  - User registration and authentication (Spring Security)
  - Role-based access control (e.g., Admin, User-

- Budget Management:

  - Add, edit, and delete budget categories (e.g., Rent, Food, Entertainment)
  - Record income and expenses under specific categories
  - Retrieve summaries (e.g., total income, total expenses, balance-

- Report Generation:
  - Generate reports showing income vs. expenses for a specified time range
  - Filter results by category or date range

## TECHNICAL REQUIREMENTS

- Database:

  - Use JPA for database interaction
  - Use notations as much as possible and avoid SQL Queries
  - Include relationships between entities (e.g., Users, Budgets, Transactions)

- Security:

  - Secure your endpoints using Spring Boot and Basic Auth
  - Whitelist the endpoints that do not need security
  - Implement role-based authorization.

- Internationalization and Localization:

  - Provide support for at least 2 languages (e.g., French and English)
  - Include dynamic localization for error messages, API responses, and reports

- Code quality:

  - Unit Tests: Implement meaningful tests to make sure the business logic is respected
  - CI: Configure a pipeline and make sure your pipeline is running the Unit Tests
  - Integration Tests: Share the collection you created to test all end points (Compatible with Postman or Bruno)

- API Documentation:
  - Create a Readme file
  - Add all important information (e.g. Objective, Necessary commands, etc)
  - Create a comprehensive layout
  - Use the correct extension
  - Store the Readme file in the good directory
- Add description to your methods, you can use your IDE to help with this

Here's a simplified documentation focused on architecture for students:

# Budget Management System Architecture

## Overview

This is a budget management system built with Spring Boot. It helps users track their income, expenses, and budgets.

## Core Components

### 1. Controllers (REST API)

- Located in `controllers` package
- Handle HTTP requests
- Main controllers:
  - `UserController`: User management
  - `BudgetController`: Budget operations
  - `ExpenseController`: Expense tracking
  - `IncomeController`: Income tracking
  - `ReportController`: Financial reports

### 2. Services

- Located in `services` package
- Business logic
- Main services:
  - `UserService`: User operations
  - `BudgetService`: Budget calculations
  - `ExpenseService`: Expense handling
  - `IncomeService`: Income management
  - `ReportService`: Report generation

### 3. Repositories (Data Access)

- Located in `repositories` package
- Interface with database
- Use Spring Data JPA
- One repository per entity

### 4. Entities (Data Models)

Main entities:

```
User
├── username
├── password
├── role
└── personal info

Budget
├── amount
├── category
└── date range

Expense
├── amount
├── category
└── date

Income
├── amount
├── type
└── date

Report
├── incomes
├── expenses
└── totals
```

### 5. Security

- Basic authentication
- Two roles: USER and ADMIN
- Password encryption
- Protected endpoints

## Database Structure

Simple relationships:

```
User 1:N Income
User 1:N Expense
User 1:N Budget
Expense N:1 Category
Budget N:1 Category
```

## Basic Flow

1. User makes HTTP request
2. Controller receives request
3. Service processes business logic
4. Repository handles data
5. Response returned to user

## Project Structure

```
src/
├── main/
│   ├── controllers/
│   ├── services/
│   ├── repositories/
│   ├── entities/
│   └── security/
└── test/
    └── [test classes]
```

That's it! Each component has its specific job, making the code organized and maintainable.

## API Reference

### Authentication

This API uses Basic Authentication. Include your username and password in the request headers.

### Base URL

http://localhost:8080/api

### Endpoints

#### Budget

1. **GET /budget/user/{userId}**
   Retrieves all budgets for a specific user, with optional filtering by category and date range.

2. **GET /budget/all**
   Retrieves all budgets in the system.

3. **GET /budget/{id}**
   Retrieves a budget by its unique ID.

4. **POST /budget/save**
   Creates a new budget entry.

5. **PUT /budget/{id}**
   Updates an existing budget.

6. **DELETE /budget/{id}**
   Deletes a budget by its ID.

---

#### User

1. **GET /user/all**
   Retrieves all users.

2. **GET /user/{id}**
   Retrieves one user by ID.

3. **POST /user/save**
   Creates a new user.

4. **PUT /user/{id}**
   Updates an existing user.

5. **DELETE /user/{id}**
   Deletes a user by ID.

---

#### Expense

1. **GET /expense/user/{userId}**
   Retrieves all expenses for a specific user, with optional filtering by category and date range.

2. **GET /expense/all**
   Retrieves all expenses in the system.

3. **GET /expense/{id}**
   Retrieves an expense by its unique ID.

4. **POST /expense/save**
   Creates a new expense entry.

5. **PUT /expense/{id}**
   Updates an existing expense.

6. **DELETE /expense/{id}**
   Deletes an expense by its ID.

---

#### Expense Category

1. **GET /expense-category/all**
   Retrieves all expense categories.

2. **GET /expense-category/{id}**
   Retrieves an expense category by its unique ID.

3. **POST /expense-category/save**
   Creates a new expense category.

4. **PUT /expense-category/{id}**
   Updates an existing expense category.

5. **DELETE /expense-category/{id}**
   Deletes an expense category by its ID.

---

#### Income

1. **GET /income/user/{userId}**
   Retrieves all incomes for a specific user, with optional filtering by type and date range.

2. **GET /income/{id}**
   Retrieves an income entry by its unique ID.

3. **POST /income/save**
   Creates a new income entry.

4. **PUT /income/{id}**
   Updates an existing income entry.

5. **DELETE /income/{id}**
   Deletes an income entry by its ID.

---

#### Report

1. **GET /report/all**
   Retrieves all reports in the system.

2. **GET /report/{id}**
   Retrieves a report by its unique ID.

3. **POST /report/create**
   Creates a new report for a user with a specified start and end date.

4. **GET /report/user/{userId}**
   Retrieves all reports for a specific user.

5. **DELETE /report/{id}**
   Deletes a report by its ID.

---
