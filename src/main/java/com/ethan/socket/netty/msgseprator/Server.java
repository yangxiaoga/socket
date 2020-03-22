package com.ethan.socket.netty.msgseprator;

import com.ethan.socket.netty.msgseprator.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author Administrator
 * 
 * TCP拆包粘包问题解决 - 消息分隔符$_
 * 
 *
 */
public class Server {
	
	final static int UNIT = 1024;
	final static int PORT = 8765;
	public static final String MSG_SEPRATOR = "$_" ;//消息分隔符
	
	public static void main(String[] args) {
		new Server().execute();
	}
	void execute() {
		EventLoopGroup pGroup = new NioEventLoopGroup();//用于接收客户端连接线程组
		EventLoopGroup cGroup = new NioEventLoopGroup();//用于进行网路通信线程组
		ServerBootstrap serverBootstrap = new ServerBootstrap();//辅助工具类，用于服务器通道的一系列配置
		
		serverBootstrap.group(pGroup, cGroup)    //指定两个线程组
		.channel(NioServerSocketChannel.class)   //指定NIO模式
		.option(ChannelOption.SO_BACKLOG, UNIT)  //设置TCP缓冲区
		.option(ChannelOption.SO_SNDBUF, 2*UNIT) //发送缓冲大小
		.option(ChannelOption.SO_RCVBUF, 32*UNIT)//接收缓冲大小
		.option(ChannelOption.SO_KEEPALIVE, true)//保持连接
		.childHandler(new ChildChannelHandler());
		
		try {
			ChannelFuture channelFuture1 = serverBootstrap.bind(PORT).sync();//进行绑定
			System.out.println("Server started..."+ PORT);
			
			//开放其他端口
			//ChannelFuture channelFuture2 = serverBootstrap.bind(PORT+1).sync();//进行绑定
			//System.out.println("Server started..."+ PORT+1);
			
			
			channelFuture1.channel().closeFuture().sync(); //等待关闭
			//channelFuture2.channel().closeFuture().sync(); //等待关闭

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//释放线程池资源
			pGroup.shutdownGracefully();
			cGroup.shutdownGracefully();
		}
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel socketChannel) throws Exception {

				//设置特殊分隔符
				ByteBuf sepratorBuf = Unpooled.copiedBuffer(MSG_SEPRATOR.getBytes());
				socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(UNIT, sepratorBuf));
				
				//设置字符串形式的解码
				socketChannel.pipeline().addLast(new StringDecoder());
				
				//配置具体数据接收方法的处理ServerHandler
				//完成业务与Netty 配置的分离
				socketChannel.pipeline().addLast(new ServerHandler());
		}
		
	}
}
