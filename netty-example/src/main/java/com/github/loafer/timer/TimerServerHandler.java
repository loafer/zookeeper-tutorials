package com.github.loafer.timer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author zhaojh.
 */
public class TimerServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
//        ByteBuf time = ctx.alloc().buffer(4);
//        time.writeInt((int) (System.currentTimeMillis()/1000L + 2208988800L));
//
//        final ChannelFuture f = ctx.writeAndFlush(time);
//        //关闭通道
//        f.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if(f == future){
//                    ctx.close();
//                }
//            }
//        });


        ChannelFuture f = ctx.writeAndFlush(new UnixTime());
        f.addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
