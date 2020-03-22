package com.ethan.socket.netty.fixlength;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author Administrator
 * 
 * TCP拆包粘包问题解决 - 消息定长
 * 
 *
 */
public class Server {
	
	final static int UNIT = 1024;
	final static int PORT = 8765;
	
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
			
			channelFuture1.channel().closeFuture().sync(); //等待关闭

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

				//消息定长，5个字符,不够5个的，需要用空格补全，达到5个就发送
				socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(5));
				
				//设置字符串形式的解码
				socketChannel.pipeline().addLast(new StringDecoder());
				
				//设置超时，自动断开连接
				socketChannel.pipeline().addLast(new ReadTimeoutHandler(5));
				
				//配置具体数据接收方法的处理ServerHandler
				//完成业务与Netty 配置的分离
				socketChannel.pipeline().addLast(new ServerHandler());
		}
		
	}
}
