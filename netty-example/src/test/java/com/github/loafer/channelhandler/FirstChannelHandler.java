package com.github.loafer.channelhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author zhaojh.
 */
public class FirstChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            System.out.println("FirstChannelHandler received: " + msg);
            ctx.fireChannelRead(msg);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
