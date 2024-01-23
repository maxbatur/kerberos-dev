package com.krb.restwithkerberos.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.kerberos.client.KerberosRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
class KerberosConfig {



    @Value("${app.user-principal}")
    private String principal;

    @Value("${app.keytab-location}")
    private String keytabLocation;



    @Bean
    public RestTemplate restTemplate() {

        Map<String, Object> loginOptions = new HashMap();
        loginOptions.put("debug", "true");
        loginOptions.put("useTicketCache", "false");
        loginOptions.put("useKeyTab", "true");
        loginOptions.put("principal", principal);
        loginOptions.put("keyTab", keytabLocation);


        System.out.println("#######################################" + "\r\n" +
                keytabLocation.toString() + "\r\n" +
                principal.toString() + "\r\n" +
                "#######################################" + "\r\n");

        loginOptions.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });

        return new KerberosRestTemplate(keytabLocation, principal, loginOptions);
    }
}
