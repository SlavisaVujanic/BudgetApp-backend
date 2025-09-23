package com.slavisa.budgetapp.controller;

import com.slavisa.budgetapp.model.Transaction;
import com.slavisa.budgetapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setTransactionID(1);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setDate(LocalDate.of(2025, 9, 15));
    }

    @Test
    void testGetAllTransactions() {
        when(transactionService.getAllTransactions()).thenReturn(List.of(testTransaction));

        List<Transaction> result = transactionController.getAllTransactions();

        assertEquals(1, result.size());
        assertEquals(testTransaction, result.getFirst());
        verify(transactionService).getAllTransactions();
    }

    @Test
    void testGetTransactionByID() {
        when(transactionService.getTransactionById(1)).thenReturn(Optional.of(testTransaction));

        Optional<Transaction> result = transactionController.getTransactionByID(1);

        assertTrue(result.isPresent());
        assertEquals(testTransaction, result.get());
        verify(transactionService).getTransactionById(1);
    }

    @Test
    void testDeleteTransactionByID() {
        transactionController.deleteTransactionByID(1);

        verify(transactionService).deleteTransaction(1);
    }

    @Test
    void testAddTransaction() {
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(new BigDecimal("200.00"));

        when(transactionService.addTransaction(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionController.addTransaction(newTransaction);

        assertEquals(testTransaction, result);
        verify(transactionService).addTransaction(newTransaction);
    }

    @Test
    void testUpdateTransaction() {
        Transaction updateTransaction = new Transaction();
        updateTransaction.setAmount(new BigDecimal("300.00"));

        when(transactionService.updateTransaction(anyInt(), any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionController.updateTransaction(1, updateTransaction);

        assertEquals(testTransaction, result);
        verify(transactionService).updateTransaction(1, updateTransaction);
    }

    @Test
    void testGetTotalIncome() {
        when(transactionService.getTotalIncome(1)).thenReturn(new BigDecimal("1000.00"));

        BigDecimal result = transactionController.getTotalIncome(1);

        assertEquals(new BigDecimal("1000.00"), result);
        verify(transactionService).getTotalIncome(1);
    }

    @Test
    void testGetTotalExpense() {
        when(transactionService.getTotalExpense(1)).thenReturn(new BigDecimal("500.00"));

        BigDecimal result = transactionController.getTotalExpense(1);

        assertEquals(new BigDecimal("500.00"), result);
        verify(transactionService).getTotalExpense(1);
    }

    @Test
    void testGetBalance() {
        when(transactionService.getBalance(1)).thenReturn(new BigDecimal("500.00"));

        BigDecimal result = transactionController.getBalance(1);

        assertEquals(new BigDecimal("500.00"), result);
        verify(transactionService).getBalance(1);
    }

    @Test
    void testGetTransactionsByMonth() {
        when(transactionService.getTransactionsByMonth(1, 9, 2025)).thenReturn(List.of(testTransaction));

        List<Transaction> result = transactionController.getTransactionsByMonth(1, 2025, 9);

        assertEquals(1, result.size());
        assertEquals(testTransaction, result.getFirst());
        verify(transactionService).getTransactionsByMonth(1, 9, 2025);
    }

    @Test
    void testGetExpenseByCategory() {
        Map<String, BigDecimal> expenses = Map.of("Food", new BigDecimal("200.00"));
        when(transactionService.getExpenseByCategory(1)).thenReturn(expenses);

        Map<String, BigDecimal> result = transactionController.getExpenseByCategory(1);

        assertEquals(expenses, result);
        assertEquals(new BigDecimal("200.00"), result.get("Food"));
        verify(transactionService).getExpenseByCategory(1);
    }

    @Test
    void testGetIncomeByCategory() {
        Map<String, BigDecimal> incomes = Map.of("Salary", new BigDecimal("3000.00"));
        when(transactionService.getIncomeByCategory(1)).thenReturn(incomes);

        Map<String, BigDecimal> result = transactionController.getIncomeByCategory(1);

        assertEquals(incomes, result);
        assertEquals(new BigDecimal("3000.00"), result.get("Salary"));
        verify(transactionService).getIncomeByCategory(1);
    }

    @Test
    void testGetTransactionsBetweenDates() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        when(transactionService.getTransactionsBetweenDates(1, start, end)).thenReturn(List.of(testTransaction));

        List<Transaction> result = transactionController.getTransactionsBetweenDates(1, "2025-01-01", "2025-01-31");

        assertEquals(1, result.size());
        assertEquals(testTransaction, result.getFirst());
        verify(transactionService).getTransactionsBetweenDates(1, start, end);
    }

    @Test
    void testGetTotalIncomeBetweenDates() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        when(transactionService.getTotalIncomeBetweenDates(1, start, end)).thenReturn(new BigDecimal("2000.00"));

        BigDecimal result = transactionController.getTotalIncomeBetweenDates(1, "2025-01-01", "2025-01-31");

        assertEquals(new BigDecimal("2000.00"), result);
        verify(transactionService).getTotalIncomeBetweenDates(1, start, end);
    }

    @Test
    void testGetTotalExpenseBetweenDates() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        when(transactionService.getTotalExpenseBetweenDates(1, start, end)).thenReturn(new BigDecimal("800.00"));

        BigDecimal result = transactionController.getTotalExpenseBetweenDates(1, "2025-01-01", "2025-01-31");

        assertEquals(new BigDecimal("800.00"), result);
        verify(transactionService).getTotalExpenseBetweenDates(1, start, end);
    }

    @Test
    void testGetBalanceBetweenDates() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        when(transactionService.getBalanceBetweenDates(1, start, end)).thenReturn(new BigDecimal("1200.00"));

        BigDecimal result = transactionController.getBalanceBetweenDates(1, "2025-01-01", "2025-01-31");

        assertEquals(new BigDecimal("1200.00"), result);
        verify(transactionService).getBalanceBetweenDates(1, start, end);
    }

    @Test
    void testGetAverageExpenseByMonth() {
        when(transactionService.getAverageExpenseByMonth(1, 9, 2025)).thenReturn(new BigDecimal("150.00"));

        BigDecimal result = transactionController.getAverageExpenseByMonth(1, 2025, 9);

        assertEquals(new BigDecimal("150.00"), result);
        verify(transactionService).getAverageExpenseByMonth(1, 9, 2025);
    }

    @Test
    void testGetAverageIncomeByMonth() {
        when(transactionService.getAverageIncomeByMonth(1, 9, 2025)).thenReturn(new BigDecimal("2500.00"));

        BigDecimal result = transactionController.getAverageIncomeByMonth(1, 2025, 9);

        assertEquals(new BigDecimal("2500.00"), result);
        verify(transactionService).getAverageIncomeByMonth(1, 9, 2025);
    }

    @Test
    void testGetTransactionsByCategoryAndDate() {
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 30);
        when(transactionService.getTransactionsByCategoryAndDate(1, 5, start, end)).thenReturn(List.of(testTransaction));

        List<Transaction> result = transactionController.getTransactionsByCategoryAndDate(1, 5, "2025-09-01", "2025-09-30");

        assertEquals(1, result.size());
        assertEquals(testTransaction, result.getFirst());
        verify(transactionService).getTransactionsByCategoryAndDate(1, 5, start, end);
    }

    @Test
    void testDeleteAllTransactions() {
        transactionController.deleteAllTransactions(1);

        verify(transactionService).deleteAllTransactions(1);
    }

    @Test
    void testGetMonthlyExpenses() {
        Map<String, BigDecimal> expenses = Map.of("SEPTEMBER", new BigDecimal("300.00"));
        when(transactionService.getMonthlyExpenses(1, 2025)).thenReturn(expenses);

        Map<String, BigDecimal> result = transactionController.getMonthlyExpenses(1, 2025);

        assertEquals(expenses, result);
        assertEquals(new BigDecimal("300.00"), result.get("SEPTEMBER"));
        verify(transactionService).getMonthlyExpenses(1, 2025);
    }

    @Test
    void testGetMonthlyIncomes() {
        Map<String, BigDecimal> incomes = Map.of("SEPTEMBER", new BigDecimal("2000.00"));
        when(transactionService.getMonthlyIncomes(1, 2025)).thenReturn(incomes);

        Map<String, BigDecimal> result = transactionController.getMonthlyIncomes(1, 2025);

        assertEquals(incomes, result);
        assertEquals(new BigDecimal("2000.00"), result.get("SEPTEMBER"));
        verify(transactionService).getMonthlyIncomes(1, 2025);
    }

    @Test
    void testGetByAccount() {
        when(transactionService.getTransactionsByAccount(1)).thenReturn(List.of(testTransaction));

        List<Transaction> result = transactionController.getByAccount(1);

        assertEquals(1, result.size());
        assertEquals(testTransaction, result.getFirst());
        verify(transactionService).getTransactionsByAccount(1);
    }
}