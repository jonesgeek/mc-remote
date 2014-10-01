/**
 * 
 */
package com.jonesgeeks.mc.remote.itunes.status;

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
import java.util.Collection;

import com.jonesgeeks.dacp.Session;
import com.jonesgeeks.mc.remote.ServerStatus;
import com.jonesgeeks.mc.remote.ServerStatusListener;

/**
 * @author will
 *
 */
public class ServerStatusPoller {

    protected final int sessionId;
    protected final Session session;
    protected final String baseUri;
    
    protected final Bootstrap b;
    private final EventLoopGroup workerGroup;
    private final ServerStatusHandler statusHandler;
    
    public ServerStatusPoller(final Session session, final Collection<ServerStatusListener> statusListeners) {
    	this.sessionId = session.getSessionId();
    	baseUri = "http://"+session.getHost()+":"+session.getPort();
    	this.session = session;
    	
    	statusHandler = new ServerStatusHandler(statusListeners);
    	statusListeners.add(new ServerStatusListener() {
    		
    		/*
    		 * (non-Javadoc)
    		 * @see com.jonesgeeks.mc.remote.ServerStatusListener#serverSatusReceived(com.jonesgeeks.mc.remote.ServerStatus)
    		 */
			@Override
			public void serverSatusReceived(ServerStatus status) {
				try {
					getStatus(status.getRevision());
				} catch (InterruptedException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
    	
    	workerGroup = new NioEventLoopGroup();
    	
    	b = new Bootstrap();
        b.group(workerGroup)
         .channel(NioSocketChannel.class)
         .handler(new ServerStatusInitializer(statusHandler));
        
        try {
			getStatus(1);
		} catch (InterruptedException | URISyntaxException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 
     * @param revision
     * @throws InterruptedException
     * @throws URISyntaxException
     */
    protected void getStatus(long revision) throws InterruptedException, URISyntaxException {
    	String s = String.format("%s/ctrl-int/1/playstatusupdate?revision-number=%d&session-id=%s", baseUri, revision, sessionId);
    	URI uri = new URI(s);
    	submitRequest(uri);
    }
    
    /**
     * 
     * @param uri
     * @throws InterruptedException
     */
    protected void submitRequest(URI uri) throws InterruptedException {
    	// Make the connection attempt.
        ChannelFuture ch = b.connect(session.getHost(), session.getPort()).sync();

        // Prepare the HTTP request.
        HttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
        request.headers().set(HttpHeaders.Names.HOST, session.getHost());
        request.headers().set(HttpHeaders.Names.ACCEPT, "*.*");
        request.headers().set("Viewer-Only-Client", 1);
        request.headers().set(HttpHeaders.Names.USER_AGENT, "MC Remote");
        
        ch.channel().writeAndFlush(request);
    }

}
