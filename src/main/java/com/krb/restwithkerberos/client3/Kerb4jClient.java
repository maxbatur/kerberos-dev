package com.krb.restwithkerberos.client3;

import com.kerb4j.client.SpnegoHttpURLConnection;
import com.sun.security.auth.module.Krb5LoginModule;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Kerb4jClient {

    private static final String JAAS_CONFIG_NAME = "UseKeytabForLogin";
    private static final String PRINCIPAL_NAME = "client/localhost@TEST.REALM";
    private static final String KEYTAB_FILE_NAME = "service-user.keytab";
    private static final String REST_ENDPOINT = "http://localhost:8080/foos";


    public static void main(final String[] args) throws Exception {
        System.setProperty("java.security.krb5.conf",
                Paths.get("krb5.conf").normalize().toAbsolutePath().toString());
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("java.security.debug", "true");
        System.setProperty("sun.security.jgss.debug", "true");
        System.setProperty("sun.security.spnego.debug", "true");
        System.setProperty("com.sun.security.auth.module.debug", "true");
        System.setProperty("http.use.global.creds", "false"); //restrict cached tickets
/* Replaced file with programmatic approach - See login.conf file for example
        System.setProperty("java.security.auth.login.config",
                Paths.get("login.conf").normalize().toAbsolutePath().toString());
*/
        Configuration jaasConfig = createJaasConfig();
        Configuration.setConfiguration(jaasConfig);


        SpnegoHttpURLConnection spnego = null;

        try {
            spnego = new SpnegoHttpURLConnection(JAAS_CONFIG_NAME);
            spnego.connect(new URL(REST_ENDPOINT));

            System.out.println("HTTP Status Code: "
                    + spnego.getResponseCode());

            System.out.println("HTTP Status Message: "
                    + spnego.getResponseMessage());

            try {
                System.out.println("HTTP Body: ");
                BufferedReader in = new BufferedReader(new InputStreamReader(spnego.getInputStream()));
                String line = null;
                while((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println(e);
            }

        } finally {
            if (null != spnego) {
                spnego.disconnect();
            }
        }
    }

    private static Configuration createJaasConfig() {

        Map<String, Object> options = new HashMap<>();
        options.put("debug", "true");
        options.put("useTicketCache","false");
        options.put("useKeyTab","true");
        options.put("principal", PRINCIPAL_NAME);
        options.put("keyTab",KEYTAB_FILE_NAME);

        AppConfigurationEntry[] entries = {
                new AppConfigurationEntry(
                        Krb5LoginModule.class.getCanonicalName(),
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        options)
        };

        return new Configuration() {
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                if(JAAS_CONFIG_NAME.equals(name)) {
                    return entries;
                }
                return null;
            }
        };

    }


}
