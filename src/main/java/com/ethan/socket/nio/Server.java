package com.ethan.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 服务器端
 * @author Administrator
 *
 */
public class Server implements Runnable{
	
	//多路复用器，管理通道
	private Selector selector;
	final static String DEFAULT_CHARSET = "UTF-8";
	
	/**缓冲区*/
	private ByteBuffer readeBuf = ByteBuffer.allocate(1024);
	//private ByteBuffer writeBuf = ByteBuffer.allocate(1024);
	
	public Server (int port) {
		try {
			//打开多路复用器
			this.selector = Selector.open();
			
			//打开服务器通道 
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);//非阻塞
			serverSocketChannel.bind(new InetSocketAddress(port));
			
			//服务器通道注册到多路复用器上，并且监听阻塞事件
			serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server started..."+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void start() {
		new Thread(new Server(8765)).start();
	}
	
	public static void main(String[] args) {
		start();
	}
	public void run() {
		while(true) {
			try {
				this.selector.select();//开始监听
				Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
				
				while(keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();
					
					if (key.isValid()) {//key有效
						if (key.isAcceptable()) {//如果为阻塞状态
							accept(key);
						}
						
						if (key.isReadable()) {
							read(key);
						}
						if (key.isWritable()) {
							write(key);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写方法
	 * @param key
	 */
	private void write(SelectionKey key) {
		
	}

	/**
	 * 读取方法
	 * @param key
	 */
	private void read(SelectionKey key) {
		this.readeBuf.clear();
		SocketChannel socketChannel = (SocketChannel)key.channel();
		int count;
		try {
			count = socketChannel.read(readeBuf);
			if (count == -1) {
				key.channel().close();
				key.cancel();
				return;
			}
			
			this.readeBuf.flip();//有数据进行读取，读取之前需要复位方法(把position和limit进行复位)
			byte[] receive = new byte[this.readeBuf.remaining()];
			this.readeBuf.get(receive); //将缓冲区数据写入接收的byte数组
			String content = new String(receive, DEFAULT_CHARSET).trim();
			System.out.println(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 阻塞方法
	 * @param key
	 */
	private void accept(SelectionKey key) {
		
		ServerSocketChannel serverSocketChannel = 	(ServerSocketChannel)key.channel();//获取服务通道
		try {
			
			SocketChannel socketChannel = serverSocketChannel.accept(); //通过ServerSocketChannel执行阻塞方法(等待客户端的通道)
			socketChannel.configureBlocking(false);
			socketChannel.register(this.selector, SelectionKey.OP_READ);//注册到多路复用器，并设置读取标识
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
