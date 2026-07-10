package com.vanxacloud.appstudio.mitmproxy.server.proxy.handler;

import org.eclipse.jetty.client.Result;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.proxy.ProxyHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class InterceptingProxyHandler extends ProxyHandler.Forward {

    private static final Logger log = LoggerFactory.getLogger(InterceptingProxyHandler.class);

    @Override
    public boolean handle(Request clientToProxyRequest, Response proxyToClientResponse, Callback proxyToClientCallback) {
        for (Connector connector : getServer().getConnectors()) {
            if (connector instanceof ServerConnector serverConnector) {
                HttpURI uri = clientToProxyRequest.getHttpURI();
                if (serverConnector.getPort() == uri.getPort() && Arrays.asList("localhost", "::1", serverConnector.getHost()).contains(uri.getHost())) {
                    log.warn("Request destination unknown. Unable to figure out where to forward request to.");
                    Response.writeError(clientToProxyRequest, proxyToClientResponse, null, HttpStatus.BAD_GATEWAY_502);
                    return false;
                }
            }
        }


        return super.handle(clientToProxyRequest, proxyToClientResponse, proxyToClientCallback);
    }

    @Override
    protected org.eclipse.jetty.client.Response.CompleteListener newServerToProxyResponseListener(Request clientToProxyRequest, org.eclipse.jetty.client.Request proxyToServerRequest,
                                                                                                  Response proxyToClientResponse, Callback proxyToClientCallback) {
        return new InterceptingProxyResponseListener(clientToProxyRequest, proxyToServerRequest, proxyToClientResponse, proxyToClientCallback);
    }

    protected org.eclipse.jetty.client.Request newProxyToServerRequest(Request clientToProxyRequest, HttpURI newHttpURI) {
        log.info("{} {}", clientToProxyRequest.getMethod(), newHttpURI);
        return super.newProxyToServerRequest(clientToProxyRequest, newHttpURI);
    }

    @Override
    protected void copyRequestHeaders(Request clientToProxyRequest, org.eclipse.jetty.client.Request proxyToServerRequest) {
        clientToProxyRequest.getHeaders().forEach(httpField -> log.info(httpField.toString()));
        super.copyRequestHeaders(clientToProxyRequest, proxyToServerRequest);
    }

    @Override
    protected void addProxyHeaders(Request clientToProxyRequest, org.eclipse.jetty.client.Request proxyToServerRequest) {
        // Remove the proxy-specific headers as we don't need them
    }

    @Override
    protected org.eclipse.jetty.client.Request.Content newProxyToServerRequestContent(Request clientToProxyRequest, Response proxyToClientResponse, org.eclipse.jetty.client.Request proxyToServerRequest) {
        return super.newProxyToServerRequestContent(clientToProxyRequest, proxyToClientResponse, proxyToServerRequest);
    }

    protected class InterceptingProxyResponseListener extends ProxyResponseListener {

        public InterceptingProxyResponseListener(Request clientToProxyRequest, org.eclipse.jetty.client.Request proxyToServerRequest, Response proxyToClientResponse, Callback proxyToClientCallback) {
            super(clientToProxyRequest, proxyToServerRequest, proxyToClientResponse, proxyToClientCallback);
        }

        @Override
        public void onHeaders(org.eclipse.jetty.client.Response serverToProxyResponse) {
            serverToProxyResponse.getHeaders().forEach(httpField -> log.info(httpField.toString()));
            super.onHeaders(serverToProxyResponse);

            // Modify serverToProxyResponse headers when injection rules are set
        }

        @Override
        public void onContent(org.eclipse.jetty.client.Response serverToProxyResponse, Content.Chunk serverToProxyChunk, Runnable serverToProxyDemander) {
            // Create a duplicate view
            ByteBuffer view = serverToProxyChunk.getByteBuffer().duplicate();
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(view);
            String str = charBuffer.toString();

            log.info("Content {}", str);
            super.onContent(serverToProxyResponse, serverToProxyChunk, serverToProxyDemander);
        }

        @Override
        public void onSuccess(org.eclipse.jetty.client.Response serverToProxyResponse) {
            log.info("Intercepted OnSuccess from Server");
            super.onSuccess(serverToProxyResponse);
        }

        @Override
        public void onComplete(Result result) {
            log.info("Intercepted OnComplete from Server");
            super.onComplete(result);
        }
    }
}