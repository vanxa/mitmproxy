package com.vanxacloud.appstudio.mitmproxy.server.proxy.handler;

import org.eclipse.jetty.client.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.proxy.ProxyHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class InterceptingProxyHandler extends ProxyHandler {

    private static final Logger log = LoggerFactory.getLogger(InterceptingProxyHandler.class);

    @Override
    public boolean handle(Request clientToProxyRequest, Response proxyToClientResponse, Callback proxyToClientCallback) {
        for(Connector connector : getServer().getConnectors()) {
            if(connector instanceof ServerConnector serverConnector) {
                HttpURI uri = clientToProxyRequest.getHttpURI();
                if(serverConnector.getPort() == uri.getPort() && Arrays.asList("localhost", "::1", serverConnector.getHost()).contains(uri.getHost())) {
                    log.warn("Request destination unknown. Unable to figure out where to forward request to.");
                    Response.writeError(clientToProxyRequest, proxyToClientResponse, null, HttpStatus.BAD_GATEWAY_502);
                    return false;
                }
            }
        }

        return super.handle(clientToProxyRequest, proxyToClientResponse, proxyToClientCallback);
    }

    @Override
    protected HttpURI rewriteHttpURI(Request clientToProxyRequest) {
        return clientToProxyRequest.getHttpURI();
    }

    protected class ProxyResponseListener extends Callback.Completable implements org.eclipse.jetty.client.Response.Listener
    {
        private final Request clientToProxyRequest;
        private final org.eclipse.jetty.client.Request proxyToServerRequest;
        private final Response proxyToClientResponse;
        private final Callback proxyToClientCallback;

        public ProxyResponseListener(Request clientToProxyRequest, org.eclipse.jetty.client.Request proxyToServerRequest, Response proxyToClientResponse, Callback proxyToClientCallback)
        {
            super(InvocationType.NON_BLOCKING);
            this.clientToProxyRequest = clientToProxyRequest;
            this.proxyToServerRequest = proxyToServerRequest;
            this.proxyToClientResponse = proxyToClientResponse;
            this.proxyToClientCallback = proxyToClientCallback;
        }

        @Override
        public void onBegin(org.eclipse.jetty.client.Response serverToProxyResponse)
        {
            proxyToClientResponse.setStatus(serverToProxyResponse.getStatus());
        }

        @Override
        public void onHeaders(org.eclipse.jetty.client.Response serverToProxyResponse)
        {
            if (log.isDebugEnabled())
            {
                log.debug("""
                        {} S2P received response
                        {}
                        {}""",
                        requestId(clientToProxyRequest),
                        serverToProxyResponse,
                        serverToProxyResponse.getHeaders());
            }
            for (HttpField serverToProxyResponseField : serverToProxyResponse.getHeaders())
            {
                if (HOP_HEADERS.contains(serverToProxyResponseField.getHeader()))
                    continue;
                HttpField newField = filterServerToProxyResponseField(serverToProxyResponseField);
                if (newField == null)
                    continue;
                proxyToClientResponse.getHeaders().add(newField);
            }
            if (log.isDebugEnabled())
            {
                log.debug("""
                        {} P2C sending response
                        {}
                        {}""",
                        requestId(clientToProxyRequest),
                        proxyToClientResponse,
                        proxyToClientResponse.getHeaders());
            }
        }

        @Override
        public void onContent(org.eclipse.jetty.client.Response serverToProxyResponse, Content.Chunk serverToProxyChunk, Runnable serverToProxyDemander)
        {
            ByteBuffer serverToProxyContent = serverToProxyChunk.getByteBuffer();
            if (log.isDebugEnabled())
                log.debug("{} S2P received content {}", requestId(clientToProxyRequest), BufferUtil.toDetailString(serverToProxyContent));

            serverToProxyChunk.retain();
            Callback callback = new Callback()
            {
                @Override
                public void succeeded()
                {
                    if (log.isDebugEnabled())
                        log.debug("{} P2C succeeded to write content {}", requestId(clientToProxyRequest), BufferUtil.toDetailString(serverToProxyContent));
                    serverToProxyChunk.release();
                    serverToProxyDemander.run();
                }

                @Override
                public void failed(Throwable failure)
                {
                    if (log.isDebugEnabled())
                        log.debug("{} P2C failed to write content {}", requestId(clientToProxyRequest), BufferUtil.toDetailString(serverToProxyContent), failure);
                    serverToProxyChunk.release();
                    // Cannot write towards the client, abort towards the server.
                    serverToProxyResponse.abort(failure);
                }

                @Override
                public InvocationType getInvocationType()
                {
                    return InvocationType.NON_BLOCKING;
                }
            };

            proxyToClientResponse.write(false, serverToProxyContent, callback);
        }

        @Override
        public void onSuccess(org.eclipse.jetty.client.Response serverToProxyResponse)
        {
            proxyToClientResponse.write(true, BufferUtil.EMPTY_BUFFER, this);
        }

        @Override
        public void onComplete(Result result)
        {
            if (result.isSucceeded())
            {
                // Wait for the last write to complete.
                whenComplete((r, failure) ->
                {
                    if (failure == null)
                    {
                        if (log.isDebugEnabled())
                            log.debug("{} P2C response complete {}", requestId(clientToProxyRequest), proxyToClientResponse);
                        onProxyToClientResponseComplete(clientToProxyRequest, proxyToServerRequest, result.getResponse(), proxyToClientResponse, proxyToClientCallback);
                    }
                    else
                    {
                        if (log.isDebugEnabled())
                            log.debug("{} P2C response failure {}", requestId(clientToProxyRequest), proxyToClientResponse, failure);
                        onProxyToClientResponseFailure(clientToProxyRequest, proxyToServerRequest, result.getResponse(), proxyToClientResponse, proxyToClientCallback, failure);
                    }
                });
            }
            else
            {
                if (log.isDebugEnabled())
                    log.debug("{} S2P failure {}", requestId(clientToProxyRequest), result.getResponse(), result.getFailure());
                onServerToProxyResponseFailure(clientToProxyRequest, proxyToServerRequest, result.getResponse(), proxyToClientResponse, proxyToClientCallback, result.getFailure());
            }
        }
    }
}