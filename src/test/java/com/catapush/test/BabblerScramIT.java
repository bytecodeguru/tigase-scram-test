package com.catapush.test;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.sm.model.StreamManagement;

@RunWith(Parameterized.class)
public class BabblerScramIT {

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

    public BabblerScramIT(boolean usePlainAuth) {
        this.usePlainAuth = usePlainAuth;
    }

    @Test
    public void login() throws XmppException {
        TcpConnectionConfiguration tcpConnectionConfig = TcpConnectionConfiguration.builder()
                .hostname(HOST)
                .port(PORT)
                .secure(false)
                .build();

        XmppSessionConfiguration.Builder sessionConfigBuilder = XmppSessionConfiguration.builder();
        if (usePlainAuth) {
            System.out.printf("Using PLAIN auth.%n");
            sessionConfigBuilder.authenticationMechanisms("PLAIN");
        } else {
            System.out.printf("Using SCRAM auth.%n");
        }
        XmppSessionConfiguration sessionConfig = sessionConfigBuilder.build();

        try (XmppClient xmppClient = XmppClient.create(VHOST, sessionConfig, tcpConnectionConfig)) {
            xmppClient.enableFeature(StreamManagement.NAMESPACE);
            System.out.printf("Connecting to %s:%d%n", HOST, PORT);
            xmppClient.connect();
            System.out.printf("Connected. Trying to login.%n");
            xmppClient.login(USER, PASSWORD, RESOURCE);
            System.out.printf("Login successful. Disconnecting.%n%n");
            xmppClient.sendPresence(new Presence(Presence.Type.UNAVAILABLE));
        }
    }

}
