package com.example.controller;


import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@RestController
//@RequestMapping("/api")
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    // Registration Endpoint
    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account){
        if(account.getUsername() == null || account.getUsername().isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(account.getPassword() == null || account.getPassword().length() < 4){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (accountService.userNameExist(account.getUsername())){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Account savedAccount = accountService.register(account);
        return ResponseEntity.ok(savedAccount);
    }

    // login endpoint
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) throws AuthenticationException{
        var optionalAccount = accountService.login(account.getUsername(), account.getPassword());
        if(optionalAccount.isPresent()){
            return ResponseEntity.ok(optionalAccount.get());
        }
        return ResponseEntity.status(401).build();
    }


    // Create Message
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        Optional<Message> createdMessage = messageService.createMessage(message);
        return createdMessage.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    // Get All Messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    // Get Message by message ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        return message.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok().build());
    }

    // Delete Message by message id
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId){
        int rowsDeleted = messageService.deleteMessage(messageId);
        int rowStatus;  // check if one row been deleted
        if(rowsDeleted == 1) {
            rowStatus = rowsDeleted;
            return ResponseEntity.ok(rowStatus); // Respond with the number of rows updated
        }
        // else no rows deleted, still return status 200
        return ResponseEntity.ok(null);
    }

    // Update or Patch Message by message id
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updatedMessage(@PathVariable Integer messageId, @RequestBody Map<String, String> newMessageText){
        try {
            int updatedRows = messageService.updateMessage(messageId, newMessageText.get("messageText"));
            if(updatedRows > 0){
                return ResponseEntity.ok(updatedRows); // Return the number of rows updated
            }
            else {
                return ResponseEntity.badRequest().body("Message not found");
            }
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get Messages by User (account id)
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        return ResponseEntity.ok(messages); // Always returns 200 OK with the list of messages
    }

    //exception handler. other customs are loc in exception folder
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handledUnauthorized(AuthenticationException ex){
        return ex.getMessage();
    }
}
