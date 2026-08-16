package com.parneel.FinTrack.transaction;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/Transactions")
public class TransactionController {
    private TransactionService service;

    @Autowired
    public void setTransactionService(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public Transaction createTransaction(@Valid @RequestBody TransactionRequest request, Authentication authentication) {
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(request.getAmount());
        newTransaction.setDate(request.getDate());
        newTransaction.setDescription(request.getDescription());
        newTransaction.setCategory(request.getCategory());
        newTransaction.setType(request.getType());
        return service.addTransaction(newTransaction, authentication.getName());
    }

    @GetMapping
    public List<Transaction> getAllTransactions(Authentication authentication) {
        return service.getAllTransactions(authentication.getName());
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable int id, Authentication authentication) {
        return service.getTransactionById(id, authentication.getName());
    }

    @GetMapping("/paginated")
    public Page<Transaction> getTransactionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);

        return service.getAllTransactionsPaginated(
                pageable,
                authentication.getName()
        );
    }

    @GetMapping("/category/{category}")
    public List<Transaction> getTransactionsByCategory(
            @PathVariable String category,
            Authentication authentication) {

        return service.getTransactionsByCategory(
                category,
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable int id, @Valid @RequestBody TransactionRequest request, Authentication authentication) {
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(request.getAmount());
        updatedTransaction.setDate(request.getDate());
        updatedTransaction.setDescription(request.getDescription());
        updatedTransaction.setCategory(request.getCategory());
        updatedTransaction.setType(request.getType());
        return service.updateTransaction(id,
                updatedTransaction,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(
            @PathVariable int id,
            Authentication authentication) {

        service.deleteTransaction(
                id,
                authentication.getName()
        );
    }
}
