package com.parneel.FinTrack.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TransactionRequest {
    @Min(value = 1, message = "Minimum amount should be 1")
    private double amount;

    private TransactionType type;

    @NotNull
    private LocalDate date;

    public @Min(value = 1, message = "Minimum amount should be 1") double getAmount() {
        return amount;
    }

    public TransactionRequest(double amount, TransactionType type, LocalDate date, String category, String description) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    public void setAmount(@Min(value = 1, message = "Minimum amount should be 1") double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public @NotNull LocalDate getDate() {
        return date;
    }

    public void setDate(@NotNull LocalDate date) {
        this.date = date;
    }

    public @NotBlank String getCategory() {
        return category;
    }

    public void setCategory(@NotBlank String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotBlank
    private String category;
    private String description;
}
