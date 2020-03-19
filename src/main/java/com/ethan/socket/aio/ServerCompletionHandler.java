package com.ethan.socket.aio;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

/**
 * ServerCompletionHandler()处理客户端异步Channel连接
 * @author Administrator
 * 
 * 
 * CompletionHandler<AsynchronousSocketChannel, Server>
 *
 */
public class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Server> {

	public void completed(AsynchronousSocketChannel asc, Server attachment) {
		//当有下一个客户端接入的时候，直接调用Server的accept方法，这样反复，保证多个客户端可以阻塞
		attachment.getAssc().accept(attachment,this);
		read(asc);
	}

	/**
	 * @param asc
	 */
	private void read(final AsynchronousSocketChannel asc) {
		//读取数据
		ByteBuffer buf = ByteBuffer.allocate(1024);
		asc.read(buf, buf, new CompletionHandler<Integer,ByteBuffer>(){
			
			//异步的，读完之后
			public void completed(Integer resultSize, ByteBuffer attachment) {
				attachment.flip();//读取之后，重置标志位
				System.out.println("收到客户端的长度： "+resultSize);
				
				String resultData = new String(attachment.array()).trim();
				System.out.println("收到客户端的信息： "+resultData);
				
				String response = "服务器端响应: "+resultData;
				write(asc, response);
			}

			private void write(AsynchronousSocketChannel asc, String response) {
				
				try {
					ByteBuffer buf = ByteBuffer.allocate(1024);
					buf.put(response.getBytes("UTF-8"));
					buf.flip();
					asc.write(buf).get();//开启了一个新的线程去执行写操作，返回值是一个Future，异步写
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch(UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			public void failed(Throwable exc, ByteBuffer attachment) {
				exc.printStackTrace();
			}
			
		});
	}

	public void failed(Throwable exc, Server attachment) {
		exc.printStackTrace();
	}

}
