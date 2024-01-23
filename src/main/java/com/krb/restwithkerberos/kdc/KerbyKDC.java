package com.krb.restwithkerberos.kdc;

import static java.util.Arrays.asList;
import static org.apache.kerby.kerberos.kerb.server.KdcConfigKey.ENCRYPTION_TYPES;
import static org.apache.kerby.kerberos.kerb.server.KdcConfigKey.PREAUTH_REQUIRED;

import java.io.File;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.SimpleKdcServer;

public class KerbyKDC {
    public static void main(String[] args) throws KrbException {
        SimpleKdcServer kdc = new SimpleKdcServer();
        kdc.enableDebug();
        kdc.setKdcHost("localhost");
        kdc.setKdcRealm("TEST.REALM");
        kdc.setKdcPort(10088);
        kdc.setAllowUdp(false);
        kdc.getKdcConfig().setBoolean(PREAUTH_REQUIRED, true);
        kdc.getKdcConfig().setString(ENCRYPTION_TYPES, "aes256-cts-hmac-sha1-96 aes128-cts-hmac-sha1-96 rc4-hmac");
        // SimpleKdcServer init also creates krb5.conf file and initializes Kadmin API.
        kdc.init();

        kdc.createPrincipal("alice", "secret");
        kdc.createPrincipal("client/localhost", "secret");
        kdc.createPrincipal("HTTP/localhost", "secret");

        // export service principal's keytab
        File keytabFile = new File("service-user.keytab");

        if (keytabFile.exists()) {
            keytabFile.delete();
        }

        kdc.getKadmin().exportKeytab(keytabFile, asList("HTTP/localhost@TEST.REALM",
                                                        "client/localhost@TEST.REALM",
                                                        "alice@TEST.REALM"));

        kdc.start();
        System.out.println("Kerberos server has started.");
    }
}
