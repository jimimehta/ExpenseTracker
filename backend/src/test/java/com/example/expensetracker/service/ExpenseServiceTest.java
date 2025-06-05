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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    void shouldReturnExpense_WhenGetExpenseByIdWithValidId() {
        // Arrange
        Long expenseId = 1L;
        Expense expense = new Expense();
        expense.setId(expenseId);
        expense.setDescription("Test Expense");
        expense.setAmount(50.0);
        expense.setDate(LocalDate.now());
        expense.setCategory("Test");

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act
        Optional<Expense> result = expenseService.getExpenseById(expenseId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expenseId, result.get().getId());
        assertEquals("Test Expense", result.get().getDescription());
        verify(expenseRepository).findById(expenseId);
    }

    @Test
    void shouldReturnEmptyOptional_WhenGetExpenseByIdWithInvalidId() {
        // Arrange
        Long nonExistentId = 999L;
        when(expenseRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Expense> result = expenseService.getExpenseById(nonExistentId);

        // Assert
        assertFalse(result.isPresent());
        verify(expenseRepository).findById(nonExistentId);
    }

    @Test
    void shouldUpdateExpense_WhenUpdateExpenseWithValidId() {
        // Arrange
        Long expenseId = 1L;

        Expense existingExpense = new Expense();
        existingExpense.setId(expenseId);
        existingExpense.setDescription("Original Description");
        existingExpense.setAmount(100.0);
        existingExpense.setDate(LocalDate.now().minusDays(1));
        existingExpense.setCategory("Original Category");

        Expense updatedExpenseData = new Expense();
        updatedExpenseData.setDescription("Updated Description");
        updatedExpenseData.setAmount(200.0);
        updatedExpenseData.setDate(LocalDate.now());
        updatedExpenseData.setCategory("Updated Category");

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(existingExpense);

        // Act
        Expense result = expenseService.updateExpense(expenseId, updatedExpenseData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Description", result.getDescription());
        assertEquals(200.0, result.getAmount());
        assertEquals("Updated Category", result.getCategory());
        verify(expenseRepository).findById(expenseId);
        verify(expenseRepository).save(existingExpense);
    }

    @Test
    void shouldThrowException_WhenUpdateExpenseWithInvalidId() {
        // Arrange
        Long nonExistentId = 999L;
        Expense updatedExpenseData = new Expense();

        when(expenseRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            expenseService.updateExpense(nonExistentId, updatedExpenseData);
        });

        verify(expenseRepository).findById(nonExistentId);
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void shouldSoftDeleteExpense_WhenDeleteExpenseWithValidId() {
        // Arrange
        Long expenseId = 1L;
        Expense expense = new Expense();
        expense.setId(expenseId);
        expense.setDeleted(false);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        // Act
        expenseService.deleteExpense(expenseId);

        // Assert
        assertTrue(expense.isDeleted());
        verify(expenseRepository).findById(expenseId);
        verify(expenseRepository).save(expense);
    }

    @Test
    void shouldThrowException_WhenDeleteExpenseWithInvalidId() {
        // Arrange
        Long nonExistentId = 999L;
        when(expenseRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            expenseService.deleteExpense(nonExistentId);
        });

        verify(expenseRepository).findById(nonExistentId);
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void shouldReturnArchivedExpenses_WhenGetArchivedExpenses() {
        // Arrange
        Expense archivedExpense1 = new Expense();
        archivedExpense1.setId(1L);
        archivedExpense1.setDescription("Archived Expense 1");
        archivedExpense1.setAmount(100.0);
        archivedExpense1.setDate(LocalDate.now().minusDays(40));
        archivedExpense1.setDeleted(true);

        Expense archivedExpense2 = new Expense();
        archivedExpense2.setId(2L);
        archivedExpense2.setDescription("Archived Expense 2");
        archivedExpense2.setAmount(200.0);
        archivedExpense2.setDate(LocalDate.now().minusDays(35));
        archivedExpense2.setDeleted(true);

        List<Expense> archivedExpenses = Arrays.asList(archivedExpense1, archivedExpense2);

        when(expenseRepository.findByDeletedTrue()).thenReturn(archivedExpenses);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Expense> result = expenseService.getArchivedExpenses(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Archived Expense 1", result.getContent().get(0).getDescription());
        assertEquals("Archived Expense 2", result.getContent().get(1).getDescription());
        verify(expenseRepository).findByDeletedTrue();
    }

    @Test
    void shouldArchiveOldExpenses_WhenAutoArchiveOldExpenses() {
        // Arrange
        LocalDate cutoffDate = LocalDate.now().minusDays(30);

        Expense oldExpense1 = new Expense();
        oldExpense1.setId(1L);
        oldExpense1.setDescription("Old Expense 1");
        oldExpense1.setAmount(100.0);
        oldExpense1.setDate(cutoffDate.minusDays(5));
        oldExpense1.setDeleted(false);

        Expense oldExpense2 = new Expense();
        oldExpense2.setId(2L);
        oldExpense2.setDescription("Old Expense 2");
        oldExpense2.setAmount(200.0);
        oldExpense2.setDate(cutoffDate.minusDays(1));
        oldExpense2.setDeleted(false);

        Expense newExpense = new Expense();
        newExpense.setId(3L);
        newExpense.setDescription("New Expense");
        newExpense.setAmount(300.0);
        newExpense.setDate(cutoffDate.plusDays(1));
        newExpense.setDeleted(false);

        List<Expense> activeExpenses = Arrays.asList(oldExpense1, oldExpense2, newExpense);

        when(expenseRepository.findByDeletedFalse()).thenReturn(activeExpenses);
        when(expenseRepository.saveAll(any())).thenReturn(Arrays.asList(oldExpense1, oldExpense2));

        // Act
        expenseService.autoArchiveOldExpenses();

        // Assert
        assertTrue(oldExpense1.isDeleted());
        assertTrue(oldExpense2.isDeleted());
        assertFalse(newExpense.isDeleted());
        verify(expenseRepository).findByDeletedFalse();
        verify(expenseRepository).saveAll(any());
    }

    @Test
    void shouldNotArchiveAnyExpenses_WhenNoOldExpensesExist() {
        // Arrange
        LocalDate cutoffDate = LocalDate.now().minusDays(30);

        Expense newExpense1 = new Expense();
        newExpense1.setId(1L);
        newExpense1.setDescription("New Expense 1");
        newExpense1.setAmount(100.0);
        newExpense1.setDate(cutoffDate.plusDays(1));
        newExpense1.setDeleted(false);

        Expense newExpense2 = new Expense();
        newExpense2.setId(2L);
        newExpense2.setDescription("New Expense 2");
        newExpense2.setAmount(200.0);
        newExpense2.setDate(LocalDate.now());
        newExpense2.setDeleted(false);

        List<Expense> activeExpenses = Arrays.asList(newExpense1, newExpense2);

        when(expenseRepository.findByDeletedFalse()).thenReturn(activeExpenses);

        // Act
        expenseService.autoArchiveOldExpenses();

        // Assert
        assertFalse(newExpense1.isDeleted());
        assertFalse(newExpense2.isDeleted());
        verify(expenseRepository).findByDeletedFalse();
        verify(expenseRepository, never()).saveAll(any());
    }
}
