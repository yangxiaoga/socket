package com.ethan.socket.bio.hypocritical;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 伪异步服务器端
 * hypocritical synchronous
 * ServerSocket
 */
public class Server {
	final static int SERVER_PORT = 8765;
	static ExecutorService pool = Executors.newFixedThreadPool(20);
	
	public static void main(String[] args) {
		accept();
	}
	public static void accept() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(SERVER_PORT);
			System.out.println("Server start..");
			
			while(true) {
				//进行阻塞
				Socket socket = server.accept();
				System.out.println("server socket: "+socket.hashCode());
				
				//提交到线程池中
				pool.submit(new Task(socket));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
 }
