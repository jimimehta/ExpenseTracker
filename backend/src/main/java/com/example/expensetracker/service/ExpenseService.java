package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Page<Expense> getAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        Optional<Expense> existing = expenseRepository.findById(id);
        if (existing.isPresent()) {
            Expense expense = existing.get();
            expense.setDescription(updatedExpense.getDescription());
            expense.setAmount(updatedExpense.getAmount());
            expense.setDate(updatedExpense.getDate());
            expense.setCategory(updatedExpense.getCategory());
            return expenseRepository.save(expense);
        } else {
            throw new RuntimeException("Expense not found with ID: " + id);
        }
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public Page<Expense> getExpensesWithFilters(
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Double minAmount,
            Double maxAmount,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);
        return expenseRepository.findWithFilters(
                category,
                startDate,
                endDate,
                minAmount,
                maxAmount,
                pageable);
    }
}