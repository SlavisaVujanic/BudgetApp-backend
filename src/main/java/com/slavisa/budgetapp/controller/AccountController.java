package com.slavisa.budgetapp.controller;

import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountID}")
    public Optional<Account> getAccountByID(@PathVariable Integer accountID){
        return accountService.getAccountById(accountID);
    }

    @DeleteMapping("/delete/{accountID}")
    public void deleteAccountByID(@PathVariable Integer accountID){
        accountService.deleteAccount(accountID);
    }

    @PostMapping("/add")
    public  Account addAccount(@RequestBody @Valid Account account){
        return accountService.addAccount(account);
    }

    @PutMapping("/update/{accountID}")
    public Account updateAccount(@PathVariable Integer accountID, @RequestBody @Valid Account account){
        return accountService.updateAccount(accountID,account);
    }
}
