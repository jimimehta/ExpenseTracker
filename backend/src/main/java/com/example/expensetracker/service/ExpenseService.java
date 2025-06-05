package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;


    public Expense createExpense(Expense expense) {
        LocalDate cutoffDate = LocalDate.now().minusDays(30);
        if(expense.getDate().isBefore(cutoffDate)) {
            expense.setDeleted(true);
        }
        return expenseRepository.save(expense);
    }

    @Deprecated
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
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));
        expense.setDeleted(true);
        expenseRepository.save(expense);
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

    public Page<Expense> getArchivedExpenses(Pageable pageable) {
        log.info("Archived expenses triggered...");
        autoArchiveOldExpenses();
        List<Expense> archived = expenseRepository.findByDeletedTrue();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), archived.size());
        List<Expense> sublist = archived.subList(start, end);
        return new PageImpl<>(sublist, pageable, archived.size());
    }

    public void autoArchiveOldExpenses() {
        LocalDate cutoffDate = LocalDate.now().minusDays(30);
        log.info("Current date: {}", cutoffDate);
        List<Expense> outdatedExpenses = expenseRepository.findByDeletedFalse().stream()
                .filter(expense -> expense.getDate() != null && expense.getDate().isBefore(cutoffDate))
                .peek(expense -> expense.setDeleted(true))
                .collect(Collectors.toList());

        if (!outdatedExpenses.isEmpty()) {
            expenseRepository.saveAll(outdatedExpenses);
        }
    }
}
