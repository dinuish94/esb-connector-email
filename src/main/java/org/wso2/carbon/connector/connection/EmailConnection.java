package org.wso2.carbon.connector.connection;

import org.wso2.carbon.connector.pojo.ConnectionConfiguration;
import org.wso2.carbon.connector.utils.EmailConstants;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import static org.apache.commons.lang.StringUtils.join;

/**
 * Represents an email connection
 */
public class EmailConnection {

    private Session session;
    private EmailProtocol protocol;

    public EmailConnection(ConnectionConfiguration connectionConfiguration) {

        this.protocol = connectionConfiguration.getProtocol();
        Properties sessionProperties = setSessionProperties(connectionConfiguration.getHost(),
                connectionConfiguration.getPort());
        sessionProperties.putAll(setTimeouts(connectionConfiguration.getReadTimeout(),
                connectionConfiguration.getWriteTimeout(), connectionConfiguration.getConnectionTimeout()));

        if (protocol.isSecure()) {
            sessionProperties.putAll(setSecureProperties(connectionConfiguration));
        }

        this.session = Session.getInstance(sessionProperties,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {

                        return new PasswordAuthentication(connectionConfiguration.getUsername(),
                                connectionConfiguration.getPassword());
                    }
                });
        this.session.setDebug(true);
    }

    public Session getSession() {

        return session;
    }

    /**
     * Sets basic session properties required by the protocol
     *
     * @param host Host name of the server
     * @param port Port to connect to
     * @return Properties configured
     */
    private Properties setSessionProperties(String host, String port) {

        Properties props = new Properties();
        props.setProperty(protocol.getPortProperty(), port);
        props.setProperty(protocol.getHostProperty(), host);
        props.setProperty(protocol.getTransportProtocolProperty(), protocol.getName());
        props.setProperty(protocol.getMailAuthProperty(), "true");
        return props;
    }

    /**
     * Sets secure properties
     *
     * @param connectionConfiguration configurations to be set
     * @return Properties to be configured
     */
    private Properties setSecureProperties(ConnectionConfiguration connectionConfiguration) {

        Properties props = new Properties();
        props.setProperty(protocol.getStartTlsProperty(), "true");
        if (connectionConfiguration.isRequireTLS()) {
            props.setProperty(protocol.getStartTlsProperty(), "true");
        } else {
            props.setProperty(protocol.getSslEnableProperty(), "true");
            props.setProperty(protocol.getSocketFactoryFallbackProperty(),
                    EmailConstants.DEFAULT_SOCKETFACTORY_FALLBACK);
            props.setProperty(protocol.getSocketFactoryPortProperty(),
                    String.valueOf(connectionConfiguration.getPort()));
        }

        if (connectionConfiguration.getCipherSuites() != null) {
            String[] cipherSuites = connectionConfiguration.getCipherSuites().split(",");
            props.setProperty(protocol.getSslCiphersuitesProperty(), join(cipherSuites, " "));
        }

        if (connectionConfiguration.getSslProtocols() != null) {
            String[] sslProtocols = connectionConfiguration.getSslProtocols().split(",");
            props.setProperty(protocol.getSslProtocolsProperty(), join(sslProtocols, " "));
        }

        if (connectionConfiguration.getTrustedHosts() != null) {
            String[] trustedHosts = connectionConfiguration.getTrustedHosts().split(",");
            props.setProperty(protocol.getSslTrustProperty(), join(trustedHosts, " "));
        }

        if (connectionConfiguration.isCheckServerIdentity()) {
            props.setProperty(protocol.getCheckServerIdentityProperty(), Boolean.toString(connectionConfiguration
                    .isCheckServerIdentity()));
        }

        return props;
    }

    private Properties setTimeouts(String readTimeout, String writeTimeout, String connectionTimeout) {

        Properties props = new Properties();
        if (readTimeout != null) {
            props.setProperty(protocol.getReadTimeoutProperty(), readTimeout);
        }
        if (writeTimeout != null) {
            props.setProperty(protocol.getWriteTimeoutProperty(), writeTimeout);
        }
        if (connectionTimeout != null) {
            props.setProperty(protocol.getConnectionTimeoutProperty(), connectionTimeout);
        }
        return props;
    }

}
