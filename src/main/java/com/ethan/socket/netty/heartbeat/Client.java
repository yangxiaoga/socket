package com.ethan.socket.netty.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Administrator
 * 
 * 客户端
 */
public class Client {
	
	private static final int PORT = 8765;
	private static final String IP = "192.168.1.2";
	
	public static void main(String[] args) throws InterruptedException {
		new Client().connect();
	}
	void connect() throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
		.channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				socketChannel.pipeline().addLast(MarshallingCodeFactory.buildMarshallingDecoder());
				socketChannel.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder());
				socketChannel.pipeline().addLast(new ClientHeartBeatHandler());
			}
		});
		
		ChannelFuture channelFuture = bootstrap.connect(IP, PORT).sync();
		
		channelFuture.channel().closeFuture().sync();
		group.shutdownGracefully();
	}
}
