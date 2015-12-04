package com.github.loafer.timer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zhaojh.
 */
public class TimerEncoder extends MessageToByteEncoder<UnixTime>{
    @Override
    protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) throws Exception {
        out.writeInt(msg.value());
    }
}
