package com.parneel.FinTrack.transaction;

import com.parneel.FinTrack.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByUser(User user);
    Optional<Transaction> findByIdAndUser(int id, User user);
    List<Transaction> findByUserAndCategory(User user, String category);
    Page<Transaction> findByUser(User user, Pageable pageable);
}
