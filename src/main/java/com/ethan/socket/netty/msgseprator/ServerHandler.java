package com.ethan.socket.netty.msgseprator;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Administrator
 *
 */
public class ServerHandler extends ChannelHandlerAdapter{
	
	static String CHARSET = "UTF-8";
	
	@Override
	public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
		String body = (String)msg; 
		System.out.println("server: "+body);
		
		//服务器给客户端的响应
		String response = "Hello Client...$_"; //$_是规定的消息分隔符
		
		//Unpooled.copiedBuffer工具类返回ByteBuf
		channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(response.getBytes(CHARSET)));
		//.addListener(ChannelFutureListener.CLOSE);//ChannelFuture写完关闭连接
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}