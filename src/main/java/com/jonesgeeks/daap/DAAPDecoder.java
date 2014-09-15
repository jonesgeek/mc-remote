/**
 * 
 */
package com.jonesgeeks.daap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author will
 *
 */
@Sharable
public class DAAPDecoder extends SimpleChannelInboundHandler<HttpObject> {
	private static final Logger LOG = LoggerFactory.getLogger(DAAPDecoder.class);

	/*
	 * 
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		
		if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            Response res = ResponseParser.performParse(new ByteBufInputStream(content));
            ctx.fireChannelRead(res);
		} else {
			ctx.fireChannelRead(msg);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
