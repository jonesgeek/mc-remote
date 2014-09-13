/**
 * 
 */
package com.jonesgeeks.mc.remote;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author will
 *
 */
public class ServerStatus {

	protected final String host;
    protected final int port;
    protected final int sessionId;
    protected final String baseUri;
    
    protected final Bootstrap b;
    private final EventLoopGroup workerGroup;
    private final ServerStatusHandler statusHandler;
    
    public ServerStatus(final String host, final int port, final int sessionId) {
    	this.host = host;
    	this.port = port;
    	this.sessionId = sessionId;
    	baseUri = "http://"+host+":"+port;
    	
    	statusHandler = new ServerStatusHandler();
    	
    	workerGroup = new NioEventLoopGroup();
    	
    	b = new Bootstrap();
        b.group(workerGroup)
         .channel(NioSocketChannel.class)
         .handler(new ServerStatusInitializer(statusHandler));
    }
    
    public void addListener(ServerStatusListener listener) {
    	statusHandler.addListener(listener);
    }
    
    public void getStatus() throws InterruptedException, URISyntaxException {
    	String s = String.format("%s/ctrl-int/1/playstatusupdate?revision-number=%d&session-id=%s", baseUri, 1, sessionId);
    	URI uri = new URI(s);
    	submitRequest(uri);
    }
    
    protected void submitRequest(URI uri) throws InterruptedException {
    	// Make the connection attempt.
        ChannelFuture ch = b.connect(host, port).sync();

        // Prepare the HTTP request.
        HttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
        request.headers().set(HttpHeaders.Names.HOST, host);
        request.headers().set(HttpHeaders.Names.ACCEPT, "*.*");
        request.headers().set("Viewer-Only-Client", 1);
        request.headers().set(HttpHeaders.Names.USER_AGENT, "MC Remote");
        
        ch.channel().writeAndFlush(request);
    }

}
