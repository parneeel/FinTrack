package com.parneel.FinTrack.transaction;

import com.parneel.FinTrack.user.User;
import com.parneel.FinTrack.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void addTransaction_shouldSaveTransactionSuccessfully() {

        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction transaction = new Transaction();
        transaction.setAmount(500);
        transaction.setType(TransactionType.EXPENSE);
        transaction.setDate(LocalDate.now());
        transaction.setCategory("Food");
        transaction.setDescription("Lunch");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repository.save(transaction))
                .thenReturn(transaction);

        Transaction result =
                transactionService.addTransaction(transaction, username);

        assertEquals(transaction, result);
        assertEquals(user, transaction.getUser());

        verify(userRepository).findByUsername(username);
        verify(repository).save(transaction);
    }


    @Test
    void addTransaction_shouldThrowException_whenUserNotFound() {

        String username = "unknown";

        Transaction transaction = new Transaction();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionService.addTransaction(transaction, username)
        );

        assertEquals("User not found", exception.getMessage());

        verify(repository, never()).save(any(Transaction.class));
    }


    @Test
    void getAllTransactions_shouldReturnUserTransactions() {

        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction transaction = new Transaction();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repository.findByUser(user))
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.getAllTransactions(username);

        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));

        verify(repository).findByUser(user);
    }


    @Test
    void getTransactionsByCategory_shouldReturnMatchingTransactions() {

        String username = "parneel";
        String category = "Food";

        User user = new User();
        user.setUsername(username);

        Transaction transaction = new Transaction();
        transaction.setCategory(category);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repository.findByUserAndCategory(user, category))
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.getTransactionsByCategory(
                        category,
                        username
                );

        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getCategory());

        verify(repository)
                .findByUserAndCategory(user, category);
    }


    @Test
    void getTransactionById_shouldReturnTransaction() {

        int id = 1;
        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction transaction = new Transaction();
        transaction.setId(id);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repository.findByIdAndUser(id, user))
                .thenReturn(Optional.of(transaction));

        Transaction result =
                transactionService.getTransactionById(id, username);

        assertEquals(transaction, result);

        verify(repository).findByIdAndUser(id, user);
    }


    @Test
    void getTransactionById_shouldThrowException_whenTransactionNotFound() {

        int id = 99;
        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repository.findByIdAndUser(id, user))
                .thenReturn(Optional.empty());

        TransactionNotFoundException exception = assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.getTransactionById(id, username)
        );

        assertEquals(
                "Transaction with id " + id + " not found",
                exception.getMessage()
        );

        verify(repository).findByIdAndUser(id, user);
    }


    @Test
    void updateTransaction_shouldUpdateAndSaveTransaction() {

        int id = 1;
        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(id);
        existingTransaction.setAmount(500);
        existingTransaction.setCategory("Food");

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(1000);
        updatedTransaction.setCategory("Shopping");
        updatedTransaction.setDescription("New description");
        updatedTransaction.setDate(LocalDate.now());
        updatedTransaction.setType(TransactionType.EXPENSE);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repository.findByIdAndUser(id, user))
                .thenReturn(Optional.of(existingTransaction));

        when(repository.save(existingTransaction))
                .thenReturn(existingTransaction);

        Transaction result =
                transactionService.updateTransaction(
                        id,
                        updatedTransaction,
                        username
                );

        assertEquals(1000, result.getAmount());
        assertEquals("Shopping", result.getCategory());
        assertEquals("New description", result.getDescription());
        assertEquals(TransactionType.EXPENSE, result.getType());

        verify(repository).save(existingTransaction);
    }


    @Test
    void deleteTransaction_shouldDeleteTransaction() {

        int id = 1;
        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction transaction = new Transaction();
        transaction.setId(id);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repository.findByIdAndUser(id, user))
                .thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(id, username);

        verify(repository).delete(transaction);
    }


    @Test
    void getAllTransactionsPaginated_shouldReturnPage() {

        String username = "parneel";

        User user = new User();
        user.setUsername(username);

        Transaction transaction = new Transaction();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> page =
                new PageImpl<>(List.of(transaction));

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repository.findByUser(user, pageable))
                .thenReturn(page);

        Page<Transaction> result =
                transactionService.getAllTransactionsPaginated(
                        pageable,
                        username
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(transaction, result.getContent().get(0));

        verify(repository).findByUser(user, pageable);
    }
}