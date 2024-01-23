package com.krb.restwithkerberos.server.controller;

import com.krb.restwithkerberos.server.exception.CustomException1;
import com.krb.restwithkerberos.server.exception.CustomException2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/foos")
public class FooController {
    private static final Logger logger = LoggerFactory.getLogger(FooController.class);


    @GetMapping
    public Foo getFoos() {
        String userName = "";
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails instanceof UserDetails){
            userName = ((UserDetails)userDetails).getUsername();
        } else {
            userName = userDetails.toString();
        }

        final Foo foo = new Foo("FirstFoo", "Hello", userName );

        return foo;
    }


    @ExceptionHandler({ CustomException1.class, CustomException2.class })
    public void handleException(final Exception ex) {
        final String error = "Application specific error handling";
        logger.error(error, ex);
    }
}