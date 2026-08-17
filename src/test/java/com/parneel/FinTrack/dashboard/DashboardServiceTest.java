package com.parneel.FinTrack.dashboard;

import com.parneel.FinTrack.transaction.Transaction;
import com.parneel.FinTrack.transaction.TransactionRepository;
import com.parneel.FinTrack.transaction.TransactionType;
import com.parneel.FinTrack.user.User;
import com.parneel.FinTrack.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;


    @Test
    void getDashboard_shouldCalculateIncomeExpenseAndBalance() {

        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction income = new Transaction();
        income.setAmount(5000);
        income.setType(TransactionType.INCOME);
        income.setDate(LocalDate.of(2026, 8, 1));
        income.setCategory("Salary");

        Transaction expense = new Transaction();
        expense.setAmount(1500);
        expense.setType(TransactionType.EXPENSE);
        expense.setDate(LocalDate.of(2026, 8, 2));
        expense.setCategory("Food");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(income, expense));

        DashboardResponse result =
                dashboardService.getDashboard(username);

        assertEquals(5000, result.getTotalIncome());
        assertEquals(1500, result.getTotalExpense());
        assertEquals(3500, result.getBalance());

        verify(transactionRepository).findByUser(user);
    }


    @Test
    void getDashboard_shouldCalculateHighestAndAverageExpense() {

        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction expense1 = new Transaction();
        expense1.setAmount(1000);
        expense1.setType(TransactionType.EXPENSE);
        expense1.setDate(LocalDate.of(2026, 8, 1));
        expense1.setCategory("Food");

        Transaction expense2 = new Transaction();
        expense2.setAmount(3000);
        expense2.setType(TransactionType.EXPENSE);
        expense2.setDate(LocalDate.of(2026, 8, 2));
        expense2.setCategory("Shopping");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(expense1, expense2));

        DashboardResponse result =
                dashboardService.getDashboard(username);

        assertEquals(3000, result.getHighestExpense());
        assertEquals(2000, result.getAverageSpending());
    }


    @Test
    void getDashboard_shouldCalculateTopCategoryAndBreakdown() {

        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction food1 = new Transaction();
        food1.setAmount(1000);
        food1.setType(TransactionType.EXPENSE);
        food1.setDate(LocalDate.of(2026, 8, 1));
        food1.setCategory("Food");

        Transaction food2 = new Transaction();
        food2.setAmount(2000);
        food2.setType(TransactionType.EXPENSE);
        food2.setDate(LocalDate.of(2026, 8, 2));
        food2.setCategory("Food");

        Transaction shopping = new Transaction();
        shopping.setAmount(1500);
        shopping.setType(TransactionType.EXPENSE);
        shopping.setDate(LocalDate.of(2026, 8, 3));
        shopping.setCategory("Shopping");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(food1, food2, shopping));

        DashboardResponse result =
                dashboardService.getDashboard(username);

        assertEquals("Food", result.getTopCategory());

        assertEquals(
                3000,
                result.getCategoryBreakdown().get("Food")
        );

        assertEquals(
                1500,
                result.getCategoryBreakdown().get("Shopping")
        );
    }


    @Test
    void getDashboard_shouldCalculateMonthlySpending() {

        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction januaryExpense = new Transaction();
        januaryExpense.setAmount(1000);
        januaryExpense.setType(TransactionType.EXPENSE);
        januaryExpense.setDate(LocalDate.of(2026, 1, 10));
        januaryExpense.setCategory("Food");

        Transaction januaryExpense2 = new Transaction();
        januaryExpense2.setAmount(500);
        januaryExpense2.setType(TransactionType.EXPENSE);
        januaryExpense2.setDate(LocalDate.of(2026, 1, 20));
        januaryExpense2.setCategory("Shopping");

        Transaction februaryExpense = new Transaction();
        februaryExpense.setAmount(2000);
        februaryExpense.setType(TransactionType.EXPENSE);
        februaryExpense.setDate(LocalDate.of(2026, 2, 10));
        februaryExpense.setCategory("Rent");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                januaryExpense,
                                januaryExpense2,
                                februaryExpense
                        )
                );

        DashboardResponse result =
                dashboardService.getDashboard(username);

        assertEquals(
                1500,
                result.getMonthlySpending()
                        .get(YearMonth.of(2026, 1))
        );

        assertEquals(
                2000,
                result.getMonthlySpending()
                        .get(YearMonth.of(2026, 2))
        );
    }


    @Test
    void getDashboard_shouldReturnZerosWhenNoTransactions() {

        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of());

        DashboardResponse result =
                dashboardService.getDashboard(username);

        assertEquals(0, result.getTotalIncome());
        assertEquals(0, result.getTotalExpense());
        assertEquals(0, result.getBalance());
        assertEquals(0, result.getHighestExpense());
        assertEquals(0, result.getAverageSpending());
        assertEquals("No expenses", result.getTopCategory());

        assertTrue(result.getMonthlySpending().isEmpty());
        assertTrue(result.getCategoryBreakdown().isEmpty());
    }


    @Test
    void getDashboard_shouldThrowException_whenUserNotFound() {

        String username = "unknown";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> dashboardService.getDashboard(username)
        );

        assertEquals("User not found", exception.getMessage());

        verify(transactionRepository, never())
                .findByUser(any(User.class));
    }
}