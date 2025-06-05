package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExpenseService to test on invalid inputs
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(expenseRepository);
    }

    @Test
    void shouldReturnSavedExpense_WhenInputIsValid() {
        // Arrange
        Expense validExpense = new Expense();
        validExpense.setDescription("Ordered Lunch");
        validExpense.setAmount(20.0);
        validExpense.setDate(LocalDate.now());
        validExpense.setCategory("Food");

        when(expenseRepository.save(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createExpense(validExpense);

        // Assert
        assertNotNull(result);
        assertEquals("Ordered Lunch", result.getDescription());
        verify(expenseRepository).save(validExpense);
    }

    @Test
    void shouldSaveExpense_WhenDateIsTodayy() {
        // Arrange
        Expense todayExpense = new Expense();
        todayExpense.setDescription("Netflix");
        todayExpense.setAmount(45.0);
        todayExpense.setDate(LocalDate.now()); // today's date
        todayExpense.setCategory("Bills");

        when(expenseRepository.save(any(Expense.class))).thenReturn(todayExpense);

        // Act
        Expense result = expenseService.createExpense(todayExpense);

        // Assert
        assertNotNull(result);
        assertEquals("Netflix", result.getDescription());
        assertEquals(LocalDate.now(), result.getDate());
        verify(expenseRepository).save(todayExpense);
    }

    @Test
    void testThrowsExceptionOnNullAmount() {
        // Arrange
        Expense invalidExpense = new Expense();
        invalidExpense.setDescription("GO Bus Ticket");
        invalidExpense.setAmount(null); // Invalid input
        invalidExpense.setDate(LocalDate.now());
        invalidExpense.setCategory("Travel");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.createExpense(invalidExpense);
        });

        verify(expenseRepository, never()).save(any(Expense.class));
    }
}