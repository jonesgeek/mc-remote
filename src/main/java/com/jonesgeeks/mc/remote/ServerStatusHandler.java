/**
 * 
 */
package com.jonesgeeks.mc.remote;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jonesgeeks.daap.Response;
import com.jonesgeeks.daap.ResponseParser;

/**
 * @author will
 *
 */
@Sharable
public class ServerStatusHandler extends SimpleChannelInboundHandler<HttpObject> {
	private static final Logger LOG = LoggerFactory.getLogger(ServerStatusHandler.class);
	
	private Collection<ServerStatusListener> listeners = new ArrayList<>();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		
		for( ServerStatusListener listener : listeners ) {
			if (msg instanceof HttpContent) {
	            HttpContent httpContent = (HttpContent) msg;

	            ByteBuf content = httpContent.content();
	            Response res = ResponseParser.performParse(new ByteBufInputStream(content));
	            
	            listener.serverSatusReceived(res);
			}
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void addListener(ServerStatusListener listener) {
		this.listeners.add(listener);
	}

}
