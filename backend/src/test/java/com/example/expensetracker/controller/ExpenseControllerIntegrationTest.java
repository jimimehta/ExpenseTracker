package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;

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
}
