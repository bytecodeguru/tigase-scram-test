package com.catapush.test;

import java.io.IOException;
import java.util.Arrays;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SmackScramIT {

    private static final String HOST = "host";
    private static final int PORT = 5222;
    private static final String VHOST = "vhost";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String RESOURCE = "resource";
    private final boolean usePlainAuth;

    @Parameters(name = "use plain auth: {0}")
    public static Iterable<Boolean> params() {
        return Arrays.asList(true, false);
    }

    public SmackScramIT(boolean usePlainAuth) {
        this.usePlainAuth = usePlainAuth;
    }

    @Test
    public void login() throws SmackException, XMPPException, IOException {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setCompressionEnabled(false);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        config.setUsernameAndPassword(USER, PASSWORD);
        config.setResource(RESOURCE);
        config.setHost(HOST);
        config.setPort(PORT);
        config.setServiceName(VHOST);

        if (usePlainAuth) {
            System.out.printf("Using PLAIN auth.%n");
            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        } else {
            System.out.printf("Using SCRAM auth.%n");
            SASLAuthentication.unBlacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.blacklistSASLMechanism("PLAIN");
        }

        XMPPTCPConnection connection = null;
        try {
            connection = new XMPPTCPConnection(config.build());
            connection.setUseStreamManagement(true);
            connection.setUseStreamManagementResumption(true);

            System.out.printf("Connecting to %s:%d%n", HOST, PORT);
            connection.connect();
            System.out.printf("Connected. Trying to login.%n");
            connection.login();
            System.out.printf("Login successful. Disconnecting.%n%n");

        } finally {
            if (connection != null && connection.isConnected()) {
                connection.disconnect();
            }
        }

    }

}
