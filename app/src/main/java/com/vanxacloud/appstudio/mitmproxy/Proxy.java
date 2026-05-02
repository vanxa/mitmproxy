package com.vanxacloud.appstudio.mitmproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Proxy {
    private final Logger log;
    private final int port;


    private Proxy(Builder builder) {
        this.port = builder.port;
        this.log = LoggerFactory.getLogger(String.format("Proxy [port=%d]", port));
    }

    public void listen() {
        log.info("Proxy listening");
    }


    public static class Builder {

        private int port;

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Proxy build() {
            return new Proxy(this);
        }
    }
}
