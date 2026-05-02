package com.vanxacloud.appstudio.proximity.app.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class Proxy {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final Logger log = LoggerFactory.getLogger(Proxy.class);
    private final Map<String, ProxyListener> proxies = new HashMap<>();

    public String startListener() {
        log.info("Starting listener");
        ProxyListener listener = new ProxyListener();
        proxies.put(listener.getId(), listener);
        executorService.submit(listener);
        log.info("Listener started");
        return listener.getId();
    }

    public ProxyListener.ProxyListenerState getListenerState(String id) {
        ProxyListener listener = proxies.get(id);
        if (listener == null) {
            return ProxyListener.ProxyListenerState.UNKNOWN;
        }
        return listener.getState();
    }

    public void stopAllListeners() {
        proxies.forEach((id, proxyListener) -> proxyListener.stop());
    }

    public void close() {
        try {
            log.debug("Shutting down thread pool");
            executorService.shutdown();
            log.info("Stopping proxy");
            stopAllListeners();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.warn("Interrupted while closing. Ignoring inerruption and proceeding with close");
        }
        log.info("Terminating");
        executorService.shutdownNow();
    }
}
