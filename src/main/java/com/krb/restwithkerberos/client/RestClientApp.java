package com.krb.restwithkerberos.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;

@SpringBootApplication
class RestClientApp {

    static {
        System.setProperty("java.security.krb5.conf",
                Paths.get("krb5.conf").normalize().toAbsolutePath().toString());
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("java.security.debug", "true");
        // disable usage of local kerberos ticket cache
        System.setProperty("http.use.global.creds", "false");
//        System.setProperty("javax.security.auth.useSubjectCredsOnly","false"); //if login fails search gss-jaas.conf
//        System.setProperty("java.security.auth.login.config",
//                Paths.get("login.conf").normalize().toAbsolutePath().toString());
    }

    public static void main(String[] args) {
        SpringApplication.run(RestClientApp.class, args);
    }
}