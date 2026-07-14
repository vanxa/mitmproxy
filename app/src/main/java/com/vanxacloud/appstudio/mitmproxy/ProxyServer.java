package com.vanxacloud.appstudio.mitmproxy;

import com.vanxacloud.appstudio.mitmproxy.server.proxy.handler.InterceptingProxyHandler;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.proxy.ProxyHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyServer {
    private static final String DEFAULT_HOST = "localhost";

    private final Logger log;
    private final int port;
    private final String host;

    private ProxyServer(Builder builder) {
        this(builder.host, builder.port);
    }


    private ProxyServer() {
        this(DEFAULT_HOST, 8080);
    }

    private ProxyServer(int port) {
        this(DEFAULT_HOST, port);
    }

    private ProxyServer(String host, int port) {
        this.port = port;
        this.host = StringUtils.isEmpty(host) ? DEFAULT_HOST : host;
        this.log = LoggerFactory.getLogger(String.format("Proxy [%s:%d]", host, port));
        this.log.info("Server configured");
    }

    public void start() {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(this.host);
        connector.setPort(this.port);
        server.addConnector(connector);


        ProxyHandler handler = new InterceptingProxyHandler();
        server.setHandler(handler);


        try {
            server.start();
            server.join();
        } catch (Exception e) {
            log.error("Caught exception while starting proxy server", e);
            throw new RuntimeException(e);
        }

    }

    public static class Builder {

        private String host;
        private int port;

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public ProxyServer build() {
            return new ProxyServer(this);
        }
    }
}
