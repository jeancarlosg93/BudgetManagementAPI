package ca.vanier.budgetmanagement.controllers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

@Getter
public abstract class BaseController {

    //this class is used to get the message from the message source
    //and return the response entity
    //every controller class should extend this class

    @Autowired
    protected MessageSource messageSource;

    protected String getMessage(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }

    protected ResponseEntity<String> success(String key, Locale locale) {
        return ResponseEntity.ok(getMessage(key, null, locale));
    }

    protected ResponseEntity<String> error(String key, Object[] args, Locale locale) {
        return ResponseEntity.badRequest()
                .body(getMessage(key, args, locale));
    }
}