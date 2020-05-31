package org.wso2.carbon.connector.connection;

import org.wso2.carbon.connector.utils.EmailConfigProperties;

import java.net.Socket;

import static java.lang.String.format;

/**
 * Contains the Email Protocols and mail configurations
 */
public enum EmailProtocol {

    /**
     * represents the Simple Mail Transfer Protocol.
     */
    SMTP("smtp", false),

    /**
     * represents the secured Simple Mail Transfer Protocol.
     */
    SMTPS("smtp", true),

    /**
     * represents the Internet Message Access Protocol.
     */
    IMAP("imap", false),

    /**
     * represents the secured Internet Message Access Protocol.
     */
    IMAPS("imap", true),

    /**
     * represents the Post Office Protocol.
     */
    POP3("pop3", false),

    /**
     * represents the secured Post Office Protocol.
     */
    POP3S("pop3", true);

    private final String name;
    private final boolean secure;

    /**
     * Creates an Email Protocol instance.
     *
     * @param name the name of the protocol.
     * @param secure whether the protocol is secure or not.
     */
    EmailProtocol(String name, boolean secure) {

        this.name = name;
        this.secure = secure;
    }

    public String getName() {

        return name;
    }

    public boolean isSecure() {

        return secure;
    }

    /**
     * The host name of the mail server.
     *
     * @return the protocol host name property.
     */
    public String getHostProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_HOST);
    }

    /**
     * The port number of the mail server.
     *
     * @return the protocol port property.
     */
    public String getPortProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_PORT);
    }

    /**
     * Indicates if should attempt to authorize or not. Defaults to false.
     *
     * @return the protocol mail auth property.
     */
    public String getMailAuthProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_AUTH);
    }

    /**
     * Whether to use {@link Socket} as a fallback if the initial connection fails or not.
     *
     * @return the protocol socket factory fallback property.
     */
    public String getSocketFactoryFallbackProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_SOCKETFACTORY_FALLBACK);
    }

    /**
     * Specifies the port to connect to when using a socket factory.
     *
     * @return the protocol socket factory port property.
     */
    public String getSocketFactoryPortProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_SOCKETFACTORY_PORT);
    }

    /**
     * Specifies the SSL ciphersuites that will be enabled for SSL connections.
     *
     * @return the protocol ssl ciphersuites property.
     */
    public String getSslCiphersuitesProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_SSL_CIPHER_SUITES);
    }

    /**
     * Specifies the SSL protocols that will be enabled for SSL connections.
     *
     * @return the protocol ssl enabled protocols property.
     */
    public String getSslProtocolsProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_SSL_PROTOCOLS);
    }

    /**
     * Specifies if ssl is enabled or not.
     *
     * @return the ssl enable property.
     */
    public String getSslEnableProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_SSL_ENABLE);
    }

    /**
     * Specifies the trusted hosts.
     *
     * @return the protocol ssl trust property.
     */
    public String getSslTrustProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_SSL_TRUST);
    }

    /**
     * Indicates if the STARTTLS command shall be used to initiate a TLS-secured connection.
     *
     * @return the protocol start tls property.
     */
    public String getStartTlsProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_START_TLS_ENABLE);
    }

    /**
     * Specifies the default transport name.
     *
     * @return the protocol name property.
     */
    public String getTransportProtocolProperty() {

        return EmailConfigProperties.PROPERTY_TRANSPORT_NAME;
    }

    /**
     * Socket read timeout value in milliseconds. Default is infinite timeout.
     *
     * @return the protocol read timeout property.
     */
    public String getReadTimeoutProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_TIMEOUT);
    }

    /**
     * Socket connection timeout value in milliseconds. Default is infinite timeout.
     *
     * @return the protocol connection timeout property.
     */
    public String getConnectionTimeoutProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_CONNECTION_TIMEOUT);
    }

    /**
     * Socket write timeout value in milliseconds. Default is infinite timeout.
     *
     * @return the protocol write timeout property.
     */
    public String getWriteTimeoutProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_WRITE_TIMEOUT);
    }

    /**
     * Check the server identity as specified by RFC 2595.
     * These additional checks based on the content of the server's certificate are intended to prevent
     * man-in-the-middle attacks. Default is false.
     *
     * @return the check server Identity property.
     */
    public String getCheckServerIdentityProperty() {

        return unmaskProperty(EmailConfigProperties.PROPERTY_CHECK_SERVER_IDENTITY);
    }

    private String unmaskProperty(String property) {

        return format(property, name);
    }
}
