package com.parneel.FinTrack.transaction;

import com.parneel.FinTrack.user.User;
import com.parneel.FinTrack.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Transaction addTransaction(Transaction transaction, String username) {
        User user = getUser(username);
        transaction.setUser(user);
        return repository.save(transaction);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
    public List<Transaction> getAllTransactions(String username) {
        User user = getUser(username);
        return repository.findByUser(user);
    }

    public List<Transaction> getTransactionsByCategory(
            String category,
            String username) {

        User user = getUser(username);

        return repository.findByUserAndCategory(user, category);
    }

    public Transaction getTransactionById(int id, String username) {
        User user = getUser(username);

        return repository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction with id " + id + " not found"
                        ));
    }

    public Page<Transaction> getAllTransactionsPaginated(
            Pageable pageable,
            String username) {

        User user = getUser(username);

        return repository.findByUser(user, pageable);
    }
    public Transaction updateTransaction(
            int id,
            Transaction updatedTransaction,
            String username) {

        User user = getUser(username);

        Transaction transaction = repository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction with id " + id + " not found"
                        ));

        transaction.setDescription(updatedTransaction.getDescription());
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setDate(updatedTransaction.getDate());
        transaction.setType(updatedTransaction.getType());
        transaction.setCategory(updatedTransaction.getCategory());

        return repository.save(transaction);
    }
    public void deleteTransaction(int id, String username) {

        User user = getUser(username);

        Transaction transaction = repository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction with id " + id + " not found"
                        ));

        repository.delete(transaction);
    }

}
