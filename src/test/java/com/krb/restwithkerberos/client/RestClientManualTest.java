package com.krb.restwithkerberos.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestClientManualTest {

    @Autowired
    private RestClient sampleClient;

    @Test
    public void givenKerberizedRestTemplate_whenServiceCall_thenSuccess() {
        assertEquals("{\"name\":\"FirstFoo\"," +
                                "\"message\":\"Hello\"," +
                                "\"principalName\":\"client/localhost@TEST.REALM\"" +
                                "}", sampleClient.getData());
    }

    @Test
    public void givenRestTemplate_whenServiceCall_thenFail() {
        sampleClient.setRestTemplate(new RestTemplate());
        assertThrows(RestClientException.class, sampleClient::getData);
    }
}
