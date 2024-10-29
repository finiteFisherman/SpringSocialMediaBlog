package com.example.service;

import com.example.entity.Message;
import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
public class AccountService{
    private AccountRepository accountRepository;
    //private MessageService messageService;

    //inject
    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
        //this.messageService = messageService;
    }

    // register new account
    public Account register(Account newAccount){
        return accountRepository.save(newAccount);
    }

    //login user
    public Optional<Account> login(String username, String password) throws AuthenticationException{
        return accountRepository.findByUsername(username)
                .filter(account -> account.getPassword().equals(password));
    }

    // check if user exists in db
    public boolean userNameExist(String username){
        return accountRepository.findByUsername(username).isPresent();
    }
}
