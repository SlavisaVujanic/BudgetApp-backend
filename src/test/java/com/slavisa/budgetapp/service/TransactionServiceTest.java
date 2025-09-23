package com.slavisa.budgetapp.service;

import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.model.Category;
import com.slavisa.budgetapp.model.Transaction;
import com.slavisa.budgetapp.model.TransactionType;
import com.slavisa.budgetapp.repository.TransactionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepo transactionRepo;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void testGetAllTransactions(){
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        transaction1.setTransactionID(12);
        transaction2.setTransactionID(32);

        when(transactionRepo.findAll()).thenReturn(Arrays.asList(transaction1,transaction2));

        List<Transaction> transactions = transactionService.getAllTransactions();

        assertEquals(2,transactions.size());
        verify(transactionRepo).findAll();
    }

    @Test
    void testSuccessfullyGetTransactionByID(){
        Transaction transaction = new Transaction();
        transaction.setTransactionID(43);
        transaction.setDescription("Description");

        when(transactionRepo.findById(transaction.getTransactionID())).thenReturn(Optional.of(transaction));

        Optional<Transaction> foundTransaction = transactionService.getTransactionById(43);

        assertTrue(foundTransaction.isPresent());
        assertEquals("Description",foundTransaction.get().getDescription());
    }

    @Test
    void testFailedGetTransactionByID(){
        int transactionID = 26;
        when(transactionRepo.findById(transactionID)).thenReturn(Optional.empty());

        Optional<Transaction> transaction = transactionRepo.findById(transactionID);

        assertTrue(transaction.isEmpty());
    }

    @Test
    void testDeleteTransaction(){
        Transaction transaction = new Transaction();
        transaction.setTransactionID(123);

        transactionService.deleteTransaction(123);
        verify(transactionRepo,times(1)).deleteById(123);
    }

    @Test
    void updateTransactionThrowExceptionNotFound() {
        when(transactionRepo.findById(99)).thenReturn(Optional.empty());

        Transaction transactionToUpdate = new Transaction();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transactionService.updateTransaction(99, transactionToUpdate));

        assertEquals("Transaction doesn't exist.", ex.getMessage());
        verify(transactionRepo).findById(99);
        verify(transactionRepo, never()).save(any());
    }

    @Test
    void updateTransactionUpdateAllFields() {
        Transaction existing = new Transaction();
        existing.setTransactionID(1);
        Account account = new Account();
        account.setAccountID(10);
        Category category = new Category();
        category.setCategoryID(5);
        category.setTitle("Food");

        Transaction input = new Transaction();
        input.setAccount(account);
        input.setCategory(category);
        input.setDescription("Lunch at restaurant");
        input.setAmount(BigDecimal.valueOf(25.5));
        input.setType(TransactionType.EXPENSE);
        input.setDate(LocalDate.of(2025, 9, 20));

        when(transactionRepo.findById(1)).thenReturn(Optional.of(existing));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.updateTransaction(1, input);

        assertEquals(input.getAccount(), result.getAccount());
        assertEquals(input.getCategory(), result.getCategory());
        assertEquals("Lunch at restaurant", result.getDescription());
        assertEquals(new BigDecimal("25.5"), result.getAmount());
        assertEquals(TransactionType.EXPENSE, result.getType());
        assertEquals(LocalDate.of(2025, 9, 20), result.getDate());

        verify(transactionRepo).findById(1);
        verify(transactionRepo).save(existing);
    }

    @Test
    void testAddTransaction(){
        Transaction input = new Transaction();
        input.setDescription("New transaction");

        Transaction saved = new Transaction();
        saved.setTransactionID(1);
        saved.setDescription("New transaction");

        when(transactionRepo.save(any(Transaction.class))).thenReturn(saved);

        Transaction result = transactionService.addTransaction(input);

        assertNotNull(result);
        assertEquals(1, result.getTransactionID());
        assertEquals("New transaction", result.getDescription());

        verify(transactionRepo).save(input);
    }

    @Test
    void testGetTotalIncome(){
        Account account = new Account();
        account.setAccountID(2);

        Transaction tr1 = new Transaction();
        tr1.setType(TransactionType.INCOME);
        tr1.setAmount(new BigDecimal("1000"));
        tr1.setAccount(account);

        Transaction tr2 = new Transaction();
        tr2.setType(TransactionType.INCOME);
        tr2.setAmount(new BigDecimal("1050"));

        Transaction tr3 = new Transaction();
        tr3.setType(TransactionType.INCOME);
        tr3.setAmount(new BigDecimal("950"));

        List<Transaction> incomes = Arrays.asList(tr1,tr2,tr3);

        when(transactionRepo.findByAccount_AccountIDAndType(account.getAccountID(),TransactionType.INCOME)).thenReturn(incomes);

        BigDecimal result = transactionService.getTotalIncome(2);

        assertEquals(new BigDecimal("3000"),result);
        verify(transactionRepo).findByAccount_AccountIDAndType(account.getAccountID(),TransactionType.INCOME);
    }

    @Test
    void testGetTotalExpense(){
        Account account = new Account();
        account.setAccountID(4);

        Transaction tr1 = new Transaction();
        tr1.setType(TransactionType.EXPENSE);
        tr1.setAmount(new BigDecimal("1000"));
        tr1.setAccount(account);

        Transaction tr2 = new Transaction();
        tr2.setType(TransactionType.EXPENSE);
        tr2.setAmount(new BigDecimal("50"));

        Transaction tr3 = new Transaction();
        tr3.setType(TransactionType.EXPENSE);
        tr3.setAmount(new BigDecimal("450"));

        List<Transaction> expenses = Arrays.asList(tr1,tr2,tr3);

        when(transactionRepo.findByAccount_AccountIDAndType(account.getAccountID(),TransactionType.EXPENSE)).thenReturn(expenses);

        BigDecimal result = transactionService.getTotalExpense(4);

        assertEquals(new BigDecimal("1500"),result);
        verify(transactionRepo).findByAccount_AccountIDAndType(account.getAccountID(),TransactionType.EXPENSE);
    }

    @Test
    void testGetBalance(){
        int accountId = 1;

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("600"));

        Transaction income2 = new Transaction();
        income2.setAmount(new BigDecimal("400"));

        Transaction expense = new Transaction();
        expense.setAmount(new BigDecimal("400"));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income1, income2));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(List.of(expense));

        BigDecimal balance = transactionService.getBalance(accountId);

        assertEquals(new BigDecimal("600"), balance);
    }

    @Test
    void testGetTransactionsByMonth(){
        int accountId = 1;
        int month = 9;
        int year = 2025;

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setDate(LocalDate.of(2025, 9, 10));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setDate(LocalDate.of(2025, 9, 15));

        List<Transaction> mockTransactions = List.of(t1, t2);

        when(transactionRepo.findByAccount_AccountIDAndDateBetween(accountId, start, end))
                .thenReturn(mockTransactions);

        List<Transaction> result = transactionService.getTransactionsByMonth(accountId, month, year);

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("100"), result.getFirst().getAmount());
        assertEquals(LocalDate.of(2025, 9, 10), result.getFirst().getDate());

        verify(transactionRepo).findByAccount_AccountIDAndDateBetween(accountId, start, end);
    }

    @Test
    void testGetExpenseByCategory() {
        int accountId = 1;

        Category food = new Category();
        food.setTitle("Food");

        Category transport = new Category();
        transport.setTitle("Transport");

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setCategory(food);
        t1.setType(TransactionType.EXPENSE);

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("50"));
        t2.setCategory(food);
        t2.setType(TransactionType.EXPENSE);

        Transaction t3 = new Transaction();
        t3.setAmount(new BigDecimal("30"));
        t3.setCategory(transport);
        t3.setType(TransactionType.EXPENSE);

        List<Transaction> expenses = List.of(t1, t2, t3);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(expenses);

        Map<String, BigDecimal> result = transactionService.getExpenseByCategory(accountId);

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("150"), result.get("Food"));
        assertEquals(new BigDecimal("30"), result.get("Transport"));

        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetIncomeByCategory() {
        int accountId = 1;

        Category salary = new Category();
        salary.setTitle("Salary");

        Category bonus = new Category();
        bonus.setTitle("Bonus");

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setCategory(salary);
        t1.setType(TransactionType.INCOME);

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("50"));
        t2.setCategory(bonus);
        t2.setType(TransactionType.INCOME);

        Transaction t3 = new Transaction();
        t3.setAmount(new BigDecimal("30"));
        t3.setCategory(bonus);
        t3.setType(TransactionType.INCOME);

        List<Transaction> incomes = List.of(t1, t2, t3);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(incomes);

        Map<String, BigDecimal> result = transactionService.getIncomeByCategory(accountId);

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("100"), result.get("Salary"));
        assertEquals(new BigDecimal("80"), result.get("Bonus"));

        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetTransactionsBetweenDates() {
        int accountId = 1;
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setDate(LocalDate.of(2025, 9, 10));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setDate(LocalDate.of(2025, 9, 20));

        List<Transaction> mockTransactions = List.of(t1, t2);

        when(transactionRepo.findByAccount_AccountIDAndDateBetween(accountId, startDate, endDate))
                .thenReturn(mockTransactions);

        List<Transaction> result = transactionService.getTransactionsBetweenDates(accountId, startDate, endDate);

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("100"), result.getFirst().getAmount());
        assertEquals(LocalDate.of(2025, 9, 10), result.getFirst().getDate());

        verify(transactionRepo).findByAccount_AccountIDAndDateBetween(accountId, startDate, endDate);
    }

    @Test
    void testGetTotalIncomeBetweenDates() {
        int accountId = 1;
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 30);

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("500"));
        t1.setType(TransactionType.INCOME);
        t1.setDate(LocalDate.of(2025, 9, 10));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("700"));
        t2.setType(TransactionType.INCOME);
        t2.setDate(LocalDate.of(2025, 9, 15));

        List<Transaction> incomes = List.of(t1, t2);

        when(transactionRepo.findByAccount_AccountIDAndTypeAndDateBetween(accountId, TransactionType.INCOME, start, end))
                .thenReturn(incomes);

        BigDecimal totalIncome = transactionService.getTotalIncomeBetweenDates(accountId, start, end);

        assertEquals(new BigDecimal("1200"), totalIncome);

        verify(transactionRepo).findByAccount_AccountIDAndTypeAndDateBetween(accountId, TransactionType.INCOME, start, end);
    }

    @Test
    void testGetTotalExpenseBetweenDates() {
        int accountId = 1;
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 30);

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("300"));
        t1.setType(TransactionType.EXPENSE);
        t1.setDate(LocalDate.of(2025, 9, 5));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setType(TransactionType.EXPENSE);
        t2.setDate(LocalDate.of(2025, 9, 20));

        List<Transaction> expenses = List.of(t1, t2);

        when(transactionRepo.findByAccount_AccountIDAndTypeAndDateBetween(
                        accountId, TransactionType.EXPENSE, start, end))
                .thenReturn(expenses);

        BigDecimal totalExpense = transactionService.getTotalExpenseBetweenDates(accountId, start, end);

        assertEquals(new BigDecimal("500"), totalExpense);

        verify(transactionRepo).findByAccount_AccountIDAndTypeAndDateBetween(
                accountId, TransactionType.EXPENSE, start, end);
    }

    @Test
    void testGetBalanceBetweenDates() {
        int accountId = 1;
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 30);

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("1000"));
        income1.setType(TransactionType.INCOME);
        income1.setDate(LocalDate.of(2025, 9, 10));

        List<Transaction> incomes = List.of(income1);

        Transaction expense1 = new Transaction();
        expense1.setAmount(new BigDecimal("400"));
        expense1.setType(TransactionType.EXPENSE);
        expense1.setDate(LocalDate.of(2025, 9, 15));

        List<Transaction> expenses = List.of(expense1);

        when(transactionRepo.findByAccount_AccountIDAndTypeAndDateBetween(
                        accountId, TransactionType.INCOME, start, end))
                .thenReturn(incomes);

        when(transactionRepo.findByAccount_AccountIDAndTypeAndDateBetween(
                        accountId, TransactionType.EXPENSE, start, end))
                .thenReturn(expenses);

        BigDecimal balance = transactionService.getBalanceBetweenDates(accountId, start, end);

        assertEquals(new BigDecimal("600"), balance);
        verify(transactionRepo).findByAccount_AccountIDAndTypeAndDateBetween(
                accountId, TransactionType.INCOME, start, end);
        verify(transactionRepo).findByAccount_AccountIDAndTypeAndDateBetween(
                accountId, TransactionType.EXPENSE, start, end);
    }

    @Test
    void testGetAverageExpenseByMonthEmptyList() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(List.of());

        BigDecimal avg = transactionService.getAverageExpenseByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);

        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetAverageExpenseByMonthWithTransactions() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setDate(LocalDate.of(2025, 9, 10));
        t1.setType(TransactionType.EXPENSE);

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setDate(LocalDate.of(2025, 9, 15));
        t2.setType(TransactionType.EXPENSE);

        Transaction t3 = new Transaction();
        t3.setAmount(new BigDecimal("50"));
        t3.setDate(LocalDate.of(2025, 8, 20));
        t3.setType(TransactionType.EXPENSE);

        List<Transaction> expenses = List.of(t1, t2, t3);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(expenses);

        BigDecimal avg = transactionService.getAverageExpenseByMonth(accountId, month, year);

        assertEquals(new BigDecimal("150.00"), avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetAverageExpenseByMonthWrongMonth() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setDate(LocalDate.of(2025, 8, 10));
        t1.setType(TransactionType.EXPENSE);

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setDate(LocalDate.of(2025, 10, 15));
        t2.setType(TransactionType.EXPENSE);

        List<Transaction> expenses = List.of(t1, t2);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(expenses);

        BigDecimal avg = transactionService.getAverageExpenseByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetAverageExpenseByMonthWrongYear() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setDate(LocalDate.of(2024, 9, 10));
        t1.setType(TransactionType.EXPENSE);

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setDate(LocalDate.of(2026, 9, 15));
        t2.setType(TransactionType.EXPENSE);

        List<Transaction> expenses = List.of(t1, t2);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(expenses);

        BigDecimal avg = transactionService.getAverageExpenseByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetAverageExpenseByMonthWrongMonthAndYear() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setDate(LocalDate.of(2024, 8, 10));
        t1.setType(TransactionType.EXPENSE);

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setDate(LocalDate.of(2026, 10, 15));
        t2.setType(TransactionType.EXPENSE);

        List<Transaction> expenses = List.of(t1, t2);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(expenses);

        BigDecimal avg = transactionService.getAverageExpenseByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetAverageExpenseByMonthMixedTransactions() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction validTx1 = new Transaction();
        validTx1.setAmount(new BigDecimal("100"));
        validTx1.setDate(LocalDate.of(2025, 9, 10));

        Transaction validTx2 = new Transaction();
        validTx2.setAmount(new BigDecimal("200"));
        validTx2.setDate(LocalDate.of(2025, 9, 20));

        Transaction invalidMonth = new Transaction();
        invalidMonth.setAmount(new BigDecimal("50"));
        invalidMonth.setDate(LocalDate.of(2025, 8, 15));

        Transaction invalidYear = new Transaction();
        invalidYear.setAmount(new BigDecimal("75"));
        invalidYear.setDate(LocalDate.of(2024, 9, 15));

        Transaction invalidBoth = new Transaction();
        invalidBoth.setAmount(new BigDecimal("25"));
        invalidBoth.setDate(LocalDate.of(2024, 8, 15));

        List<Transaction> expenses = List.of(validTx1, validTx2, invalidMonth, invalidYear, invalidBoth);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(expenses);

        BigDecimal avg = transactionService.getAverageExpenseByMonth(accountId, month, year);
        assertEquals(new BigDecimal("150.00"), avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetAverageIncomeByMonthEmptyList() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of());

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthSingleTransaction() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction income = new Transaction();
        income.setAmount(new BigDecimal("1000.00"));
        income.setDate(LocalDate.of(2025, 9, 15));
        income.setType(TransactionType.INCOME);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(new BigDecimal("1000.00"), avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthMultipleTransactions() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("1000"));
        income1.setDate(LocalDate.of(2025, 9, 10));
        income1.setType(TransactionType.INCOME);

        Transaction income2 = new Transaction();
        income2.setAmount(new BigDecimal("1500"));
        income2.setDate(LocalDate.of(2025, 9, 20));
        income2.setType(TransactionType.INCOME);

        Transaction income3 = new Transaction();
        income3.setAmount(new BigDecimal("2000"));
        income3.setDate(LocalDate.of(2025, 9, 25));
        income3.setType(TransactionType.INCOME);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income1, income2, income3));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(new BigDecimal("1500.00"), avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthWrongMonth() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("1000"));
        income1.setDate(LocalDate.of(2025, 8, 15));
        income1.setType(TransactionType.INCOME);

        Transaction income2 = new Transaction();
        income2.setAmount(new BigDecimal("1500"));
        income2.setDate(LocalDate.of(2025, 10, 20));
        income2.setType(TransactionType.INCOME);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income1, income2));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthWrongYear() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("1000"));
        income1.setDate(LocalDate.of(2024, 9, 15));
        income1.setType(TransactionType.INCOME);

        Transaction income2 = new Transaction();
        income2.setAmount(new BigDecimal("1500"));
        income2.setDate(LocalDate.of(2026, 9, 20));
        income2.setType(TransactionType.INCOME);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income1, income2));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthWrongMonthAndYear() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("1000"));
        income1.setDate(LocalDate.of(2024, 8, 15));
        income1.setType(TransactionType.INCOME);

        Transaction income2 = new Transaction();
        income2.setAmount(new BigDecimal("1500"));
        income2.setDate(LocalDate.of(2026, 10, 20));
        income2.setType(TransactionType.INCOME);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income1, income2));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthMixedTransactions() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction validIncome1 = new Transaction();
        validIncome1.setAmount(new BigDecimal("1000"));
        validIncome1.setDate(LocalDate.of(2025, 9, 10));

        Transaction validIncome2 = new Transaction();
        validIncome2.setAmount(new BigDecimal("2000"));
        validIncome2.setDate(LocalDate.of(2025, 9, 25));

        Transaction invalidMonth = new Transaction();
        invalidMonth.setAmount(new BigDecimal("500"));
        invalidMonth.setDate(LocalDate.of(2025, 8, 15));

        Transaction invalidYear = new Transaction();
        invalidYear.setAmount(new BigDecimal("750"));
        invalidYear.setDate(LocalDate.of(2024, 9, 15));

        Transaction invalidBoth = new Transaction();
        invalidBoth.setAmount(new BigDecimal("300"));
        invalidBoth.setDate(LocalDate.of(2024, 8, 15));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(validIncome1, validIncome2, invalidMonth, invalidYear, invalidBoth));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(new BigDecimal("1500.00"), avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthPrecisionRounding() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("100"));
        income1.setDate(LocalDate.of(2025, 9, 10));
        income1.setType(TransactionType.INCOME);

        Transaction income2 = new Transaction();
        income2.setAmount(new BigDecimal("200"));
        income2.setDate(LocalDate.of(2025, 9, 20));
        income2.setType(TransactionType.INCOME);

        Transaction income3 = new Transaction();
        income3.setAmount(new BigDecimal("300"));
        income3.setDate(LocalDate.of(2025, 9, 25));
        income3.setType(TransactionType.INCOME);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income1, income2, income3));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(new BigDecimal("200.00"), avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthComplexRounding() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("100.00"));
        income1.setDate(LocalDate.of(2025, 9, 10));
        income1.setType(TransactionType.INCOME);

        Transaction income2 = new Transaction();
        income2.setAmount(new BigDecimal("200.00"));
        income2.setDate(LocalDate.of(2025, 9, 20));
        income2.setType(TransactionType.INCOME);

        Transaction income3 = new Transaction();
        income3.setAmount(new BigDecimal("233.33"));
        income3.setDate(LocalDate.of(2025, 9, 25));
        income3.setType(TransactionType.INCOME);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income1, income2, income3));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(new BigDecimal("177.78"), avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetAverageIncomeByMonthEmptyBranch() {
        int accountId = 1;
        int month = 9;
        int year = 2025;

        Transaction income = new Transaction();
        income.setAmount(new BigDecimal("1000"));
        income.setDate(LocalDate.of(2025, 8, 15));
        income.setType(TransactionType.INCOME);

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(income));

        BigDecimal avg = transactionService.getAverageIncomeByMonth(accountId, month, year);

        assertEquals(BigDecimal.ZERO, avg);
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetTransactionsByCategoryAndDateValidData() {
        int accountId = 1;
        int categoryId = 5;
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        Transaction t1 = new Transaction();
        t1.setTransactionID(1);
        t1.setAmount(new BigDecimal("100.00"));
        t1.setDate(LocalDate.of(2025, 9, 10));

        Transaction t2 = new Transaction();
        t2.setTransactionID(2);
        t2.setAmount(new BigDecimal("250.50"));
        t2.setDate(LocalDate.of(2025, 9, 25));

        List<Transaction> expectedTransactions = List.of(t1, t2);

        when(transactionRepo.findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                accountId, categoryId, startDate, endDate))
                .thenReturn(expectedTransactions);

        List<Transaction> result = transactionService.getTransactionsByCategoryAndDate(
                accountId, categoryId, startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedTransactions, result);
        assertEquals(new BigDecimal("100.00"), result.get(0).getAmount());
        assertEquals(new BigDecimal("250.50"), result.get(1).getAmount());

        verify(transactionRepo, times(1))
                .findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                        accountId, categoryId, startDate, endDate);
    }

    @Test
    void testGetTransactionsByCategoryAndDateEmptyResult() {
        int accountId = 1;
        int categoryId = 999;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(transactionRepo.findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                accountId, categoryId, startDate, endDate))
                .thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getTransactionsByCategoryAndDate(
                accountId, categoryId, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepo, times(1))
                .findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                        accountId, categoryId, startDate, endDate);
    }

    @Test
    void testGetTransactionsByCategoryAndDateSingleTransaction() {
        int accountId = 2;
        int categoryId = 3;
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        Transaction singleTransaction = new Transaction();
        singleTransaction.setTransactionID(10);
        singleTransaction.setAmount(new BigDecimal("75.25"));
        singleTransaction.setDate(LocalDate.of(2025, 6, 15));

        when(transactionRepo.findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                accountId, categoryId, startDate, endDate))
                .thenReturn(List.of(singleTransaction));

        List<Transaction> result = transactionService.getTransactionsByCategoryAndDate(
                accountId, categoryId, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(singleTransaction, result.getFirst());
        assertEquals(new BigDecimal("75.25"), result.getFirst().getAmount());

        verify(transactionRepo, times(1))
                .findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                        accountId, categoryId, startDate, endDate);
    }

    @Test
    void testGetTransactionsByCategoryAndDateSameDateStartEnd() {
        int accountId = 1;
        int categoryId = 2;
        LocalDate sameDate = LocalDate.of(2025, 9, 15);

        Transaction dayTransaction = new Transaction();
        dayTransaction.setTransactionID(5);
        dayTransaction.setAmount(new BigDecimal("45.00"));
        dayTransaction.setDate(sameDate);

        when(transactionRepo.findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                accountId, categoryId, sameDate, sameDate))
                .thenReturn(List.of(dayTransaction));

        List<Transaction> result = transactionService.getTransactionsByCategoryAndDate(
                accountId, categoryId, sameDate, sameDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dayTransaction, result.getFirst());

        verify(transactionRepo, times(1))
                .findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                        accountId, categoryId, sameDate, sameDate);
    }

    @Test
    void testGetTransactionsByCategoryAndDateNullAccountId() {
        int categoryId = 1;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(transactionRepo.findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                null, categoryId, startDate, endDate))
                .thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getTransactionsByCategoryAndDate(
                null, categoryId, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepo, times(1))
                .findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                        null, categoryId, startDate, endDate);
    }

    @Test
    void testGetTransactionsByCategoryAndDateNullCategoryId() {
        int accountId = 1;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(transactionRepo.findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                accountId, null, startDate, endDate))
                .thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getTransactionsByCategoryAndDate(
                accountId, null, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepo, times(1))
                .findByAccount_AccountIDAndCategory_CategoryIDAndDateBetween(
                        accountId, null, startDate, endDate);
    }

    @Test
    void testDeleteAllTransactionsValidAccountId() {
        int accountId = 1;
        doNothing().when(transactionRepo).deleteByAccount_AccountID(accountId);

        transactionService.deleteAllTransactions(accountId);

        verify(transactionRepo, times(1)).deleteByAccount_AccountID(accountId);
    }

    @Test
    void testDeleteAllTransactionsWithNullAccountId() {
        doNothing().when(transactionRepo).deleteByAccount_AccountID(null);

        transactionService.deleteAllTransactions(null);

        verify(transactionRepo, times(1)).deleteByAccount_AccountID(null);
    }

    @Test
    void testGetMonthlyExpensesValidData() {
        int accountId = 1;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setDate(LocalDate.of(2025, 9, 10));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setDate(LocalDate.of(2025, 9, 20));

        Transaction t3 = new Transaction();
        t3.setAmount(new BigDecimal("150"));
        t3.setDate(LocalDate.of(2025, 10, 5));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(List.of(t1, t2, t3));

        Map<String, BigDecimal> result = transactionService.getMonthlyExpenses(accountId, year);

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("300"), result.get("SEPTEMBER"));
        assertEquals(new BigDecimal("150"), result.get("OCTOBER"));

        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetMonthlyExpensesEmptyTransactions() {
        int accountId = 1;
        int year = 2025;

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(List.of());

        Map<String, BigDecimal> result = transactionService.getMonthlyExpenses(accountId, year);

        assertTrue(result.isEmpty());
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetMonthlyExpensesWrongYear() {
        int accountId = 1;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setDate(LocalDate.of(2024, 9, 10));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setDate(LocalDate.of(2026, 10, 5));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(List.of(t1, t2));

        Map<String, BigDecimal> result = transactionService.getMonthlyExpenses(accountId, year);

        assertTrue(result.isEmpty());
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetMonthlyExpensesSingleMonth() {
        int accountId = 1;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("50"));
        t1.setDate(LocalDate.of(2025, 12, 1));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("75"));
        t2.setDate(LocalDate.of(2025, 12, 15));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(List.of(t1, t2));

        Map<String, BigDecimal> result = transactionService.getMonthlyExpenses(accountId, year);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("125"), result.get("DECEMBER"));
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetMonthlyExpensesMixedYears() {
        int accountId = 1;
        int year = 2025;

        Transaction validTx = new Transaction();
        validTx.setAmount(new BigDecimal("300"));
        validTx.setDate(LocalDate.of(2025, 6, 10));

        Transaction invalidTx = new Transaction();
        invalidTx.setAmount(new BigDecimal("500"));
        invalidTx.setDate(LocalDate.of(2024, 6, 10));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(List.of(validTx, invalidTx));

        Map<String, BigDecimal> result = transactionService.getMonthlyExpenses(accountId, year);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("300"), result.get("JUNE"));
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.EXPENSE);
    }

    @Test
    void testGetMonthlyIncomesValidData() {
        int accountId = 1;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("1000"));
        t1.setDate(LocalDate.of(2025, 3, 15));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("1200"));
        t2.setDate(LocalDate.of(2025, 3, 25));

        Transaction t3 = new Transaction();
        t3.setAmount(new BigDecimal("800"));
        t3.setDate(LocalDate.of(2025, 4, 10));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(t1, t2, t3));

        Map<String, BigDecimal> result = transactionService.getMonthlyIncomes(accountId, year);

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("2200"), result.get("MARCH"));
        assertEquals(new BigDecimal("800"), result.get("APRIL"));

        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetMonthlyIncomesEmptyTransactions() {
        int accountId = 1;
        int year = 2025;

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of());

        Map<String, BigDecimal> result = transactionService.getMonthlyIncomes(accountId, year);

        assertTrue(result.isEmpty());
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetMonthlyIncomesWrongYear() {
        int accountId = 1;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("500"));
        t1.setDate(LocalDate.of(2024, 5, 10));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("700"));
        t2.setDate(LocalDate.of(2026, 6, 5));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(t1, t2));

        Map<String, BigDecimal> result = transactionService.getMonthlyIncomes(accountId, year);

        assertTrue(result.isEmpty());
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetMonthlyIncomesSingleMonth() {
        int accountId = 1;
        int year = 2025;

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("2000"));
        t1.setDate(LocalDate.of(2025, 1, 1));

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("2500"));
        t2.setDate(LocalDate.of(2025, 1, 31));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(t1, t2));

        Map<String, BigDecimal> result = transactionService.getMonthlyIncomes(accountId, year);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("4500"), result.get("JANUARY"));
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetMonthlyIncomesMixedYears() {
        int accountId = 1;
        int year = 2025;

        Transaction validTx = new Transaction();
        validTx.setAmount(new BigDecimal("1500"));
        validTx.setDate(LocalDate.of(2025, 8, 15));

        Transaction invalidTx = new Transaction();
        invalidTx.setAmount(new BigDecimal("2000"));
        invalidTx.setDate(LocalDate.of(2024, 8, 15));

        when(transactionRepo.findByAccount_AccountIDAndType(accountId, TransactionType.INCOME))
                .thenReturn(List.of(validTx, invalidTx));

        Map<String, BigDecimal> result = transactionService.getMonthlyIncomes(accountId, year);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("1500"), result.get("AUGUST"));
        verify(transactionRepo).findByAccount_AccountIDAndType(accountId, TransactionType.INCOME);
    }

    @Test
    void testGetTransactionsByAccountValidAccountId() {
        int accountId = 1;

        Transaction t1 = new Transaction();
        t1.setTransactionID(1);
        t1.setAmount(new BigDecimal("100"));
        t1.setType(TransactionType.EXPENSE);

        Transaction t2 = new Transaction();
        t2.setTransactionID(2);
        t2.setAmount(new BigDecimal("200"));
        t2.setType(TransactionType.INCOME);

        List<Transaction> expectedTransactions = List.of(t1, t2);

        when(transactionRepo.findByAccountAccountID(accountId))
                .thenReturn(expectedTransactions);

        List<Transaction> result = transactionService.getTransactionsByAccount(accountId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedTransactions, result);
        verify(transactionRepo).findByAccountAccountID(accountId);
    }

    @Test
    void testGetTransactionsByAccountEmptyResult() {
        int accountId = 999;

        when(transactionRepo.findByAccountAccountID(accountId))
                .thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getTransactionsByAccount(accountId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(transactionRepo).findByAccountAccountID(accountId);
    }

    @Test
    void testGetTransactionsByAccountNullAccountId() {

        when(transactionRepo.findByAccountAccountID(null))
                .thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getTransactionsByAccount(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(transactionRepo).findByAccountAccountID(null);
    }

}