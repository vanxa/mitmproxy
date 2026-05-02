package com.vanxacloud.mitmproxy;

public class Proxy {
    private final int port;


    private Proxy(Builder builder) {
        this.port = builder.port;
    }





    public static class Builder {

        private int port;

        public Builder port(int port) {
            this.port = port;
            return this;
        }
    }
}
