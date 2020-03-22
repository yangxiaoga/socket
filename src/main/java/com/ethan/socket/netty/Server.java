package com.ethan.socket.netty;



import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Administrator
 * 
 * ChannelOption.SO_BACKLOG：
 * 
 * 	服务器端TCP内核维护有2个队列：A和B
 * 	客户端向服务器端connect的时候，会发送带有SYN标志的包(第一次握手)
 *  服务器收到客户端发送的SYN时，向客户端发送SYN ACK 确认(第二次握手)
 *  此时TCP内核模块把客户端连接加入到A队列中，然后服务器收到客户端发来的ACK时(第三次握手)
 *  TCP内核模块把客户端连接从A队列移动到B队列，连接完成，应用程序的accept会返回
 *  也就是说accept从B队列中取出完成三次握手的连接
 *  A队列和B队列的长度之和是backlog,当A和B队列的长度之和大于backlog时，新连接会被TCP内核拒绝
 *  所以，如果backlog过小，可能会出现accept速度跟不上，A，B队列满了，导致新的客户端无法连接
 *  要注意的是：backlog对程序支持的连接并无影响，backlog影响的知识没有被accept取出的连接
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
		
		.childHandler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
				//配置具体数据接收方法的处理ServerHandler
				//完成业务与Netty 配置的分离
				ch.pipeline().addLast(new ServerHandler());
			}
		});
		
		try {
			ChannelFuture cf = serverBootstrap.bind(PORT).sync();//进行绑定
			cf.channel().closeFuture().sync(); //等待关闭
			
			pGroup.shutdownGracefully();
			cGroup.shutdownGracefully();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
