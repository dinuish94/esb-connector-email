package org.wso2.carbon.connector.pojo;

import org.wso2.carbon.connector.connection.EmailProtocol;
import org.wso2.carbon.connector.utils.EmailConstants;

public class ConnectionConfiguration {

    private String host;
    private String port;
    private String connectionName;
    private String username;
    private String password;
    private EmailProtocol protocol;
    private String readTimeout;
    private String connectionTimeout;
    private String writeTimeout;
    private boolean requireTLS;
    private boolean checkServerIdentity;
    private String trustedHosts;
    private String sslProtocols;
    private String cipherSuites;
    private int maxActiveConnections;
    private int maxIdleConnections;
    private long maxWaitTime;
    private long minEvictionTime;
    private long evictionCheckInterval;
    private String exhaustedAction;
    private String initialisationPolicy;
    private boolean disablePooling;

    public String getHost() {

        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public String getPort() {

        return port;
    }

    public void setPort(String port) {

        this.port = port;
    }

    public String getConnectionName() {

        return connectionName;
    }

    public void setConnectionName(String connectionName) {

        this.connectionName = connectionName;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public EmailProtocol getProtocol() {

        return protocol;
    }

    public void setProtocol(EmailProtocol protocol) {

        this.protocol = protocol;
    }

    public String getReadTimeout() {

        return readTimeout;
    }

    public void setReadTimeout(String readTimeout) {

        this.readTimeout = readTimeout;
    }

    public String getConnectionTimeout() {

        return connectionTimeout;
    }

    public void setConnectionTimeout(String connectionTimeout) {

        this.connectionTimeout = connectionTimeout;
    }

    public String getWriteTimeout() {

        return writeTimeout;
    }

    public void setWriteTimeout(String writeTimeout) {

        this.writeTimeout = writeTimeout;
    }

    public boolean isRequireTLS() {

        return requireTLS;
    }

    public void setRequireTLS(boolean requireTLS) {

        this.requireTLS = requireTLS;
    }

    public boolean isCheckServerIdentity() {

        return checkServerIdentity;
    }

    public void setCheckServerIdentity(boolean checkServerIdentity) {

        this.checkServerIdentity = checkServerIdentity;
    }

    public String getTrustedHosts() {

        return trustedHosts;
    }

    public void setTrustedHosts(String trustedHosts) {

        this.trustedHosts = trustedHosts;
    }

    public String getSslProtocols() {

        return sslProtocols;
    }

    public void setSslProtocols(String sslProtocols) {

        this.sslProtocols = sslProtocols;
    }

    public String getCipherSuites() {

        return cipherSuites;
    }

    public void setCipherSuites(String cipherSuites) {

        this.cipherSuites = cipherSuites;
    }

    public int getMaxActiveConnections() {
        if (this.maxActiveConnections == 0){
            this.maxActiveConnections = EmailConstants.DEFAULT_MAX_ACTIVE_CONNECTIONS;
        }
        return maxActiveConnections;
    }

    public void setMaxActiveConnections(int maxActiveConnections) {

        this.maxActiveConnections = maxActiveConnections;
    }

    public int getMaxIdleConnections() {
        if (this.maxIdleConnections == 0){
            this.maxIdleConnections = EmailConstants.DEFAULT_MAX_IDLE_CONNECTIONS;
        }
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {

        this.maxIdleConnections = maxIdleConnections;
    }

    public long getMaxWaitTime() {

        return maxWaitTime;
    }

    public void setMaxWaitTime(long maxWaitTime) {

        this.maxWaitTime = maxWaitTime;
    }

    public long getMinEvictionTime() {

        return minEvictionTime;
    }

    public void setMinEvictionTime(long minEvictionTime) {

        this.minEvictionTime = minEvictionTime;
    }

    public long getEvictionCheckInterval() {

        return evictionCheckInterval;
    }

    public void setEvictionCheckInterval(long evictionCheckInterval) {

        this.evictionCheckInterval = evictionCheckInterval;
    }

    public String getExhaustedAction() {

        return exhaustedAction;
    }

    public void setExhaustedAction(String exhaustedAction) {

        this.exhaustedAction = exhaustedAction;
    }

    public String getInitialisationPolicy() {

        return initialisationPolicy;
    }

    public void setInitialisationPolicy(String initialisationPolicy) {

        this.initialisationPolicy = initialisationPolicy;
    }

    public boolean getDisablePooling() {

        return disablePooling;
    }

    public void setDisablePooling(boolean disablePooling) {

        this.disablePooling = disablePooling;
    }
}
