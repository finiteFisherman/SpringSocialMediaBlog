package com.example.service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountRepository accountRepository;

    // create message
    public Optional<Message> createMessage(Message message) {
        Optional<Account> account = accountRepository.findById(message.getPostedBy());
        //length of new message text
        int newMessageLength = message.getMessageText().length();
        if (account.isPresent() && message.getMessageText() != null && !message.getMessageText().isBlank()
            && newMessageLength < 255) {
            return Optional.of(messageRepository.save(message));
        }
        return Optional.empty();
    }

    // get all messages
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // get message by message id
    public Optional<Message> getMessageById(Integer messageId){
        return messageRepository.findById(messageId);
    }

    //delete message by message id
    public int deleteMessage(Integer messageId) {
        //messageId  found
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return 1; //one row was deleted
        }
        return 0; //no row was deleted (message not found)
    }

    // update message by message id
    public int updateMessage(Integer messageId, String newMessageText) throws ResourceNotFoundException {
        if(newMessageText != null && !newMessageText.isBlank() && newMessageText.length() < 255 && !newMessageText.isEmpty()){
            //throw new IllegalArgumentException("Invalid message text");
            Optional<Message> optionalMessage = Optional.ofNullable(messageRepository.findById(messageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Message was not found. Please try again.")));;
            if(optionalMessage.isPresent()){
                Message message = optionalMessage.get();
                message.setMessageText(newMessageText);
                messageRepository.save(message);
                return 1; // rows affected
            }
            else{
                return 0;
            }
        }
        return 0;
    }

    // get all messages by user (pk account: account id, fk message: postedBy)
    public List<Message> getMessagesByAccountId(Integer accountId){
        return messageRepository.findByPostedBy(accountId);
    }

}