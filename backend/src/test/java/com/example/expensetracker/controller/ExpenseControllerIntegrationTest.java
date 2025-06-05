package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ExpenseController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExpenseControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExpenseRepository expenseRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/expenses";
    }

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
    }

    /**
     * Tests for getting a page of expenses matching the filters in return
     */
    @Test
    void getExpensesWithFilters_ShouldReturnFilteredExpenses() {
        // Arrange
        Expense expense1 = new Expense();
        expense1.setDescription("Test Expense 1");
        expense1.setAmount(100.0);
        expense1.setDate(LocalDate.now());
        expense1.setCategory("Groceries");
        expense1.setDeleted(false);
        expenseRepository.save(expense1);

        Expense expense2 = new Expense();
        expense2.setDescription("Test Expense 2");
        expense2.setAmount(200.0);
        expense2.setDate(LocalDate.now().minusDays(1));
        expense2.setCategory("Utilities");
        expense2.setDeleted(false);
        expenseRepository.save(expense2);

        Expense expense3 = new Expense();
        expense3.setDescription("Test Expense 3");
        expense3.setAmount(200.0);
        expense3.setDate(LocalDate.now().minusDays(1));
        expense3.setCategory("Groceries");
        expense3.setDeleted(false);
        expenseRepository.save(expense3);

        // Act
        String url = getBaseUrl() + "/filter?category=Groceries";
        ResponseEntity<Map> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity(url, Map.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody.get("expenses"));
        assertEquals(2, ((java.util.List) responseBody.get("expenses")).size());
        assertEquals(0, responseBody.get("currentPage"));
        assertEquals(2, responseBody.get("totalItems"));
        assertEquals(1, responseBody.get("totalPages"));
    }

    @Test
    void getAllExpenses_ShouldReturnAllExpenses() {
        // Arrange
        Expense expense1 = new Expense();
        expense1.setDescription("Test Expense 1");
        expense1.setAmount(100.0);
        expense1.setDate(LocalDate.now());
        expense1.setCategory("Groceries");
        expense1.setDeleted(false);
        expenseRepository.save(expense1);

        Expense expense2 = new Expense();
        expense2.setDescription("Test Expense 2");
        expense2.setAmount(200.0);
        expense2.setDate(LocalDate.now().minusDays(1));
        expense2.setCategory("Utilities");
        expense2.setDeleted(false);
        expenseRepository.save(expense2);

        Expense deletedExpense = new Expense();
        deletedExpense.setDescription("Deleted Expense");
        deletedExpense.setAmount(300.0);
        deletedExpense.setDate(LocalDate.now().minusDays(2));
        deletedExpense.setCategory("Entertainment");
        deletedExpense.setDeleted(true);
        expenseRepository.save(deletedExpense);

        // Act
        ResponseEntity<List> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity(getBaseUrl(), List.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // The getAllExpenses method is deprecated and doesn't filter deleted expenses
        // It returns all expenses, including deleted ones
        assertEquals(3, response.getBody().size());
    }

    @Test
    void getExpenseById_ShouldReturnExpense_WhenIdExists() {
        // Arrange
        Expense expense = new Expense();
        expense.setDescription("Test Expense");
        expense.setAmount(150.0);
        expense.setDate(LocalDate.now());
        expense.setCategory("Test");
        expense.setDeleted(false);
        Expense savedExpense = expenseRepository.save(expense);

        // Act
        ResponseEntity<Expense> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity(getBaseUrl() + "/" + savedExpense.getId(), Expense.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Expense", response.getBody().getDescription());
        assertEquals(150.0, response.getBody().getAmount());
        assertEquals("Test", response.getBody().getCategory());
    }

    @Test
    void createExpense_ShouldCreateAndReturnExpense() {
        // Arrange
        Expense newExpense = new Expense();
        newExpense.setDescription("New Test Expense");
        newExpense.setAmount(175.0);
        newExpense.setDate(LocalDate.now());
        newExpense.setCategory("Test Creation");

        // Act
        ResponseEntity<Expense> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity(getBaseUrl(), newExpense, Expense.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("New Test Expense", response.getBody().getDescription());
        assertEquals(175.0, response.getBody().getAmount());
        assertEquals("Test Creation", response.getBody().getCategory());

        // Verify it was saved to the database
        assertTrue(expenseRepository.findById(response.getBody().getId()).isPresent());
    }

    @Test
    void updateExpense_ShouldUpdateAndReturnExpense() {
        // Arrange
        Expense expense = new Expense();
        expense.setDescription("Original Expense");
        expense.setAmount(200.0);
        expense.setDate(LocalDate.now());
        expense.setCategory("Original Category");
        expense.setDeleted(false);
        Expense savedExpense = expenseRepository.save(expense);

        Expense updatedExpense = new Expense();
        updatedExpense.setDescription("Updated Expense");
        updatedExpense.setAmount(250.0);
        updatedExpense.setDate(LocalDate.now());
        updatedExpense.setCategory("Updated Category");

        // Act
        ResponseEntity<Expense> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange(
                        getBaseUrl() + "/" + savedExpense.getId(),
                        HttpMethod.PUT,
                        new HttpEntity<>(updatedExpense),
                        Expense.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedExpense.getId(), response.getBody().getId());
        assertEquals("Updated Expense", response.getBody().getDescription());
        assertEquals(250.0, response.getBody().getAmount());
        assertEquals("Updated Category", response.getBody().getCategory());

        // Verify it was updated in the database
        Expense fromDb = expenseRepository.findById(savedExpense.getId()).get();
        assertEquals("Updated Expense", fromDb.getDescription());
    }

    @Test
    void deleteExpense_ShouldSoftDeleteExpense() {
        // Arrange
        Expense expense = new Expense();
        expense.setDescription("Expense to Delete");
        expense.setAmount(300.0);
        expense.setDate(LocalDate.now());
        expense.setCategory("To Be Deleted");
        expense.setDeleted(false);
        Expense savedExpense = expenseRepository.save(expense);

        // Act
        restTemplate
                .withBasicAuth("admin", "admin")
                .delete(getBaseUrl() + "/" + savedExpense.getId());

        // Assert
        Expense fromDb = expenseRepository.findById(savedExpense.getId()).get();
        assertTrue(fromDb.isDeleted());
    }

    @Test
    void getArchivedExpenses_ShouldReturnArchivedExpenses() {
        // Arrange
        Expense archivedExpense1 = new Expense();
        archivedExpense1.setDescription("Archived Expense 1");
        archivedExpense1.setAmount(100.0);
        archivedExpense1.setDate(LocalDate.now().minusDays(40));
        archivedExpense1.setDeleted(true);
        expenseRepository.save(archivedExpense1);

        Expense archivedExpense2 = new Expense();
        archivedExpense2.setDescription("Archived Expense 2");
        archivedExpense2.setAmount(200.0);
        archivedExpense2.setDate(LocalDate.now().minusDays(35));
        archivedExpense2.setDeleted(true);
        expenseRepository.save(archivedExpense2);

        Expense activeExpense = new Expense();
        activeExpense.setDescription("Active Expense");
        activeExpense.setAmount(300.0);
        activeExpense.setDate(LocalDate.now());
        activeExpense.setDeleted(false);
        expenseRepository.save(activeExpense);

        // Act
        String url = getBaseUrl() + "/archived";
        ResponseEntity<Map> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity(url, Map.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody.get("expenses"));
        assertEquals(2, ((java.util.List) responseBody.get("expenses")).size());
    }
}
