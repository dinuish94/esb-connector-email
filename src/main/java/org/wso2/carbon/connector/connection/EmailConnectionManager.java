package org.wso2.carbon.connector.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.connector.exception.EmailConnectionException;
import org.wso2.carbon.connector.exception.EmailConnectionPoolException;
import org.wso2.carbon.connector.pojo.ConnectionConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Manages email connections and connection pools
 */
public class EmailConnectionManager {

    private static Log log = LogFactory.getLog(EmailConnectionManager.class);

    private Map<String, EmailConnection> connectionMap;
    private Map<String, EmailConnectionPool> connectionPoolMap;

    private static EmailConnectionManager manager;

    private EmailConnectionManager(){
        this.connectionMap = Collections.synchronizedMap(new HashMap<>());
        this.connectionPoolMap = Collections.synchronizedMap(new HashMap<>());
    }

    public static synchronized EmailConnectionManager getEmailConnectionManager(){
        if (manager == null){
            synchronized (EmailConnectionManager.class) {
                if(manager == null){
                    manager = new EmailConnectionManager();
                }
            }
        }
        return manager;
    }

    private void addConnection(String name, EmailConnection emailConnection) {
        if (log.isDebugEnabled()){
            log.debug(format("Creating connection : %s", name));
        }
        connectionMap.putIfAbsent(name, emailConnection);
    }

    public EmailConnection getConnection(String name) throws EmailConnectionException {
        if (connectionMap.get(name) != null) {
            return connectionMap.get(name);
        }
        throw new EmailConnectionException(format("Connection with the name %s has not been initialized.", name));
    }

    private void addConnectionPool(String name, EmailConnectionPool emailConnectionPool) {
        if (log.isDebugEnabled()){
            log.debug(format("Creating connection pool for connection : %s", name));
        }
        connectionPoolMap.putIfAbsent(name, emailConnectionPool);
    }

    public EmailConnectionPool getConnectionPool(String name) throws EmailConnectionException {
        if (connectionPoolMap.get(name) != null){
            if (log.isDebugEnabled()){
                log.debug(format("Returning connection pool for connection: %s", name));
            }
            return connectionPoolMap.get(name);
        }
        throw new EmailConnectionException(format("Connection with the name %s has not been initialized.", name));
    }

    public synchronized void createConnection(ConnectionConfiguration connectionConfiguration) {
        String connectionName = connectionConfiguration.getConnectionName();
        if (connectionConfiguration.getProtocol().getName().equalsIgnoreCase(EmailProtocol.SMTP.name())
                && connectionMap.get(connectionName) == null){
            EmailConnection connection = new EmailConnection(connectionConfiguration);
            addConnection(connectionConfiguration.getConnectionName(), connection);
        } else if (!connectionConfiguration.getProtocol().getName().equalsIgnoreCase(EmailProtocol.SMTP.name())
                && connectionPoolMap.get(connectionName) == null) {
            EmailConnectionPool pool = new EmailConnectionPool(new EmailConnectionFactory(connectionConfiguration),
                    connectionConfiguration);
            addConnectionPool(connectionConfiguration.getConnectionName(), pool);
        } else {
            if (log.isDebugEnabled()){
                log.debug(format("Connection %s exists", connectionName));
            }
        }
    }

    public void clearConnectionPools(){
        if (log.isDebugEnabled()){
            log.debug("Clearing connection pools...");
        }
        for (Map.Entry<String, EmailConnectionPool> pool : connectionPoolMap.entrySet()){
            try {
                pool.getValue().close();
            } catch (EmailConnectionPoolException e) {
                log.error(format("Failed to clear connection pool for %s.", pool.getKey()), e);
            }
        }
    }

}
