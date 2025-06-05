package com.example.expensetracker.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount should be greater than zero")
    private Double amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String category;

    private boolean deleted = false; // Soft delete flag

    // Custom validation check for future date
    @javax.validation.constraints.AssertTrue(message = "Date should not be in the future")
    public boolean isDateValid() {
        return date == null || !date.isAfter(LocalDate.now());
    }
}
