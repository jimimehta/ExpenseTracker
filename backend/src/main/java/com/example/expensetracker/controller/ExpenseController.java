package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import java.util;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses(Pageable.unpaged()).getContent();
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));
    }

    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {
        return expenseService.createExpense(expense);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
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

    @GetMapping("/category")
    public ResponseEntity<Page<Expense>> getExpensesByCategory(
            @RequestParam Optional<String> category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Expense> expenses = expenseService.getExpensesWithFilters(
                category.orElse(null), null, null, null, null, page, size);

        return ResponseEntity.ok(expenses);
    }
}