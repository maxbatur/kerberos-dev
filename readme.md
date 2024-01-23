# Kerberos Test Environment
This is setup-free local dev environment for playing with kerberos and java. Clone and go. 

### Consists of 3 main modules

* Kerby KDC Server
* Spring Boot Web Server with dummy /foos REST service protected by Kerberos. Make sure 8080 port is free or change it in application.properties
* Clients:
  * **client**  - *Spring Boot test client with KerberosRestTemplate*
  * **client2** - *Rest client based on [Sourceforge spnego](http://spnego.sourceforge.net/)*
  * **client3** - *Kerb4j is a modern version of sourceforge spnego. Same functionality as SourceforgeClient [Kerb4j](https://github.com/bedrin/kerb4j)*

### Usage
* Run KerbyKDC.main()
* Run RestWithKerberosServer.main() Access running service via http://localhost:8080/foos
* Test by running spring test client - com.krb.restwithkerberos.client.RestClientManualTest

Upon successful execution you'll see output from Spring REST service:
  {"name":"FirstFoo","message":"Hello","principalName":"client/localhost@TEST.REALM"}
You can also test by client2 and client3 if everything goes fine with client 1. 

Use **curl** to test kerberos protected service. Password for client/localhost@TEST.REALM is "secret" by default, can be set in KerbyKDC:

    $ kinit client/localhost@TEST.REALM
    Password for client/localhost@TEST.REALM:

    $ klist
    Ticket cache: FILE:/tmp/krb5cc_1000
    Default principal: client/localhost@TEST.REALM
    
    Valid starting       Expires              Service principal
    30.06.2022 23:27:14  01.07.2022 23:27:14  krbtgt/TEST.REALM@TEST.REALM
    renew until 01.07.2022 23:27:14

    $ curl --negotiate -u : -v http://localhost:8080/foos
    *   Trying 127.0.0.1:8080... 
    * Connected to localhost (127.0.0.1) port 8080 (#0)
    * Server auth using Negotiate with user ''
    > GET /foos HTTP/1.1
    > Host: localhost:8080
    > Authorization: Negotiate YIICTQYGKwYBBQUCoIICQTCCAj2gDTALBgkqhkiG9xIBAgKiggIqBIICJmCCAiIGCSqGSIb3EgECAgEAboICETCCAg2gAwIBBaEDAgEOogcDBQAgAAAAo4IBG2GCARcwggEToAMCAQWhDBsKVEVTVC5SRUFMTaIcMBqgAwIBA6ETMBEbBEhUVFAbCWxvY2FsaG9zdKOB3zCB3KADAgESoQMCAQGigc8EgcwWxk0icVPH4HgiAv709sfSeHC8eEEUpTjv5mqTu3Nwc5hrkMp96HMRjuWXoISbBiA6SaCBNZIjQPVP8uJFav9HNSJ5Nbbe8KhB8usWvJn6ipzxExmvtQdN5O8EQ7R+SDvdMyYqX7E3BC0qISOnK5GlYsAjF5YBE+8DpvOgOHVIGL5mHIZxW3yAgzRe4oYZHT52OqdyzUV8Z0Uzi0TMGdwdtwaXyeGJAwvaBiJG3gmPPbY11KKQsekVHYXsRhOo8zCqmTJ66J36E3G0pBykgdgwgdWgAwIBEqKBzQSBylG/qQxjnFgXM1YTGS26Wx4zxxG69+UNZdv0wrgRyE5pDnGyJdAnnU69baVFR5ZWnkWvy9huOPIvpbz0CK+RQAwY+LzZayW+9l5ZyZNh5sHEHXNRrsL8QgdAMYgF/P9FQPa44VxNo7493rhS8g6gy6+dbLNBYktws/F98Roj4faZ9wyGz2qha262pCHGR83fO4kXAhuegW1qDKrE2VDyzlZSO1dimikO89QfD+jgeG3r8oMDCx7V2mzxx33bvg+OJTGphs+yRtk/nNQ=
    > User-Agent: curl/7.81.0
    > Accept: */*
    >
    * Mark bundle as not supporting multiuse
    < HTTP/1.1 200
    < Set-Cookie: JSESSIONID=96C4569082B0BEC11D5040F1C411E004; Path=/; HttpOnly
    < X-Content-Type-Options: nosniff
    < X-XSS-Protection: 1; mode=block
    < Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    < Pragma: no-cache
    < Expires: 0
    < X-Frame-Options: DENY
    < Content-Type: application/json
    < Transfer-Encoding: chunked
    < Date: Thu, 30 Jun 2022 20:30:57 GMT
    <
    * Connection #0 to host localhost left intact
    {"name":"FirstFoo","message":"Hello","principalName":"client/localhost@TEST.REALM"}


Google Chrome can be used to test on localhost  `  $ google-chrome --auth-server-whitelist="localhost"`


### KDC settings  
See KerbyKDC.java. After execution it will generate kerb5.conf and service-user.keytab

`kerb5.conf`

    [libdefaults]
      kdc_realm = TEST.REALM
      default_realm = TEST.REALM
      udp_preference_limit = 1
      kdc_tcp_port = 10088
      #_KDC_UDP_PORT_
    [realms]
      TEST.REALM = {
      kdc = localhost:10088
    }

`service-user.keytab` contains service and user credentials.

    $ klist -ket  ./service-user.keytab     
    Keytab name: FILE:./service-user.keytab
    KVNO Timestamp           Principal
    ---- ------------------- ------------------------------------------------------
    1 30.06.2022 19:21:26 HTTP/localhost@TEST.REALM (aes128-cts-hmac-sha1-96)
    1 30.06.2022 19:21:26 HTTP/localhost@TEST.REALM (DEPRECATED:arcfour-hmac)
    1 30.06.2022 19:21:26 HTTP/localhost@TEST.REALM (aes256-cts-hmac-sha1-96)
    1 30.06.2022 19:21:26 alice@TEST.REALM (aes128-cts-hmac-sha1-96)
    1 30.06.2022 19:21:26 alice@TEST.REALM (DEPRECATED:arcfour-hmac)
    1 30.06.2022 19:21:26 alice@TEST.REALM (aes256-cts-hmac-sha1-96)
    1 30.06.2022 19:21:26 client/localhost@TEST.REALM (aes128-cts-hmac-sha1-96)
    1 30.06.2022 19:21:26 client/localhost@TEST.REALM (DEPRECATED:arcfour-hmac)
    1 30.06.2022 19:21:26 client/localhost@TEST.REALM (aes256-cts-hmac-sha1-96)

### Server
Spring Boot settings - application.properties file. 
Uncomment security  related settings to disable kerberos endpoint protection.
You can set port here if 8080 is already used

### Java
    $ java -version
    java version "1.8.0_331"
    Java(TM) SE Runtime Environment (build 1.8.0_331-b09)
    Java HotSpot(TM) 64-Bit Server VM (build 25.331-b09, mixed mode)

### OS
Tested on Ubuntu

    $ lsb_release -a
    No LSB modules are available.
    Distributor ID: Ubuntu
    Description:    Ubuntu 22.04 LTS
    Release:        22.04
    Codename:       jammy

### Typical output of client2
    /opt/java/jdk1.8.0_331/bin/java -javaagent:/home/max/idea-IU-221.5921.22/lib/idea_rt.jar=33327:/home/max/idea-IU-221.5921.22/bin -Dfile.encoding=UTF-8 -classpath /opt/java/jdk1.8.0_331/jre/lib/charsets.jar:/opt/java/jdk1.8.0_331/jre/lib/deploy.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/cldrdata.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/dnsns.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/jaccess.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/jfxrt.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/localedata.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/nashorn.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/sunec.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/sunjce_provider.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/sunpkcs11.jar:/opt/java/jdk1.8.0_331/jre/lib/ext/zipfs.jar:/opt/java/jdk1.8.0_331/jre/lib/javaws.jar:/opt/java/jdk1.8.0_331/jre/lib/jce.jar:/opt/java/jdk1.8.0_331/jre/lib/jfr.jar:/opt/java/jdk1.8.0_331/jre/lib/jfxswt.jar:/opt/java/jdk1.8.0_331/jre/lib/jsse.jar:/opt/java/jdk1.8.0_331/jre/lib/management-agent.jar:/opt/java/jdk1.8.0_331/jre/lib/plugin.jar:/opt/java/jdk1.8.0_331/jre/lib/resources.jar:/opt/java/jdk1.8.0_331/jre/lib/rt.jar:/home/max/Documents/RestWithKerberos/target/classes:/home/max/Documents/RestWithKerberos/src/main/resources/lib/spnego-r9.jar:/home/max/.m2/repository/org/apache/kerby/kerb-simplekdc/2.0.1/kerb-simplekdc-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerb-client/2.0.1/kerb-client-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerby-config/2.0.1/kerby-config-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerb-core/2.0.1/kerb-core-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerby-pkix/2.0.1/kerby-pkix-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerby-asn1/2.0.1/kerby-asn1-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerby-util/2.0.1/kerby-util-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerb-common/2.0.1/kerb-common-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerb-crypto/2.0.1/kerb-crypto-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerb-util/2.0.1/kerb-util-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/token-provider/2.0.1/token-provider-2.0.1.jar:/home/max/.m2/repository/com/nimbusds/nimbus-jose-jwt/8.2.1/nimbus-jose-jwt-8.2.1.jar:/home/max/.m2/repository/com/github/stephenc/jcip/jcip-annotations/1.0-1/jcip-annotations-1.0-1.jar:/home/max/.m2/repository/org/apache/kerby/kerb-admin/2.0.1/kerb-admin-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerb-server/2.0.1/kerb-server-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerb-identity/2.0.1/kerb-identity-2.0.1.jar:/home/max/.m2/repository/org/apache/kerby/kerby-xdr/2.0.1/kerby-xdr-2.0.1.jar:/home/max/.m2/repository/com/kerb4j/kerb4j-client/0.1.2/kerb4j-client-0.1.2.jar:/home/max/.m2/repository/com/kerb4j/kerb4j-common/0.1.2/kerb4j-common-0.1.2.jar:/home/max/.m2/repository/com/kerb4j/kerb4j-base64-java7/0.1.2/kerb4j-base64-java7-0.1.2.jar:/home/max/.m2/repository/com/kerb4j/kerb4j-base64-common/0.1.2/kerb4j-base64-common-0.1.2.jar:/home/max/.m2/repository/com/kerb4j/kerb4j-base64-java8/0.1.2/kerb4j-base64-java8-0.1.2.jar:/home/max/.m2/repository/org/springframework/boot/spring-boot-starter-security/2.7.1/spring-boot-starter-security-2.7.1.jar:/home/max/.m2/repository/org/springframework/boot/spring-boot-starter/2.7.1/spring-boot-starter-2.7.1.jar:/home/max/.m2/repository/org/springframework/boot/spring-boot/2.7.1/spring-boot-2.7.1.jar:/home/max/.m2/repository/org/springframework/boot/spring-boot-autoconfigure/2.7.1/spring-boot-autoconfigure-2.7.1.jar:/home/max/.m2/repository/org/springframework/boot/spring-boot-starter-logging/2.7.1/spring-boot-starter-logging-2.7.1.jar:/home/max/.m2/repository/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar:/home/max/.m2/repository/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar:/home/max/.m2/repository/org/apache/logging/log4j/log4j-to-slf4j/2.17.2/log4j-to-slf4j-2.17.2.jar:/home/max/.m2/repository/org/apache/logging/log4j/log4j-api/2.17.2/log4j-api-2.17.2.jar:/home/max/.m2/repository/org/slf4j/jul-to-slf4j/1.7.36/jul-to-slf4j-1.7.36.jar:/home/max/.m2/repository/jakarta/annotation/jakarta.annotation-api/1.3.5/jakarta.annotation-api-1.3.5.jar:/home/max/.m2/repository/org/yaml/snakeyaml/1.30/snakeyaml-1.30.jar:/home/max/.m2/repository/org/springframework/spring-aop/5.3.21/spring-aop-5.3.21.jar:/home/max/.m2/repository/org/springframework/spring-beans/5.3.21/spring-beans-5.3.21.jar:/home/max/.m2/repository/org/springframework/security/spring-security-config/5.7.2/spring-security-config-5.7.2.jar:/home/max/.m2/repository/org/springframework/spring-context/5.3.21/spring-context-5.3.21.jar:/home/max/.m2/repository/org/springframework/security/spring-security-web/5.7.2/spring-security-web-5.7.2.jar:/home/max/.m2/repository/org/springframework/spring-expression/5.3.21/spring-expression-5.3.21.jar:/home/max/.m2/repository/org/springframework/boot/spring-boot-starter-web/2.7.1/spring-boot-starter-web-2.7.1.jar:/home/max/.m2/repository/org/springframework/boot/spring-boot-starter-json/2.7.1/spring-boot-starter-json-2.7.1.jar:/home/max/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.13.3/jackson-databind-2.13.3.jar:/home/max/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.13.3/jackson-annotations-2.13.3.jar:/home/max/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.13.3/jackson-core-2.13.3.jar:/home/max/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.13.3/jackson-datatype-jdk8-2.13.3.jar:/home/max/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.13.3/jackson-datatype-jsr310-2.13.3.jar:/home/max/.m2/repository/com/fasterxml/jackson/module/jackson-module-parameter-names/2.13.3/jackson-module-parameter-names-2.13.3.jar:/home/max/.m2/repository/org/springframework/boot/spring-boot-starter-tomcat/2.7.1/spring-boot-starter-tomcat-2.7.1.jar:/home/max/.m2/repository/org/apache/tomcat/embed/tomcat-embed-core/9.0.64/tomcat-embed-core-9.0.64.jar:/home/max/.m2/repository/org/apache/tomcat/embed/tomcat-embed-el/9.0.64/tomcat-embed-el-9.0.64.jar:/home/max/.m2/repository/org/apache/tomcat/embed/tomcat-embed-websocket/9.0.64/tomcat-embed-websocket-9.0.64.jar:/home/max/.m2/repository/org/springframework/spring-web/5.3.21/spring-web-5.3.21.jar:/home/max/.m2/repository/org/springframework/spring-webmvc/5.3.21/spring-webmvc-5.3.21.jar:/home/max/.m2/repository/net/minidev/json-smart/2.4.8/json-smart-2.4.8.jar:/home/max/.m2/repository/net/minidev/accessors-smart/2.4.8/accessors-smart-2.4.8.jar:/home/max/.m2/repository/org/ow2/asm/asm/9.1/asm-9.1.jar:/home/max/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar:/home/max/.m2/repository/jakarta/xml/bind/jakarta.xml.bind-api/2.3.3/jakarta.xml.bind-api-2.3.3.jar:/home/max/.m2/repository/jakarta/activation/jakarta.activation-api/1.2.2/jakarta.activation-api-1.2.2.jar:/home/max/.m2/repository/org/hamcrest/hamcrest/2.2/hamcrest-2.2.jar:/home/max/.m2/repository/org/springframework/spring-core/5.3.21/spring-core-5.3.21.jar:/home/max/.m2/repository/org/springframework/spring-jcl/5.3.21/spring-jcl-5.3.21.jar:/home/max/.m2/repository/org/springframework/spring-test/5.3.21/spring-test-5.3.21.jar:/home/max/.m2/repository/org/springframework/security/spring-security-test/5.7.2/spring-security-test-5.7.2.jar:/home/max/.m2/repository/org/springframework/security/spring-security-core/5.7.2/spring-security-core-5.7.2.jar:/home/max/.m2/repository/org/springframework/security/spring-security-crypto/5.7.2/spring-security-crypto-5.7.2.jar:/home/max/.m2/repository/org/springframework/security/kerberos/spring-security-kerberos-web/1.0.1.RELEASE/spring-security-kerberos-web-1.0.1.RELEASE.jar:/home/max/.m2/repository/org/springframework/security/kerberos/spring-security-kerberos-core/1.0.1.RELEASE/spring-security-kerberos-core-1.0.1.RELEASE.jar:/home/max/.m2/repository/org/springframework/security/kerberos/spring-security-kerberos-client/1.0.1.RELEASE/spring-security-kerberos-client-1.0.1.RELEASE.jar:/home/max/.m2/repository/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar:/home/max/.m2/repository/org/apache/httpcomponents/httpcore/4.4.15/httpcore-4.4.15.jar:/home/max/.m2/repository/commons-codec/commons-codec/1.15/commons-codec-1.15.jar:/home/max/.m2/repository/org/springframework/security/kerberos/spring-security-kerberos-test/1.0.1.RELEASE/spring-security-kerberos-test-1.0.1.RELEASE.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-core-api/2.0.0-M15/apacheds-core-api-2.0.0-M15.jar:/home/max/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-core-constants/2.0.0-M15/apacheds-core-constants-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-i18n/2.0.0-M15/apacheds-i18n-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/api/api-i18n/1.0.0-M20/api-i18n-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-asn1-api/1.0.0-M20/api-asn1-api-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-client-api/1.0.0-M20/api-ldap-client-api-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-codec-core/1.0.0-M20/api-ldap-codec-core-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-extras-aci/1.0.0-M20/api-ldap-extras-aci-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-extras-util/1.0.0-M20/api-ldap-extras-util-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-model/1.0.0-M20/api-ldap-model-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-util/1.0.0-M20/api-util-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/mina/mina-core/2.0.7/mina-core-2.0.7.jar:/home/max/.m2/repository/net/sf/ehcache/ehcache-core/2.4.4/ehcache-core-2.4.4.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-protocol-shared/2.0.0-M15/apacheds-protocol-shared-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-kerberos-codec/2.0.0-M15/apacheds-kerberos-codec-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-mavibot-partition/2.0.0-M15/apacheds-mavibot-partition-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/mavibot/mavibot/1.0.0-M1/mavibot-1.0.0-M1.jar:/home/max/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-core-avl/2.0.0-M15/apacheds-core-avl-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-xdbm-partition/2.0.0-M15/apacheds-xdbm-partition-2.0.0-M15.jar:/home/max/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar:/home/max/.m2/repository/junit/junit/4.13.2/junit-4.13.2.jar:/home/max/.m2/repository/org/hamcrest/hamcrest-core/2.2/hamcrest-core-2.2.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptor-kerberos/2.0.0-M15/apacheds-interceptor-kerberos-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-core/2.0.0-M15/apacheds-core-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-admin/2.0.0-M15/apacheds-interceptors-admin-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-authn/2.0.0-M15/apacheds-interceptors-authn-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-authz/2.0.0-M15/apacheds-interceptors-authz-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-changelog/2.0.0-M15/apacheds-interceptors-changelog-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-collective/2.0.0-M15/apacheds-interceptors-collective-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-event/2.0.0-M15/apacheds-interceptors-event-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-exception/2.0.0-M15/apacheds-interceptors-exception-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-journal/2.0.0-M15/apacheds-interceptors-journal-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-normalization/2.0.0-M15/apacheds-interceptors-normalization-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-operational/2.0.0-M15/apacheds-interceptors-operational-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-referral/2.0.0-M15/apacheds-interceptors-referral-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-schema/2.0.0-M15/apacheds-interceptors-schema-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-subtree/2.0.0-M15/apacheds-interceptors-subtree-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-interceptors-trigger/2.0.0-M15/apacheds-interceptors-trigger-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-extras-trigger/1.0.0-M20/api-ldap-extras-trigger-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-protocol-ldap/2.0.0-M15/apacheds-protocol-ldap-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/api/api-asn1-ber/1.0.0-M20/api-asn1-ber-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-extras-codec-api/1.0.0-M20/api-ldap-extras-codec-api-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-extras-codec/1.0.0-M20/api-ldap-extras-codec-1.0.0-M20.jar:/home/max/.m2/repository/org/apache/directory/api/api-ldap-extras-sp/1.0.0-M20/api-ldap-extras-sp-1.0.0-M20.jar:/home/max/.m2/repository/bouncycastle/bcprov-jdk15/140/bcprov-jdk15-140.jar:/home/max/.m2/repository/org/apache/directory/api/api-all/1.0.0-M20/api-all-1.0.0-M20.jar:/home/max/.m2/repository/commons-pool/commons-pool/1.6/commons-pool-1.6.jar:/home/max/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:/home/max/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-protocol-kerberos/2.0.0-M15/apacheds-protocol-kerberos-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-core-shared/2.0.0-M15/apacheds-core-shared-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-ldif-partition/2.0.0-M15/apacheds-ldif-partition-2.0.0-M15.jar:/home/max/.m2/repository/org/apache/directory/server/apacheds-jdbm-partition/2.0.0-M15/apacheds-jdbm-partition-2.0.0-M15.jar com.krb.restwithkerberos.client2.SourceforgeClient
    Debug is  true storeKey false useTicketCache false useKeyTab true doNotPrompt false ticketCache is null isInitiator true KeyTab is service-user.keytab refreshKrb5Config is false principal is client/localhost@TEST.REALM tryFirstPass is false useFirstPass is false storePass is false clearPass is false
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): HTTP
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 60; type: 17
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): HTTP
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 60; type: 23
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): HTTP
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 76; type: 18
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): alice
    >>> KeyTab: load() entry length: 50; type: 17
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): alice
    >>> KeyTab: load() entry length: 50; type: 23
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): alice
    >>> KeyTab: load() entry length: 66; type: 18
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): client
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 62; type: 17
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): client
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 62; type: 23
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): client
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 78; type: 18
    Looking for keys for: client/localhost@TEST.REALM
    Java config name: /home/max/Documents/RestWithKerberos/krb5.conf
    Loaded from Java config
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    >>> KdcAccessibility: reset
    Looking for keys for: client/localhost@TEST.REALM
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    Using builtin default etypes for default_tkt_enctypes
    default etypes for default_tkt_enctypes: 18 17 16 23.
    >>> KrbAsReq creating message
    >>> KrbKdcReq send: kdc=localhost TCP:10088, timeout=30000, number of retries =3, #bytes=148
    >>> KDCCommunication: kdc=localhost TCP:10088, timeout=30000,Attempt =1, #bytes=148
    >>>DEBUG: TCPClient reading 166 bytes
    >>> KrbKdcReq send: #bytes read=166
    >>>Pre-Authentication Data:
    PA-DATA type = 19
    PA-ETYPE-INFO2 etype = 18, salt = null, s2kparams = null
    PA-ETYPE-INFO2 etype = 17, salt = null, s2kparams = null
    
    >>> KdcAccessibility: remove localhost:10088
    >>> KDCRep: init() encoding tag is 126 req type is 11
    >>>KRBError:
    sTime is Fri Jul 01 00:29:08 MSK 2022 1656624548000
    suSec is 100
    error code is 25
    error Message is Additional pre-authentication required
    sname is client/localhost@TEST.REALM
    eData provided.
    msgType is 30
    >>>Pre-Authentication Data:
    PA-DATA type = 19
    PA-ETYPE-INFO2 etype = 18, salt = null, s2kparams = null
    PA-ETYPE-INFO2 etype = 17, salt = null, s2kparams = null
    
    KRBError received: Additional pre-authentication required
    KrbAsReqBuilder: PREAUTH FAILED/REQ, re-send AS-REQ
    Using builtin default etypes for default_tkt_enctypes
    default etypes for default_tkt_enctypes: 18 17 16 23.
    Looking for keys for: client/localhost@TEST.REALM
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    Looking for keys for: client/localhost@TEST.REALM
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    Using builtin default etypes for default_tkt_enctypes
    default etypes for default_tkt_enctypes: 18 17 16 23.
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> KrbAsReq creating message
    >>> KrbKdcReq send: kdc=localhost TCP:10088, timeout=30000, number of retries =3, #bytes=237
    >>> KDCCommunication: kdc=localhost TCP:10088, timeout=30000,Attempt =1, #bytes=237
    >>>DEBUG: TCPClient reading 568 bytes
    >>> KrbKdcReq send: #bytes read=568
    >>> KdcAccessibility: remove localhost:10088
    Looking for keys for: client/localhost@TEST.REALM
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> KrbAsRep cons in KrbAsReq.getReply client/localhost
    principal is client/localhost@TEST.REALM
    Will use keytab
    Commit Succeeded
    
    Search Subject for SPNEGO INIT cred (<<DEF>>, sun.security.jgss.spnego.SpNegoCredElement)
    Search Subject for Kerberos V5 INIT cred (<<DEF>>, sun.security.jgss.krb5.Krb5InitCredential)
    Found ticket for client/localhost@TEST.REALM to go to krbtgt/TEST.REALM@TEST.REALM expiring on Sat Jul 02 00:29:09 MSK 2022
    Entered SpNego.initSecContext with state=STATE_NEW
    Entered Krb5Context.initSecContext with state=STATE_NEW
    Service ticket not found in the subject
    >>> Credentials serviceCredsSingle: same realm
    Using builtin default etypes for default_tgs_enctypes
    default etypes for default_tgs_enctypes: 18 17 16 23.
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> CksumType: sun.security.krb5.internal.crypto.HmacSha1Aes256CksumType
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> KrbKdcReq send: kdc=localhost TCP:10088, timeout=30000, number of retries =3, #bytes=592
    >>> KDCCommunication: kdc=localhost TCP:10088, timeout=30000,Attempt =1, #bytes=592
    >>>DEBUG: TCPClient reading 557 bytes
    >>> KrbKdcReq send: #bytes read=557
    >>> KdcAccessibility: remove localhost:10088
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> TGS credentials serviceCredsSingle:
    >>> DEBUG: ----Credentials----
    client: client/localhost@TEST.REALM
    server: HTTP/localhost@TEST.REALM
    ticket: sname: HTTP/localhost@TEST.REALM
    endTime: 1656710949000
    ----Credentials end----
    Subject is readOnly;Kerberos Service ticket not stored
    >>> KrbApReq: APOptions are 00100000 00000000 00000000 00000000
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    Krb5Context setting mySeqNumber to: 839264929
    Created InitSecContextToken:
    0000: 01 00 6E 82 01 FF 30 82   01 FB A0 03 02 01 05 A1  ..n...0.........
    0010: 03 02 01 0E A2 07 03 05   00 20 00 00 00 A3 82 01  ......... ......
    0020: 08 61 82 01 04 30 82 01   00 A0 03 02 01 05 A1 0C  .a...0..........
    0030: 1B 0A 54 45 53 54 2E 52   45 41 4C 4D A2 1C 30 1A  ..TEST.REALM..0.
    0040: A0 03 02 01 00 A1 13 30   11 1B 04 48 54 54 50 1B  .......0...HTTP.
    0050: 09 6C 6F 63 61 6C 68 6F   73 74 A3 81 CC 30 81 C9  .localhost...0..
    0060: A0 03 02 01 12 A1 03 02   01 01 A2 81 BC 04 81 B9  ................
    0070: 84 B5 9D D1 F3 CC 6F 02   B5 A5 1A 3B 7C B9 BA CE  ......o....;....
    0080: 75 E4 95 20 26 79 41 E2   79 6E EB 4F 4B FC 03 7B  u.. &yA.yn.OK...
    0090: 0B A7 65 F7 78 3A B6 C6   BA 83 26 35 3B AB C0 FE  ..e.x:....&5;...
    00A0: 0C 81 97 F9 87 D2 DE 70   0F 97 55 DA 27 0A 3F C5  .......p..U.'.?.
    00B0: E4 22 4A CE 35 5C C9 0E   B8 C4 F5 6A 27 45 CF 0C  ."J.5\.....j'E..
    00C0: 97 AB 4E D0 7F 11 2F 2C   F6 33 7A 71 3F 70 AB 5D  ..N.../,.3zq?p.]
    00D0: 25 25 20 53 04 A5 D7 47   C7 7F BE D8 C8 72 06 8C  %% S...G.....r..
    00E0: 57 C4 B0 64 99 3E BC 1C   8C EF E5 BE 58 CF EF 97  W..d.>......X...
    00F0: 3A 04 40 AF C4 C8 CE F5   A1 3A A2 05 08 D6 7F 80  :.@......:......
    0100: 57 80 59 86 F0 F9 B7 9B   73 39 D7 0F 26 55 23 0A  W.Y.....s9..&U#.
    0110: C9 FD 13 CA 74 6F 63 C7   55 62 B2 97 7B BA 63 1E  ....toc.Ub....c.
    0120: E2 3A D9 9E E7 56 F0 DC   B1 A4 81 D9 30 81 D6 A0  .:...V......0...
    0130: 03 02 01 12 A2 81 CE 04   81 CB 56 19 2A 2E 93 AA  ..........V.*...
    0140: 1B 61 29 8D 75 8D DF 67   4F 05 96 C9 69 C0 90 8D  .a).u..gO...i...
    0150: 96 4F 6F AF F4 5D 59 51   3A 6E 6E 27 03 42 3A 0A  .Oo..]YQ:nn'.B:.
    0160: 10 3F 73 7E C7 AD CA D0   E2 CD 52 78 32 C8 68 7D  .?s.......Rx2.h.
    0170: 82 EA D8 D4 D6 D7 FA 42   92 D6 04 08 3C DE 0F 0F  .......B....<...
    0180: CD A5 FE 83 62 1E 9B 92   6A 53 7C 67 0E 4D ED 71  ....b...jS.g.M.q
    0190: 31 C8 A0 DB F0 2B 00 72   1A 9F 67 56 17 C3 1B 25  1....+.r..gV...%
    01A0: D5 00 4B C9 03 48 4F AD   4A A5 7F 81 E0 65 1F 18  ..K..HO.J....e..
    01B0: 06 9F 8F 13 18 22 1E 8F   42 2F 77 AD 6D 5A 9C 1B  ....."..B/w.mZ..
    01C0: 1E CA FF 27 0E C1 5A FB   76 79 BC 31 25 2D 05 36  ...'..Z.vy.1%-.6
    01D0: E8 A5 BD 1C E0 E0 B4 E2   33 D7 26 83 3F F1 F9 26  ........3.&.?..&
    01E0: ED 1F DE 26 58 53 30 ED   19 11 44 F4 D7 FA 10 B9  ...&XS0...D.....
    01F0: D0 68 CF 42 7D 08 34 9A   1C 71 5B F0 78 36 34 90  .h.B..4..q[.x64.
    0200: F1 FA BC E2 DC                                     .....
    
    SpNegoContext.initSecContext: sending token of type = SPNEGO NegTokenInit
    SNegoContext.initSecContext: sending token = a0 82 02 35 30 82 02 31 a0 0d 30 0b 06 09 2a 86 48 86 f7 12 01 02 02 a1 04 03 02 01 76 a2 82 02 18 04 82 02 14 60 82 02 10 06 09 2a 86 48 86 f7 12 01 02 02 01 00 6e 82 01 ff 30 82 01 fb a0 03 02 01 05 a1 03 02 01 0e a2 07 03 05 00 20 00 00 00 a3 82 01 08 61 82 01 04 30 82 01 00 a0 03 02 01 05 a1 0c 1b 0a 54 45 53 54 2e 52 45 41 4c 4d a2 1c 30 1a a0 03 02 01 00 a1 13 30 11 1b 04 48 54 54 50 1b 09 6c 6f 63 61 6c 68 6f 73 74 a3 81 cc 30 81 c9 a0 03 02 01 12 a1 03 02 01 01 a2 81 bc 04 81 b9 84 b5 9d d1 f3 cc 6f 02 b5 a5 1a 3b 7c b9 ba ce 75 e4 95 20 26 79 41 e2 79 6e eb 4f 4b fc 03 7b 0b a7 65 f7 78 3a b6 c6 ba 83 26 35 3b ab c0 fe 0c 81 97 f9 87 d2 de 70 0f 97 55 da 27 0a 3f c5 e4 22 4a ce 35 5c c9 0e b8 c4 f5 6a 27 45 cf 0c 97 ab 4e d0 7f 11 2f 2c f6 33 7a 71 3f 70 ab 5d 25 25 20 53 04 a5 d7 47 c7 7f be d8 c8 72 06 8c 57 c4 b0 64 99 3e bc 1c 8c ef e5 be 58 cf ef 97 3a 04 40 af c4 c8 ce f5 a1 3a a2 05 08 d6 7f 80 57 80 59 86 f0 f9 b7 9b 73 39 d7 0f 26 55 23 0a c9 fd 13 ca 74 6f 63 c7 55 62 b2 97 7b ba 63 1e e2 3a d9 9e e7 56 f0 dc b1 a4 81 d9 30 81 d6 a0 03 02 01 12 a2 81 ce 04 81 cb 56 19 2a 2e 93 aa 1b 61 29 8d 75 8d df 67 4f 05 96 c9 69 c0 90 8d 96 4f 6f af f4 5d 59 51 3a 6e 6e 27 03 42 3a 0a 10 3f 73 7e c7 ad ca d0 e2 cd 52 78 32 c8 68 7d 82 ea d8 d4 d6 d7 fa 42 92 d6 04 08 3c de 0f 0f cd a5 fe 83 62 1e 9b 92 6a 53 7c 67 0e 4d ed 71 31 c8 a0 db f0 2b 00 72 1a 9f 67 56 17 c3 1b 25 d5 00 4b c9 03 48 4f ad 4a a5 7f 81 e0 65 1f 18 06 9f 8f 13 18 22 1e 8f 42 2f 77 ad 6d 5a 9c 1b 1e ca ff 27 0e c1 5a fb 76 79 bc 31 25 2d 05 36 e8 a5 bd 1c e0 e0 b4 e2 33 d7 26 83 3f f1 f9 26 ed 1f de 26 58 53 30 ed 19 11 44 f4 d7 fa 10 b9 d0 68 cf 42 7d 08 34 9a 1c 71 5b f0 78 36 34 90 f1 fa bc e2 dc
    [Krb5LoginModule]: Entering logout
    [Krb5LoginModule]: logged out Subject
    HTTP Status Code: 200
    HTTP Status Message: null
    HTTP Body:
    {"name":"FirstFoo","message":"Hello","principalName":"client/localhost@TEST.REALM"}
    [Krb5LoginModule]: Entering logout
    [Krb5LoginModule]: logged out Subject
    
    Process finished with exit code 0


### Typical output of client
    2022-07-01 00:26:32.856 DEBUG 20843 --- [           main] tractDirtiesContextTestExecutionListener : Before test method: context [DefaultTestContext@3af9c5b7 testClass = RestClientManualTest, testInstance = com.krb.restwithkerberos.client.RestClientManualTest@2f465398, testMethod = givenKerberizedRestTemplate_whenServiceCall_thenSuccess@RestClientManualTest, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> false, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]], class annotated with @DirtiesContext [false] with mode [null], method annotated with @DirtiesContext [false] with mode [null].
    2022-07-01 00:26:32.867 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:32.867 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 2, missCount = 1]
    2022-07-01 00:26:32.867 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:32.867 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 3, missCount = 1]
    2022-07-01 00:26:32.877 DEBUG 20843 --- [      Finalizer] h.i.c.PoolingHttpClientConnectionManager : Connection manager is shutting down
    2022-07-01 00:26:32.882 DEBUG 20843 --- [      Finalizer] h.i.c.PoolingHttpClientConnectionManager : Connection manager shut down
    2022-07-01 00:26:33.206 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:33.208 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 4, missCount = 1]
    Debug is  true storeKey true useTicketCache false useKeyTab true doNotPrompt true ticketCache is null isInitiator true KeyTab is service-user.keytab refreshKrb5Config is false principal is client/localhost@TEST.REALM tryFirstPass is false useFirstPass is false storePass is false clearPass is false
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): HTTP
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 60; type: 17
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): HTTP
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 60; type: 23
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): HTTP
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 76; type: 18
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): alice
    >>> KeyTab: load() entry length: 50; type: 17
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): alice
    >>> KeyTab: load() entry length: 50; type: 23
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): alice
    >>> KeyTab: load() entry length: 66; type: 18
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): client
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 62; type: 17
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): client
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 62; type: 23
    >>> KeyTabInputStream, readName(): TEST.REALM
    >>> KeyTabInputStream, readName(): client
    >>> KeyTabInputStream, readName(): localhost
    >>> KeyTab: load() entry length: 78; type: 18
    Looking for keys for: client/localhost@TEST.REALM
    Java config name: /home/max/Documents/RestWithKerberos/krb5.conf
    Loaded from Java config
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    >>> KdcAccessibility: reset
    Looking for keys for: client/localhost@TEST.REALM
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    Using builtin default etypes for default_tkt_enctypes
    default etypes for default_tkt_enctypes: 18 17 16 23.
    >>> KrbAsReq creating message
    >>> KrbKdcReq send: kdc=localhost TCP:10088, timeout=30000, number of retries =3, #bytes=150
    >>> KDCCommunication: kdc=localhost TCP:10088, timeout=30000,Attempt =1, #bytes=150
    >>>DEBUG: TCPClient reading 166 bytes
    >>> KrbKdcReq send: #bytes read=166
    >>>Pre-Authentication Data:
    PA-DATA type = 19
    PA-ETYPE-INFO2 etype = 18, salt = null, s2kparams = null
    PA-ETYPE-INFO2 etype = 17, salt = null, s2kparams = null
    
    >>> KdcAccessibility: remove localhost:10088
    >>> KDCRep: init() encoding tag is 126 req type is 11
    >>>KRBError:
    sTime is Fri Jul 01 00:26:33 MSK 2022 1656624393000
    suSec is 100
    error code is 25
    error Message is Additional pre-authentication required
    sname is client/localhost@TEST.REALM
    eData provided.
    msgType is 30
    >>>Pre-Authentication Data:
    PA-DATA type = 19
    PA-ETYPE-INFO2 etype = 18, salt = null, s2kparams = null
    PA-ETYPE-INFO2 etype = 17, salt = null, s2kparams = null
    
    KRBError received: Additional pre-authentication required
    KrbAsReqBuilder: PREAUTH FAILED/REQ, re-send AS-REQ
    Using builtin default etypes for default_tkt_enctypes
    default etypes for default_tkt_enctypes: 18 17 16 23.
    Looking for keys for: client/localhost@TEST.REALM
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    Looking for keys for: client/localhost@TEST.REALM
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    Using builtin default etypes for default_tkt_enctypes
    default etypes for default_tkt_enctypes: 18 17 16 23.
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> KrbAsReq creating message
    >>> KrbKdcReq send: kdc=localhost TCP:10088, timeout=30000, number of retries =3, #bytes=237
    >>> KDCCommunication: kdc=localhost TCP:10088, timeout=30000,Attempt =1, #bytes=237
    >>>DEBUG: TCPClient reading 568 bytes
    >>> KrbKdcReq send: #bytes read=568
    >>> KdcAccessibility: remove localhost:10088
    Looking for keys for: client/localhost@TEST.REALM
    Added key: 18version: 1
    Added key: 23version: 1
    Added key: 17version: 1
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> KrbAsRep cons in KrbAsReq.getReply client/localhost
    principal is client/localhost@TEST.REALM
    Will use keytab
    Commit Succeeded
    
    2022-07-01 00:26:33.371 DEBUG 20843 --- [           main] o.s.s.k.client.KerberosRestTemplate      : HTTP GET http://localhost:8080/foos
    2022-07-01 00:26:33.372 DEBUG 20843 --- [           main] o.s.s.k.client.KerberosRestTemplate      : Accept=[text/plain, application/json, application/*+json, */*]
    2022-07-01 00:26:33.400 DEBUG 20843 --- [           main] o.a.h.client.protocol.RequestAddCookies  : CookieSpec selected: default
    2022-07-01 00:26:33.426 DEBUG 20843 --- [           main] o.a.h.client.protocol.RequestAuthCache   : Auth cache not set in the context
    2022-07-01 00:26:33.428 DEBUG 20843 --- [           main] h.i.c.PoolingHttpClientConnectionManager : Connection request: [route: {}->http://localhost:8080][total available: 0; route allocated: 0 of 2; total allocated: 0 of 20]
    2022-07-01 00:26:33.522 DEBUG 20843 --- [           main] h.i.c.PoolingHttpClientConnectionManager : Connection leased: [id: 0][route: {}->http://localhost:8080][total available: 0; route allocated: 1 of 2; total allocated: 1 of 20]
    2022-07-01 00:26:33.525 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Opening connection {}->http://localhost:8080
    2022-07-01 00:26:33.527 DEBUG 20843 --- [           main] .i.c.DefaultHttpClientConnectionOperator : Connecting to localhost/127.0.0.1:8080
    2022-07-01 00:26:33.530 DEBUG 20843 --- [           main] .i.c.DefaultHttpClientConnectionOperator : Connection established 127.0.0.1:49860<->127.0.0.1:8080
    2022-07-01 00:26:33.532 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Executing request GET /foos HTTP/1.1
    2022-07-01 00:26:33.532 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Target auth state: UNCHALLENGED
    2022-07-01 00:26:33.533 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Proxy auth state: UNCHALLENGED
    2022-07-01 00:26:33.533 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> GET /foos HTTP/1.1
    2022-07-01 00:26:33.540 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Accept: text/plain, application/json, application/*+json, */*
    2022-07-01 00:26:33.540 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Host: localhost:8080
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Connection: Keep-Alive
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> User-Agent: Apache-HttpClient/4.5.13 (Java/1.8.0_331)
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Accept-Encoding: gzip,deflate
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "GET /foos HTTP/1.1[\r][\n]"
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Accept: text/plain, application/json, application/*+json, */*[\r][\n]"
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Host: localhost:8080[\r][\n]"
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Connection: Keep-Alive[\r][\n]"
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "User-Agent: Apache-HttpClient/4.5.13 (Java/1.8.0_331)[\r][\n]"
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Accept-Encoding: gzip,deflate[\r][\n]"
    2022-07-01 00:26:33.541 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "HTTP/1.1 401 [\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "WWW-Authenticate: Negotiate[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "X-Content-Type-Options: nosniff[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "X-XSS-Protection: 1; mode=block[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Cache-Control: no-cache, no-store, max-age=0, must-revalidate[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Pragma: no-cache[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Expires: 0[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "X-Frame-Options: DENY[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Transfer-Encoding: chunked[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Date: Thu, 30 Jun 2022 21:26:33 GMT[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Keep-Alive: timeout=60[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Connection: keep-alive[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "0[\r][\n]"
    2022-07-01 00:26:33.551 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "[\r][\n]"
    2022-07-01 00:26:33.553 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << HTTP/1.1 401
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << WWW-Authenticate: Negotiate
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << X-Content-Type-Options: nosniff
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << X-XSS-Protection: 1; mode=block
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Pragma: no-cache
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Expires: 0
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << X-Frame-Options: DENY
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Transfer-Encoding: chunked
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Date: Thu, 30 Jun 2022 21:26:33 GMT
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Keep-Alive: timeout=60
    2022-07-01 00:26:33.557 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Connection: keep-alive
    2022-07-01 00:26:33.560 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Connection can be kept alive for 60000 MILLISECONDS
    2022-07-01 00:26:33.560 DEBUG 20843 --- [           main] o.a.http.impl.auth.HttpAuthenticator     : Authentication required
    2022-07-01 00:26:33.560 DEBUG 20843 --- [           main] o.a.http.impl.auth.HttpAuthenticator     : localhost:8080 requested authentication
    2022-07-01 00:26:33.560 DEBUG 20843 --- [           main] o.a.h.i.c.TargetAuthenticationStrategy   : Authentication schemes in the order of preference: [Negotiate, Kerberos, NTLM, CredSSP, Digest, Basic]
    2022-07-01 00:26:33.569 DEBUG 20843 --- [           main] org.apache.http.impl.auth.SPNegoScheme   : Received challenge '' from the auth server
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for Kerberos authentication scheme not available
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for NTLM authentication scheme not available
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for CredSSP authentication scheme not available
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for Digest authentication scheme not available
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for Basic authentication scheme not available
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.http.impl.auth.HttpAuthenticator     : Selected authentication options: [NEGOTIATE]
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Executing request GET /foos HTTP/1.1
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Target auth state: CHALLENGED
    2022-07-01 00:26:33.573 DEBUG 20843 --- [           main] o.a.http.impl.auth.HttpAuthenticator     : Generating response to an authentication challenge using Negotiate scheme
    2022-07-01 00:26:33.574 DEBUG 20843 --- [           main] org.apache.http.impl.auth.SPNegoScheme   : init localhost
    Found ticket for client/localhost@TEST.REALM to go to krbtgt/TEST.REALM@TEST.REALM expiring on Sat Jul 02 00:26:33 MSK 2022
    Entered Krb5Context.initSecContext with state=STATE_NEW
    Found ticket for client/localhost@TEST.REALM to go to krbtgt/TEST.REALM@TEST.REALM expiring on Sat Jul 02 00:26:33 MSK 2022
    Service ticket not found in the subject
    >>> Credentials serviceCredsSingle: same realm
    Using builtin default etypes for default_tgs_enctypes
    default etypes for default_tgs_enctypes: 18 17 16 23.
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> CksumType: sun.security.krb5.internal.crypto.HmacSha1Aes256CksumType
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> KrbKdcReq send: kdc=localhost TCP:10088, timeout=30000, number of retries =3, #bytes=592
    >>> KDCCommunication: kdc=localhost TCP:10088, timeout=30000,Attempt =1, #bytes=592
    >>>DEBUG: TCPClient reading 557 bytes
    >>> KrbKdcReq send: #bytes read=557
    >>> KdcAccessibility: remove localhost:10088
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    >>> TGS credentials serviceCredsSingle:
    >>> DEBUG: ----Credentials----
    client: client/localhost@TEST.REALM
    server: HTTP/localhost@TEST.REALM
    ticket: sname: HTTP/localhost@TEST.REALM
    endTime: 1656710793000
    ----Credentials end----
    >>> KrbApReq: APOptions are 00100000 00000000 00000000 00000000
    >>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
    Krb5Context setting mySeqNumber to: 836663727
    Created InitSecContextToken:
    0000: 01 00 6E 82 01 FF 30 82   01 FB A0 03 02 01 05 A1  ..n...0.........
    0010: 03 02 01 0E A2 07 03 05   00 20 00 00 00 A3 82 01  ......... ......
    0020: 08 61 82 01 04 30 82 01   00 A0 03 02 01 05 A1 0C  .a...0..........
    0030: 1B 0A 54 45 53 54 2E 52   45 41 4C 4D A2 1C 30 1A  ..TEST.REALM..0.
    0040: A0 03 02 01 00 A1 13 30   11 1B 04 48 54 54 50 1B  .......0...HTTP.
    0050: 09 6C 6F 63 61 6C 68 6F   73 74 A3 81 CC 30 81 C9  .localhost...0..
    0060: A0 03 02 01 12 A1 03 02   01 01 A2 81 BC 04 81 B9  ................
    0070: 30 22 60 CF 35 50 E1 FB   EC 36 27 94 2B 33 A4 BF  0"`.5P...6'.+3..
    0080: 40 46 71 80 E7 B7 30 A6   73 4A 07 80 E6 A8 04 18  @Fq...0.sJ......
    0090: 1E 74 B9 4F 4B C6 84 98   60 86 C9 51 AE 5F AA 19  .t.OK...`..Q._..
    00A0: 19 42 36 BB 7B 57 DA D4   13 12 A4 DE 9B 36 32 5B  .B6..W.......62[
    00B0: 2F 3E 7D 99 8C 7C FD DF   65 22 56 75 85 6B A4 2C  />......e"Vu.k.,
    00C0: B1 70 09 EF 02 7E AE A1   1F 11 2E DD 1C 22 81 08  .p..........."..
    00D0: 85 C1 BE 5B B9 B9 D7 67   B2 14 6D 44 D0 03 28 A7  ...[...g..mD..(.
    00E0: 10 64 E5 E0 27 F6 3F B9   BA C7 D4 A3 24 04 0A 1A  .d..'.?.....$...
    00F0: 29 85 A4 1B 47 66 CB 1E   D6 E9 83 CE 0F 21 08 FA  )...Gf.......!..
    0100: 2F 83 77 8E 6E 0B 3E 4B   CF 5E 8C E1 0E 66 3E 98  /.w.n.>K.^...f>.
    0110: 4D 60 B0 65 CB 92 0F F8   3D 6D 14 68 4A A7 82 3B  M`.e....=m.hJ..;
    0120: B2 8C 20 E6 37 92 59 CC   35 A4 81 D9 30 81 D6 A0  .. .7.Y.5...0...
    0130: 03 02 01 12 A2 81 CE 04   81 CB 69 5A A3 72 0C 89  ..........iZ.r..
    0140: 7F 61 F6 3B 6F 2C 79 B2   EA 78 C5 13 91 66 AB E1  .a.;o,y..x...f..
    0150: 20 FC 1E D2 0E 3D 40 14   CC 8A 3C FF B6 7E 76 1D   ....=@...<...v.
    0160: 8B 63 78 BA 45 E1 25 FF   2D 0A EE F0 93 66 25 CB  .cx.E.%.-....f%.
    0170: 88 BD E7 C2 04 CB 49 BA   8F 25 FD F8 5B 7F 91 10  ......I..%..[...
    0180: 23 DC 4F 8C CB 85 ED 60   F5 96 99 B5 1F F0 BB DF  #.O....`........
    0190: 9C EC 11 10 9A 3E 57 24   5D 5E 44 79 3B B4 C4 3B  .....>W$]^Dy;..;
    01A0: A2 68 A8 D2 4A 0E 03 A9   39 FB 88 42 E4 D5 D7 04  .h..J...9..B....
    01B0: 25 76 E4 57 2B 0A F8 49   B1 D9 28 7D 4B A5 A6 88  %v.W+..I..(.K...
    01C0: 91 BE A6 64 4F EC E7 88   BF D9 02 12 FB EA 13 71  ...dO..........q
    01D0: 9C 7A 44 2D 8E EB 28 F5   E1 0C A6 19 D6 A0 3A 69  .zD-..(.......:i
    01E0: E8 5E A2 BD 5B 64 24 66   C7 8F A2 F7 24 BD A8 C0  .^..[d$f....$...
    01F0: 88 D8 69 F4 4F F0 A6 0D   5E 05 B1 70 2E C9 7B 3E  ..i.O...^..p...>
    0200: 7C B3 17 E0 75                                     ....u
    
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.impl.auth.SPNegoScheme   : Sending response 'YIICQQYGKwYBBQUCoIICNTCCAjGgDTALBgkqhkiG9xIBAgKhBAMCAXaiggIYBIICFGCCAhAGCSqGSIb3EgECAgEAboIB/zCCAfugAwIBBaEDAgEOogcDBQAgAAAAo4IBCGGCAQQwggEAoAMCAQWhDBsKVEVTVC5SRUFMTaIcMBqgAwIBAKETMBEbBEhUVFAbCWxvY2FsaG9zdKOBzDCByaADAgESoQMCAQGigbwEgbkwImDPNVDh++w2J5QrM6S/QEZxgOe3MKZzSgeA5qgEGB50uU9LxoSYYIbJUa5fqhkZQja7e1fa1BMSpN6bNjJbLz59mYx8/d9lIlZ1hWukLLFwCe8Cfq6hHxEu3RwigQiFwb5bubnXZ7IUbUTQAyinEGTl4Cf2P7m6x9SjJAQKGimFpBtHZsse1umDzg8hCPovg3eObgs+S89ejOEOZj6YTWCwZcuSD/g9bRRoSqeCO7KMIOY3klnMNaSB2TCB1qADAgESooHOBIHLaVqjcgyJf2H2O28sebLqeMUTkWar4SD8HtIOPUAUzIo8/7Z+dh2LY3i6ReEl/y0K7vCTZiXLiL3nwgTLSbqPJf34W3+RECPcT4zLhe1g9ZaZtR/wu9+c7BEQmj5XJF1eRHk7tMQ7omio0koOA6k5+4hC5NXXBCV25FcrCvhJsdkofUulpoiRvqZkT+zniL/ZAhL76hNxnHpELY7rKPXhDKYZ1qA6aeheor1bZCRmx4+i9yS9qMCI2Gn0T/CmDV4FsXAuyXs+fLMX4HU=' back to the auth server
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Proxy auth state: UNCHALLENGED
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> GET /foos HTTP/1.1
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Accept: text/plain, application/json, application/*+json, */*
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Host: localhost:8080
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Connection: Keep-Alive
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> User-Agent: Apache-HttpClient/4.5.13 (Java/1.8.0_331)
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Accept-Encoding: gzip,deflate
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Authorization: Negotiate YIICQQYGKwYBBQUCoIICNTCCAjGgDTALBgkqhkiG9xIBAgKhBAMCAXaiggIYBIICFGCCAhAGCSqGSIb3EgECAgEAboIB/zCCAfugAwIBBaEDAgEOogcDBQAgAAAAo4IBCGGCAQQwggEAoAMCAQWhDBsKVEVTVC5SRUFMTaIcMBqgAwIBAKETMBEbBEhUVFAbCWxvY2FsaG9zdKOBzDCByaADAgESoQMCAQGigbwEgbkwImDPNVDh++w2J5QrM6S/QEZxgOe3MKZzSgeA5qgEGB50uU9LxoSYYIbJUa5fqhkZQja7e1fa1BMSpN6bNjJbLz59mYx8/d9lIlZ1hWukLLFwCe8Cfq6hHxEu3RwigQiFwb5bubnXZ7IUbUTQAyinEGTl4Cf2P7m6x9SjJAQKGimFpBtHZsse1umDzg8hCPovg3eObgs+S89ejOEOZj6YTWCwZcuSD/g9bRRoSqeCO7KMIOY3klnMNaSB2TCB1qADAgESooHOBIHLaVqjcgyJf2H2O28sebLqeMUTkWar4SD8HtIOPUAUzIo8/7Z+dh2LY3i6ReEl/y0K7vCTZiXLiL3nwgTLSbqPJf34W3+RECPcT4zLhe1g9ZaZtR/wu9+c7BEQmj5XJF1eRHk7tMQ7omio0koOA6k5+4hC5NXXBCV25FcrCvhJsdkofUulpoiRvqZkT+zniL/ZAhL76hNxnHpELY7rKPXhDKYZ1qA6aeheor1bZCRmx4+i9yS9qMCI2Gn0T/CmDV4FsXAuyXs+fLMX4HU=
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "GET /foos HTTP/1.1[\r][\n]"
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Accept: text/plain, application/json, application/*+json, */*[\r][\n]"
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Host: localhost:8080[\r][\n]"
    2022-07-01 00:26:33.606 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Connection: Keep-Alive[\r][\n]"
    2022-07-01 00:26:33.607 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "User-Agent: Apache-HttpClient/4.5.13 (Java/1.8.0_331)[\r][\n]"
    2022-07-01 00:26:33.607 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Accept-Encoding: gzip,deflate[\r][\n]"
    2022-07-01 00:26:33.607 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "Authorization: Negotiate YIICQQYGKwYBBQUCoIICNTCCAjGgDTALBgkqhkiG9xIBAgKhBAMCAXaiggIYBIICFGCCAhAGCSqGSIb3EgECAgEAboIB/zCCAfugAwIBBaEDAgEOogcDBQAgAAAAo4IBCGGCAQQwggEAoAMCAQWhDBsKVEVTVC5SRUFMTaIcMBqgAwIBAKETMBEbBEhUVFAbCWxvY2FsaG9zdKOBzDCByaADAgESoQMCAQGigbwEgbkwImDPNVDh++w2J5QrM6S/QEZxgOe3MKZzSgeA5qgEGB50uU9LxoSYYIbJUa5fqhkZQja7e1fa1BMSpN6bNjJbLz59mYx8/d9lIlZ1hWukLLFwCe8Cfq6hHxEu3RwigQiFwb5bubnXZ7IUbUTQAyinEGTl4Cf2P7m6x9SjJAQKGimFpBtHZsse1umDzg8hCPovg3eObgs+S89ejOEOZj6YTWCwZcuSD/g9bRRoSqeCO7KMIOY3klnMNaSB2TCB1qADAgESooHOBIHLaVqjcgyJf2H2O28sebLqeMUTkWar4SD8HtIOPUAUzIo8/7Z+dh2LY3i6ReEl/y0K7vCTZiXLiL3nwgTLSbqPJf34W3+RECPcT4zLhe1g9ZaZtR/wu9+c7BEQmj5XJF1eRHk7tMQ7omio0koOA6k5+4hC5NXXBCV25FcrCvhJsdkofUulpoiRvqZkT+zniL/ZAhL76hNxnHpELY7rKPXhDKYZ1qA6aeheor1bZCRmx4+i9yS9qMCI2Gn0T/CmDV4FsXAuyXs+fLMX4HU=[\r][\n]"
    2022-07-01 00:26:33.607 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 >> "[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "HTTP/1.1 200 [\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Set-Cookie: JSESSIONID=5E850FECF02EB03DBC7672A6AF298CBB; Path=/; HttpOnly[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "X-Content-Type-Options: nosniff[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "X-XSS-Protection: 1; mode=block[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Cache-Control: no-cache, no-store, max-age=0, must-revalidate[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Pragma: no-cache[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Expires: 0[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "X-Frame-Options: DENY[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Content-Type: application/json[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Transfer-Encoding: chunked[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Date: Thu, 30 Jun 2022 21:26:33 GMT[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Keep-Alive: timeout=60[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "Connection: keep-alive[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "53[\r][\n]"
    2022-07-01 00:26:33.612 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "{"name":"FirstFoo","message":"Hello","principalName":"client/localhost@TEST.REALM"}[\r][\n]"
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "0[\r][\n]"
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.wire                     : http-outgoing-0 << "[\r][\n]"
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << HTTP/1.1 200
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Set-Cookie: JSESSIONID=5E850FECF02EB03DBC7672A6AF298CBB; Path=/; HttpOnly
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << X-Content-Type-Options: nosniff
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << X-XSS-Protection: 1; mode=block
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Pragma: no-cache
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Expires: 0
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << X-Frame-Options: DENY
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Content-Type: application/json
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Transfer-Encoding: chunked
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Date: Thu, 30 Jun 2022 21:26:33 GMT
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Keep-Alive: timeout=60
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] org.apache.http.headers                  : http-outgoing-0 << Connection: keep-alive
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] o.a.http.impl.execchain.MainClientExec   : Connection can be kept alive for 60000 MILLISECONDS
    2022-07-01 00:26:33.613 DEBUG 20843 --- [           main] o.a.http.impl.auth.HttpAuthenticator     : Authentication succeeded
    2022-07-01 00:26:33.621 DEBUG 20843 --- [           main] o.a.h.c.protocol.ResponseProcessCookies  : Cookie accepted [JSESSIONID="5E850FECF02EB03DBC7672A6AF298CBB", version:0, domain:localhost, path:/, expiry:null]
    2022-07-01 00:26:33.624 DEBUG 20843 --- [           main] o.s.s.k.client.KerberosRestTemplate      : Response 200 OK
    2022-07-01 00:26:33.625 DEBUG 20843 --- [           main] o.s.s.k.client.KerberosRestTemplate      : Reading to [java.lang.String] as "application/json"
    2022-07-01 00:26:33.646 DEBUG 20843 --- [           main] h.i.c.PoolingHttpClientConnectionManager : Connection [id: 0][route: {}->http://localhost:8080] can be kept alive for 60.0 seconds
    2022-07-01 00:26:33.646 DEBUG 20843 --- [           main] h.i.c.DefaultManagedHttpClientConnection : http-outgoing-0: set socket timeout to 0
    2022-07-01 00:26:33.646 DEBUG 20843 --- [           main] h.i.c.PoolingHttpClientConnectionManager : Connection released: [id: 0][route: {}->http://localhost:8080][total available: 1; route allocated: 1 of 2; total allocated: 1 of 20]
    2022-07-01 00:26:33.646 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:33.647 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 5, missCount = 1]
    2022-07-01 00:26:33.655 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:33.655 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 6, missCount = 1]
    2022-07-01 00:26:33.656 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:33.656 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 7, missCount = 1]
    2022-07-01 00:26:33.656 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:33.671 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 8, missCount = 1]
    2022-07-01 00:26:33.673 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:33.673 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 9, missCount = 1]
    2022-07-01 00:26:33.688 DEBUG 20843 --- [           main] c.DefaultCacheAwareContextLoaderDelegate : Retrieved ApplicationContext [616881582] from cache with key [[WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]]]
    2022-07-01 00:26:33.688 DEBUG 20843 --- [           main] org.springframework.test.context.cache   : Spring test ApplicationContext cache statistics: [DefaultContextCache@3138953b size = 1, maxSize = 32, parentContextCount = 0, hitCount = 10, missCount = 1]
    2022-07-01 00:26:33.690 DEBUG 20843 --- [           main] tractDirtiesContextTestExecutionListener : After test method: context [DefaultTestContext@3af9c5b7 testClass = RestClientManualTest, testInstance = com.krb.restwithkerberos.client.RestClientManualTest@2f465398, testMethod = givenKerberizedRestTemplate_whenServiceCall_thenSuccess@RestClientManualTest, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@37271612 testClass = RestClientManualTest, locations = '{}', classes = '{class com.krb.restwithkerberos.client.RestClientApp}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=0}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2f0a87b3, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@31f924f5, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@61ca2dfa, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@7dc222ae, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@50de0926, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@4d95d2a2], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> false, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]], class annotated with @DirtiesContext [false] with mode [null], method annotated with @DirtiesContext [false] with mode [null].

