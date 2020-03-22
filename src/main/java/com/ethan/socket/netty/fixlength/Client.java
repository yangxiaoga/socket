package com.ethan.socket.netty.fixlength;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author Administrator
 * TCP拆包粘包问题解决 - 消息定长
 */
public class Client {
	final static String IP = "192.168.1.2";
	final static int PORT = 8765;
	final static int CLIENT_UNIT = 1024;
	
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
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				
				//消息定长，5个字符,不够5个的，需要用空格补全，达到5个就发送
				socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(5));
				socketChannel.pipeline().addLast(new StringDecoder());
				socketChannel.pipeline().addLast(new ClientHandler());
			}
		});
		
		try {
			ChannelFuture channelFuture = bootstrap.connect(IP, PORT).sync();
			
			System.out.println("client start...");

			//$_消息分隔符
			channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer("aaaaa".getBytes()));
			Thread.sleep(1000);
			channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer("bbbbb".getBytes()));
			Thread.sleep(1000);
			channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer("ccccccc".getBytes()));
			
			//数据是存放在Buffer中的，flush不执行，则不会写入到管道中
			
			channelFuture.channel().closeFuture().sync();
			group.shutdownGracefully();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
