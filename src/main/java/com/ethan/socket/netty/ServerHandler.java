package com.ethan.socket.netty;

import io.netty.buffer.ByteBuf;
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
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf)msg; //消息默认是ByteBuf，ByteBuf既有读指针，又有写指针
		                            //最后不用手动释放的原因是，只要有写的操作，则会自动释放，不需要加ReferenceCountUtil.release(msg);
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);//读到req中
		
		String  body = new String(req, CHARSET);
		System.out.println("server: "+body);
		
		//服务器给客户端的响应
		String response = "Hello Client...";
		ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes(CHARSET)));//Unpooled.copiedBuffer工具类返回ByteBuf
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}