package com.vanxacloud.appstudio.proximity.app.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public final class ProxyListener implements Runnable {


    public enum ProxyListenerState {
        INITIALIZING,
        INITIALIZED,
        LISTENING,
        STOPPING,
        STOPPED,
        CONFIGURED, CONFIGURING, UNKNOWN
    }

    private ProxyListenerState state = ProxyListenerState.STOPPED;

    private static final Logger log = LoggerFactory.getLogger(ProxyListener.class);
    private volatile boolean running;
    private final UUID id = UUID.randomUUID();

    @Override
    public void run() {
        log.info("Starting proxy");
        log.debug("Initializing listener");
        initialize();
        log.debug("Listener initialized. Configuring");
        configure();
        log.debug("Listener configured");

        running = true;
        setState(ProxyListenerState.LISTENING);
        while (!Thread.currentThread().isInterrupted() && running) {
            log.trace("Proxy is running");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void configure() {
        setState(ProxyListenerState.CONFIGURING);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        setState(ProxyListenerState.CONFIGURED);
    }

    private void initialize() {
        setState(ProxyListenerState.INITIALIZING);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        setState(ProxyListenerState.INITIALIZED);

    }

    public void stop() {
        log.info("Stopping proxy");
        running = false;
    }

    public String getId() {
        return id.toString();
    }

    public ProxyListenerState getState() {
        return state;
    }

    public void setState(ProxyListenerState state) {
        this.state = state;
    }
}
