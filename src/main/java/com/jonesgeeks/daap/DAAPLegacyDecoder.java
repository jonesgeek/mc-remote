/**
 * 
 */
package com.jonesgeeks.daap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author will
 *
 */
public class DAAPLegacyDecoder extends SimpleChannelInboundHandler<HttpMessage> {
	private static final Logger LOG = LoggerFactory.getLogger(DAAPLegacyDecoder.class);
	
	public DAAPLegacyDecoder() {
		super();
		LOG.info("Initializing daap legacy decoder.");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg)
			throws Exception {
		if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            
            Response res = ResponseParser.performParse(new ByteBufInputStream(content));
            
            ctx.writeAndFlush(res);
		} else {
			ctx.write(msg);
		}
	}

}
