package com.slavisa.budgetapp.controller;

import com.slavisa.budgetapp.model.Transaction;
import com.slavisa.budgetapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getAllTransactions(){
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{transactionID}")
    public Optional<Transaction> getTransactionByID(@PathVariable Integer transactionID){
        return transactionService.getTransactionById(transactionID);
    }

    @DeleteMapping("/delete/{transactionID}")
    public void deleteTransactionByID(@PathVariable Integer transactionID){
        transactionService.deleteTransaction(transactionID);
    }

    @PostMapping("/add")
    public Transaction addTransaction(@RequestBody @Valid Transaction transaction){
       return  transactionService.addTransaction(transaction);
    }

    @PutMapping(value="/update/{transactionID}", consumes = "application/json")
    public Transaction updateTransaction(@PathVariable Integer transactionID, @RequestBody @Valid Transaction transaction){
        return transactionService.updateTransaction(transactionID,transaction);
    }

    @GetMapping("/totalIncome/{accountID}")
    public BigDecimal getTotalIncome(@PathVariable Integer accountID){
        return transactionService.getTotalIncome(accountID);
    }

    @GetMapping("/totalExpense/{accountID}")
    public BigDecimal getTotalExpense(@PathVariable Integer accountID){
        return transactionService.getTotalExpense(accountID);
    }

    @GetMapping("/balance/{accountID}")
    public BigDecimal getBalance(@PathVariable Integer accountID){
        return transactionService.getBalance(accountID);
    }

    @GetMapping("/byMonth/{accountID}/{year}/{month}")
    public List<Transaction> getTransactionsByMonth(@PathVariable Integer accountID, @PathVariable int year,
            @PathVariable int month) {
        return transactionService.getTransactionsByMonth(accountID, month, year);
    }

    @GetMapping("/expenseByCategory/{accountID}")
    public Map<String, BigDecimal> getExpenseByCategory(@PathVariable Integer accountID){
        return transactionService.getExpenseByCategory(accountID);
    }

    @GetMapping("/incomeByCategory/{accountID}")
    public Map<String, BigDecimal> getIncomeByCategory(@PathVariable Integer accountID){
        return transactionService.getIncomeByCategory(accountID);
    }

    @GetMapping("/betweenDates/{accountID}")
    public List<Transaction> getTransactionsBetweenDates(@PathVariable Integer accountID,
            @RequestParam("start") String startDate, @RequestParam("end") String endDate) {
        return transactionService.getTransactionsBetweenDates(accountID,
                LocalDate.parse(startDate),LocalDate.parse(endDate));
    }

    @GetMapping("/totalIncomeBetweenDates/{accountID}")
    public BigDecimal getTotalIncomeBetweenDates(@PathVariable Integer accountID,
            @RequestParam("start") String startDate, @RequestParam("end") String endDate) {
        return transactionService.getTotalIncomeBetweenDates(accountID,
                LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @GetMapping("/totalExpenseBetweenDates/{accountID}")
    public BigDecimal getTotalExpenseBetweenDates(@PathVariable Integer accountID,
            @RequestParam("start") String startDate, @RequestParam("end") String endDate) {
        return transactionService.getTotalExpenseBetweenDates(accountID,
                LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @GetMapping("/balanceBetweenDates/{accountID}")
    public BigDecimal getBalanceBetweenDates(@PathVariable Integer accountID,
            @RequestParam("start") String startDate, @RequestParam("end") String endDate) {
        return transactionService.getBalanceBetweenDates(accountID,
                LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @GetMapping("/averageExpenseByMonth/{accountID}/{year}/{month}")
    public BigDecimal getAverageExpenseByMonth(@PathVariable Integer accountID,
            @PathVariable int year, @PathVariable int month) {
        return transactionService.getAverageExpenseByMonth(accountID, month, year);
    }

    @GetMapping("/averageIncomeByMonth/{accountID}/{year}/{month}")
    public BigDecimal getAverageIncomeByMonth(@PathVariable Integer accountID,
            @PathVariable int year, @PathVariable int month) {
        return transactionService.getAverageIncomeByMonth(accountID, month, year);
    }

    @GetMapping("/byCategoryAndDate/{accountID}/{categoryID}")
    public List<Transaction> getTransactionsByCategoryAndDate(@PathVariable Integer accountID,@PathVariable Integer categoryID,
            @RequestParam("start") String startDate, @RequestParam("end") String endDate) {
        return transactionService.getTransactionsByCategoryAndDate(accountID,categoryID,
                LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @DeleteMapping("/deleteAll/{accountID}")
    public void deleteAllTransactions(@PathVariable Integer accountID){
        transactionService.deleteAllTransactions(accountID);
    }

    @GetMapping("/monthlyExpenses/{accountID}/{year}")
    public Map<String, BigDecimal> getMonthlyExpenses(@PathVariable Integer accountID,@PathVariable int year) {
        return transactionService.getMonthlyExpenses(accountID, year);
    }

    @GetMapping("/monthlyIncomes/{accountID}/{year}")
    public Map<String, BigDecimal> getMonthlyIncomes(@PathVariable Integer accountID,@PathVariable int year) {
        return transactionService.getMonthlyIncomes(accountID, year);
    }

    @GetMapping("/account/{accountID}")
    public List<Transaction> getByAccount(@PathVariable Integer accountID) {
        return transactionService.getTransactionsByAccount(accountID);
    }
}
