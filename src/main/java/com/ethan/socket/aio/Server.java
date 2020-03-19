package com.ethan.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Administrator
 *
 */
public class Server {
	private ExecutorService executorService;//线程池
	private AsynchronousChannelGroup threadGroup;//线程组
	private AsynchronousServerSocketChannel assc;//服务器通信
	
	public Server(int port) {
		
		try {
			
			executorService = Executors.newCachedThreadPool();//创建缓存池
			threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService,1);//创建线程组
			assc = AsynchronousServerSocketChannel.open(threadGroup);//创建服务器通道
			assc.bind(new InetSocketAddress(port)); //绑定端口
			
			//new ServerCompletionHandler()处理客户端异步Channel连接
			assc.accept(this,new ServerCompletionHandler()); 
			
			/*** 
			 * 由于是异步的，程序非阻塞，上面的accept会继续向下执行，为了让其不停止，让线程休眠
			 * AIO不阻塞就在这里了，代码可以直接往下去执行的
			 * */
			Thread.sleep(Integer.MAX_VALUE);
			
		} catch (IOException e) {
			e.printStackTrace();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public AsynchronousServerSocketChannel getAssc() {
		return assc;
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Server server = new Server(8765);
	}
}
