package com.ethan.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 客户端
 * @author Administrator
 *
 */
public class Client {
	
	final static String IP = "192.168.1.2";
	final static int PORT = 8765;
	
	public static void main(String[] args) {
		connect();
	}
	
	static void connect() {//连接Server
		InetSocketAddress addr = new InetSocketAddress(IP, PORT);
		SocketChannel channel = null;//声明通道
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		try {
			channel = SocketChannel.open();//获取通道
			channel.connect(addr);//完成注册
			
			while(true) {
				byte[] content = new byte[1024];
				System.in.read(content);//读取控制台内容
				
				buf.put(content);//数据放到缓冲区
				buf.flip();      //复位
				channel.write(buf);
				buf.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
