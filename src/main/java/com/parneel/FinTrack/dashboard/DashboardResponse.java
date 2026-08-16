package com.parneel.FinTrack.dashboard;

import java.time.YearMonth;
import java.util.Map;

public class DashboardResponse {

    private final double totalIncome;
    private final double totalExpense;
    private final double balance;
    private final double highestExpense;
    private final double averageSpending;
    private final String  topCategory;
    private final Map<YearMonth, Double> monthlySpending;
    private final Map<String, Double> categoryBreakdown;

    public String getTopCategory() {
        return topCategory;
    }

    public Map<String, Double>  getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public Map<YearMonth, Double> getMonthlySpending() {
        return monthlySpending;
    }

    public DashboardResponse(
            double totalIncome,
            double totalExpense,
            double balance,
            double highestExpense,
            double averageSpending,
            String topCategory,
            Map<YearMonth, Double> monthlySpending,
            Map<String, Double> categoryBreakdown) {

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.highestExpense = highestExpense;
        this.averageSpending = averageSpending;
        this.topCategory = topCategory;
        this.categoryBreakdown = categoryBreakdown;
        this.monthlySpending = monthlySpending;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getBalance() {
        return balance;
    }

    public double getHighestExpense() {
        return highestExpense;
    }

    public double getAverageSpending() {
        return averageSpending;
    }
}