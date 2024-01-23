package com.krb.restwithkerberos.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//(exclude = { SecurityAutoConfiguration.class })
public class RestWithKerberosServer {

    public static void main(String[] args) {
        SpringApplication.run(RestWithKerberosServer.class, args);
    }

}
