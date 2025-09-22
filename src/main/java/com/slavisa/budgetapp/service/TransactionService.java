package com.slavisa.budgetapp.service;

import com.slavisa.budgetapp.model.Transaction;
import com.slavisa.budgetapp.model.TransactionType;
import com.slavisa.budgetapp.repository.TransactionRepo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;

    public TransactionService(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public List<Transaction> getAllTransactions(){
        return transactionRepo.findAll();
    }

    public Optional<Transaction> getTransactionById(Integer transactionID){
        return transactionRepo.findById(transactionID);
    }

    public void deleteTransaction(Integer transactionID){
        transactionRepo.deleteById(transactionID);
    }

    public Transaction updateTransaction(Integer transactionID, Transaction transaction){
        Transaction transaction1 = transactionRepo.findById(transactionID).orElseThrow(() -> new RuntimeException("Transaction doesn't exist."));
        transaction1.setAccount(transaction.getAccount());
        transaction1.setCategory(transaction.getCategory());
        transaction1.setDescription(transaction.getDescription());
        transaction1.setAmount(transaction.getAmount());
        transaction1.setType(transaction.getType());
        transaction1.setDate(transaction.getDate());
        return transactionRepo.save(transaction1);
    }

    public Transaction addTransaction(@Valid Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    public BigDecimal getTotalIncome(Integer accountID){
        return transactionRepo.findByAccount_AccountIDAndType(accountID, TransactionType.INCOME)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense(Integer accountID){
        return transactionRepo.findByAccount_AccountIDAndType(accountID,TransactionType.EXPENSE)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    public BigDecimal getBalance(Integer accountID){
        return getTotalIncome(accountID).subtract(getTotalExpense(accountID));
    }

    public List<Transaction> getTransactionsByMonth(Integer accountID, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return transactionRepo.findByAccount_AccountIDAndDateBetween(accountID, start, end);
    }

    public Map<String, BigDecimal> getExpenseByCategory(Integer accountID){
        return transactionRepo.findByAccount_AccountIDAndType(accountID,TransactionType.EXPENSE)
                .stream()
                .collect(Collectors.groupingBy(tx->tx.getCategory().getTitle(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount,BigDecimal::add)));
    }

    public Map<String, BigDecimal> getIncomeByCategory(Integer accountID){
        return transactionRepo.findByAccount_AccountIDAndType(accountID,TransactionType.INCOME)
                .stream()
                .collect(Collectors.groupingBy(tx->tx.getCategory().getTitle(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount,BigDecimal::add)));
    }

    public List<Transaction> getTransactionsBetweenDates(Integer accountID, LocalDate startDate, LocalDate endDate) {
        return transactionRepo.findByAccount_AccountIDAndDateBetween(accountID, startDate, endDate);
    }

    public BigDecimal getTotalIncomeBetweenDates(Integer accountID, LocalDate start, LocalDate end) {
        return transactionRepo
                .findByAccount_AccountIDAndTypeAndDateBetween(accountID, TransactionType.INCOME, start, end)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpenseBetweenDates(Integer accountID, LocalDate start, LocalDate end) {
        return transactionRepo
                .findByAccount_AccountIDAndTypeAndDateBetween(accountID, TransactionType.EXPENSE, start, end)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBalanceBetweenDates(Integer accountID, LocalDate start, LocalDate end) {
        return getTotalIncomeBetweenDates(accountID, start, end)
                .subtract(getTotalExpenseBetweenDates(accountID, start, end));
    }

    public BigDecimal getAverageExpenseByMonth(Integer accountID, int month, int year) {
        List<Transaction> expenses = transactionRepo.findByAccount_AccountIDAndType(accountID, TransactionType.EXPENSE).stream()
                .filter(tx -> tx.getDate().getMonthValue() == month && tx.getDate().getYear() == year)
                .toList();

        return expenses.isEmpty() ? BigDecimal.ZERO :
                expenses.stream().map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAverageIncomeByMonth(Integer accountID, int month, int year) {
        List<Transaction> expenses = transactionRepo.findByAccount_AccountIDAndType(accountID, TransactionType.INCOME).stream()
                .filter(tx -> tx.getDate().getMonthValue() == month && tx.getDate().getYear() == year)
                .toList();

        return expenses.isEmpty() ? BigDecimal.ZERO :
                expenses.stream().map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);
    }

    public List<Transaction> getTransactionsByCategoryAndDate(Integer accountID, Integer categoryID, LocalDate start, LocalDate end) {
        return transactionRepo.findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(accountID, categoryID, start, end);
    }

    public void deleteAllTransactions(Integer accountID) {
        transactionRepo.deleteByAccount_AccountID(accountID);
    }

    public Map<String, BigDecimal> getMonthlyExpenses(Integer accountID, int year) {
        return transactionRepo.findByAccount_AccountIDAndType(accountID, TransactionType.EXPENSE).stream()
                .filter(tx -> tx.getDate().getYear() == year)
                .collect(Collectors.groupingBy(tx -> tx.getDate().getMonth().name(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    public Map<String, BigDecimal> getMonthlyIncomes(Integer accountID, int year) {
        return transactionRepo.findByAccount_AccountIDAndType(accountID, TransactionType.INCOME).stream()
                .filter(tx -> tx.getDate().getYear() == year)
                .collect(Collectors.groupingBy(tx -> tx.getDate().getMonth().name(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    public List<Transaction> getTransactionsByAccount(Integer accountID) {
        return transactionRepo.findByAccountAccountID(accountID);
    }
}
