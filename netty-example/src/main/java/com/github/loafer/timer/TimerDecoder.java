package com.github.loafer.timer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zhaojh.
 */
public class TimerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<4){
            return;
        }

//        out.add(in.readBytes(4));
        out.add(new UnixTime(in.readInt()));
    }
}
