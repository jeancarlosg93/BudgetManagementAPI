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