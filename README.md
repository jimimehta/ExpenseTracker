# Expense Tracker

A simple full-stack application to manage expenses, built with Spring Boot and Vue.js.

## Prerequisites
- Java 11 or higher
- Maven
- Node.js and npm

## Project Structure
- `backend/`: Spring Boot REST API
- `frontend/`: Vue.js frontend

## Test Overview
You will work on a pre-existing Expense Tracker application, which includes a backend (Java, Spring Boot, JPA) and a frontend (Vue.js). The application currently supports basic expense management, and your task is to complete a series of exercises to improve its functionality.
   -  Suggested Time to Complete: 2 hours
   - Presentation: 20-25 minutes (via screen-sharing)

## Instructions
1. Project Setup:
   - Download the project from the provided [GitHub link or ZIP file].
   - Backend:
      - Navigate to the backend/ directory.
      - Run mvn spring-boot:run to start the server (runs on http://localhost:8080).
   - Frontend:
      - Navigate to the frontend/ directory.
      - Run npm install to install dependencies.
      - Run npm run serve to start the app (typically on http://localhost:8084).
   - Verify the application is running by accessing it in your browser.
   - You can login using the credentials admin and admin.
   - Database can be accessed by going to this link http://localhost:8080/h2-console/login.jsp
   - Information required to login in the database above can be accessed from application.properties file

2. Exercises:
   Complete all four of the following tasks within the 2-hour time limit:

   - Exercise 1: Implement Advanced Filtering and Pagination
      - Backend: Add a new endpoint GET /api/expenses with support for filtering by category, date range, and amount (greater than or less than). Include pagination (page, size) and return the total number of pages and expenses.
      - Frontend: Add UI controls for filtering (e.g., dropdowns, input fields) and implement pagination buttons with page information display.

   - Exercise 2: Add Validation and Error Handling
      - Backend: Ensure the amount field is greater than zero and the date is not in the future for POST /api/expenses and PUT /api/expenses/{id} endpoints. Return a 400 Bad Request with a JSON error message if invalid. Use @ControllerAdvice for consistent error handling.
      - Frontend: Display specific error messages to the user when submission fails due to validation errors.

   - Exercise 3: Implement Soft Delete and Archiving
      - Backend: Modify the DELETE /api/expenses/{id} endpoint to implement a soft delete by setting a deleted flag. Add a new endpoint GET /api/expenses/archived to retrieve soft-deleted expenses. Implement logic to auto-archive expenses older than 30 days (mock the date for testing).
      - Frontend: Replace the "Delete" button with an "Archive" button and add a section to view archived expenses.

   - Exercise 4: Write Comprehensive Tests
      - Backend: Write unit tests for the ExpenseService class using Mockito, covering scenarios like invalid inputs. Add an integration test for the ExpenseController using @SpringBootTest.
      - Frontend: Write a test for the ExpenseForm.vue component using Vue Test Utils to verify that it emits the correct event when an expense is added.

3. Submission:
   - Submit your updated project code and a 3-4 slide PowerPoint presentation to [your email] by [deadline date/time, e.g., October 20, 2023, 5:00 PM EST].
   - The PowerPoint should include:
      - A brief overview of your solutions and design decisions.
      - Any challenges you faced and how you addressed them.
      - One suggestion for improving the project's scalability or performance (e.g., implementing caching) and its benefits.

4. Presentation:
   - During a 20-25 minute screen-sharing session, you will:
      - Walk through your code changes and explain your approach.
      - Discuss your testing strategy and improvement suggestion.
      - Answer questions about your decisions and any trade-offs.

***

# 💸 Expense Tracker Application

A full-stack web application for tracking personal expenses with advanced filtering, validation, and archiving capabilities.

## 🛠️ Tech Stack

### 🔧 Backend

- Java 11
- Spring Boot 2.7
- Spring Data JPA
- Spring Security
- H2 In-Memory Database
- JUnit & Mockito for unit testing

### 🖥️ Frontend

- Vue.js 2.6
- Vue Router
- Axios (HTTP client)
- Jest + Vue Test Utils for unit testing

## ✅ Implemented Features

### 🔹 Assigned Features

#### 🧩 Exercise 1: Advanced Filtering and Pagination

__Backend__

- ✅ GET /api/expenses/filter endpoint created

- ✅ Filtering by:

  - Category
  - Date range (startDate, endDate)
  - Amount range (minAmount, maxAmount)

- ✅ Pagination support with page and size params

- ✅ Response includes:

  - Paginated data
  - Total pages
  - Total items

__Frontend__

- ✅ UI controls for category, date, and amount filters

- ✅ Pagination with:

  - Next / Previous buttons
  - Current page indicator
  - Items per page selector

#### 🧩 Exercise 2: Validation and Error Handling

__Backend__

- ✅ Validations:

  - Amount must be greater than 0
  - Date must not be in the future

- ✅ 400 Bad Request with structured JSON errors

- ✅ Global error handling via @ControllerAdvice

__Frontend__

- ✅ Displays validation messages inline
- ✅ Highlights invalid fields
- ✅ General error handling on form submission

#### 🧩 Exercise 3: Soft Delete and Archiving

__Backend__

- ✅ DELETE /api/expenses/{id} performs soft delete using deleted flag
- ✅ New endpoint: GET /api/expenses/archived
- ✅ Auto-archive logic for expenses older than 30 days

__Frontend__

- ✅ "Delete" button changed to "Archive"
- ✅ Tabbed UI to toggle Active / Archived
- ✅ Separate pagination for archived data

#### 🧩 Exercise 4: Comprehensive Tests

__Backend__

- ✅ Unit tested ExpenseService with Mockito
- ✅ Integration tested ExpenseController with @SpringBootTest
- ✅ Validated error scenarios for invalid inputs

__Frontend__

- ✅ Unit test for ExpenseForm.vue
- ✅ Checked event emission on successful submission
- ✅ Mocked API failures for error case testing

### ✨ Additional Implemented Features

#### 📝 Expense Editing Support

- Unified form for Create & Edit
- PUT API integration
- Visual edit mode indicators

#### 💲 Decimal Amount Support

- Floating-point precision for financial inputs

#### ✅ Expanded Testing Coverage

- Tests for edge cases, archive logic, validation errors
- JaCoCo code coverage enabled with HTML report at target/site/jacoco/index.html

#### 🎨 Enhanced UI/UX

- Responsive and modern UI
- Modal forms with animations
- Category badges, styled buttons, and error highlights

#### 📊 Data Management Improvements

- Default sort: Latest expenses first
- Dual-layer validation (client + server)

#### ⚡ Performance Optimizations

- Server-side filtering using optimized JPQL
- Efficient pagination logic

#### 🧰 Development Tooling

- Babel and Jest fixes for test compatibility
- .vscode/settings.json for consistent formatting
- Improved dev script reliability

## 🧑‍💻 Development Environment

### 🚀 Running the Application

__Backend__

```bash
cd backend
mvn spring-boot:run
```

__Frontend__

```bash
cd frontend
npm install
npm run serve
```

### 🧪 Testing

__Backend Tests__

```bash
cd backend
mvn test
```

✅ Unit and integration tests with JUnit and Mockito

__Frontend Tests__

```bash
cd frontend
npm run test
```

✅ Component testing using Jest\
✅ DOM and event handling tested with Vue Test Utils

### 🔐 Default Login Credentials

```javascript
Username: admin
Password: admin
```
