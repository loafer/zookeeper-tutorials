package com.github.loafer.channelhandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author zhaojh.
 */
public class SecondChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ByteBuf msg) throws Exception {
        System.out.println("SecondChannelHandler received: " + ByteBufUtil.hexDump(msg.readBytes(msg.readableBytes())));
        ctx.fireChannelRead(msg);
    }
}
