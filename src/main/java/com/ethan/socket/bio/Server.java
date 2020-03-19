package com.ethan.socket.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器端
 * ServerSocket
 */
public class Server {
	final static int SERVER_PORT = 8765;
	
	public static void main(String[] args) {
		accept();
	}
	public static void accept() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(SERVER_PORT);
			System.out.println("Server start..");
			
			//进行阻塞
			Socket socket = server.accept();
			System.out.println("server socket: "+socket.hashCode());
			new Thread(new ServerHandler(socket)).start();
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
