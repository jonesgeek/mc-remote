/**
 * 
 */
package com.jonesgeeks.daap;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import static com.jonesgeeks.daap.DAAPDecoderState.*;

/**
 * @author will
 *
 */
public class DAAPDecoder extends ReplayingDecoder<DAAPDecoderState> {

	public DAAPDecoder() {
		super(cmst);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int length;
		// Keep reading data as a chunk until the end of connection is reached.
        int toRead = actualReadableBytes();
        if (toRead > 0) {
            ByteBuf content = in.readBytes(toRead);
            if (in.isReadable()) {
                out.add(ResponseParser.performParse(new ByteBufInputStream(content)));
            }
        }

	}

}
