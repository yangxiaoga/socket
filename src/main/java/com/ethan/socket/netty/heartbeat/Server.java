package com.ethan.socket.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Administrator
 * 
 *  心跳检测 Server端
 *  应用Jboss marshalling 序列化组件和netty通信，做心跳检测
 *	
 */
public class Server {
	
	private static final int UNIT = 1024; //默认单元大小
	private static final int PORT = 8765; //监听端口
	private static final String IP = "192.168.1.2"; //服务器所在IP
	
	public static void main(String[] args) throws InterruptedException {
		new Server().config();
	}
	
	//通道配置
	public void config() throws InterruptedException {
		EventLoopGroup parentGroup = new NioEventLoopGroup();//接收连接的线程组
		EventLoopGroup childGroup = new NioEventLoopGroup(); //处理消息的线程组
		
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(parentGroup, childGroup)
						.channel(NioServerSocketChannel.class)
						.option(ChannelOption.SO_BACKLOG, UNIT)
						//Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。
	                    // 可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
	                    .childOption(ChannelOption.SO_KEEPALIVE, true)
						.handler(new LoggingHandler(LogLevel.INFO))//设置日志
						.childHandler(new ChildChannelHandler());
		
		ChannelFuture  channelFuture = serverBootstrap.bind(IP, PORT).sync();
		System.out.println("server started....");
		channelFuture.channel().closeFuture().sync();
		 
		parentGroup.shutdownGracefully();
		childGroup.shutdownGracefully();
	}
	
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(MarshallingCodeFactory.buildMarshallingDecoder());
			ch.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder());
			ch.pipeline().addLast(new ServerHeartBeatHandler());
		}
		
	}
}
