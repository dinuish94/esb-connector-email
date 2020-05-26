package org.wso2.carbon.connector.security;

import org.apache.commons.lang.ArrayUtils;
import scala.actors.threadpool.Arrays;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class CustomSSLSocketFactory extends SSLSocketFactory {

    private final SSLSocketFactory sslSocketFactory;
    private final String[] enabledCipherSuites;
    private final String[] enabledProtocols;
    private final String[] defaultCipherSuites;
    private static CustomSSLSocketFactory defaultSocketFactory = null;

    public CustomSSLSocketFactory(SSLContext sslContext, String[] enabledCipherSuites, String[] enabledProtocols) {

        this.sslSocketFactory = sslContext.getSocketFactory();
        if (enabledCipherSuites == null) {
            enabledCipherSuites = this.sslSocketFactory.getDefaultCipherSuites();
        }

        List<String> cipherSuites = Arrays.asList(enabledCipherSuites);

        cipherSuites.retainAll(Arrays.asList(this.sslSocketFactory.getSupportedCipherSuites()));
        cipherSuites.retainAll(Arrays.asList(this.sslSocketFactory.getDefaultCipherSuites()));

        this.enabledCipherSuites = cipherSuites.toArray(new String[0]);
        this.defaultCipherSuites = cipherSuites.toArray(new String[0]);

        if (enabledProtocols == null) {
            enabledProtocols = sslContext.getDefaultSSLParameters().getProtocols();
        }

        List<String> protocols = Arrays.asList(enabledProtocols);

        protocols.retainAll(Arrays.asList(sslContext.getSupportedSSLParameters().getProtocols()));

        this.enabledProtocols = protocols.toArray(new String[0]);
    }

    @Override
    public String[] getDefaultCipherSuites() {

        return this.defaultCipherSuites;
    }

    @Override
    public String[] getSupportedCipherSuites() {

        return this.enabledCipherSuites;
    }

    public Socket createSocket(String host, int port) throws IOException {
        return this.restrictCipherSuites((SSLSocket)this.sslSocketFactory.createSocket(host, port));
    }

    public Socket createSocket(String host, int port, InetAddress clientAddress, int clientPort) throws IOException {
        return this.restrictCipherSuites((SSLSocket)this.sslSocketFactory.createSocket(host, port, clientAddress, clientPort));
    }

    public Socket createSocket(InetAddress address, int port) throws IOException {
        return this.restrictCipherSuites((SSLSocket)this.sslSocketFactory.createSocket(address, port));
    }

    public Socket createSocket(InetAddress address, int port, InetAddress clientAddress, int clientPort) throws IOException {
        return this.restrictCipherSuites((SSLSocket)this.sslSocketFactory.createSocket(address, port, clientAddress, clientPort));
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return this.restrictCipherSuites((SSLSocket)this.sslSocketFactory.createSocket(socket, host, port, autoClose));
    }

    private SSLSocket restrictCipherSuites(SSLSocket socket) {
        socket.setEnabledCipherSuites(this.enabledCipherSuites);
        socket.setEnabledProtocols(this.enabledProtocols);
        return socket;
    }

}
