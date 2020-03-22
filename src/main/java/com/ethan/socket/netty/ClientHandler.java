package com.ethan.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * @author Administrator
 *
 */
public class ClientHandler extends ChannelHandlerAdapter{
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		try{
			ByteBuf buf = (ByteBuf) msg;
			byte[] content = new byte[buf.readableBytes()];
			
			buf.readBytes(content);
			String body = new String(content, "UTF-8");
			
			System.out.println("client收到： "+body);
		} finally {
			//ByteBuf是一个引用计数对象，这个对象必须显示的调用release方法来释放，处理器的职责是
			//释放所有传递到处理器的引用计数对象
			ReferenceCountUtil.release(msg);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
	
	
	
	
	
}
