package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @Deprecated
    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses(Pageable.unpaged()).getContent();
    }

    @GetMapping("/archived")
    public ResponseEntity<Map<String, Object>> getArchivedExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Expense> archivedPage = expenseService.getArchivedExpenses(PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("expenses", archivedPage.getContent());
        response.put("currentPage", archivedPage.getNumber());
        response.put("totalItems", archivedPage.getTotalElements());
        response.put("totalPages", archivedPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));
    }

    @PostMapping
    public Expense createExpense(@Valid @RequestBody Expense expense) {
        return expenseService.createExpense(expense);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        return expenseService.updateExpense(id, expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getExpensesWithFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Expense> expensePage = expenseService.getExpensesWithFilters(
                category, startDate, endDate, minAmount, maxAmount, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("expenses", expensePage.getContent());
        response.put("currentPage", expensePage.getNumber());
        response.put("totalItems", expensePage.getTotalElements());
        response.put("totalPages", expensePage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
