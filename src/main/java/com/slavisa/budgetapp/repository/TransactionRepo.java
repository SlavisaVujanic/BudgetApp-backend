package com.slavisa.budgetapp.repository;

import com.slavisa.budgetapp.model.Transaction;
import com.slavisa.budgetapp.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Integer> {

    List<Transaction> findByAccount_AccountIDAndType(Integer accountId, TransactionType type);
    List<Transaction> findByAccount_AccountIDAndDateBetween(Integer accountID, LocalDate start, LocalDate end);
    List<Transaction> findByAccount_AccountIDAndTypeAndDateBetween(
            Integer accountID, TransactionType type, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(Integer accountID, Integer categoryID, LocalDate start, LocalDate end);
    void deleteByAccount_AccountID(Integer accountID);
    List<Transaction> findByAccountAccountID(Integer accountID);

}
