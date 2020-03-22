package com.ethan.socket.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Administrator
 *
 */
public class Client {
	final static String IP = "192.168.1.2";
	final static int PORT = 8765;
	
	public static void main(String[] args) {
		new Client().connect();
	}
	void connect() {
		EventLoopGroup group = new NioEventLoopGroup();//用于接收客户端连接
		Bootstrap bootstrap = new Bootstrap();//辅助工具类，用于服务器通道的一系列配置
		
		bootstrap.group(group)    //指定一个线程组
		.channel(NioSocketChannel.class)   //指定NIO模式
		.handler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ClientHandler());
			}
		});
		
		try {
			ChannelFuture cf = bootstrap.connect(IP, PORT).sync();
			
			System.out.println("client start...");
			
			cf.channel().writeAndFlush(Unpooled.copiedBuffer("Hello Server..".getBytes()));
			Thread.sleep(1000);
			cf.channel().writeAndFlush(Unpooled.copiedBuffer("Hello Server...".getBytes()));
			Thread.sleep(1000);
			cf.channel().writeAndFlush(Unpooled.copiedBuffer("Hello Server....".getBytes()));
			
			cf.channel().closeFuture().sync();
			group.shutdownGracefully();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
