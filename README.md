# Budget Management API - Spring Boot
## Final Project  - Transactional Web Applications
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
-   Add description to your methods, you can use your IDE to help with this