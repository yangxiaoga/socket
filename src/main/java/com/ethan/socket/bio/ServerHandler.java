package com.ethan.socket.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 	ServerHandler处理类
 *
 */
public class ServerHandler implements Runnable {

	private Socket socket;
	
	public ServerHandler(Socket socket) {
		
		this.socket = socket;
	}
	public void run() {
		
		BufferedReader reader = null;
		PrintWriter writer = null;
		
		try {
			
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			String content = reader.readLine();	
			System.out.println("server接收内容："+content);
			
			writer.println("from server 消息收到,您已连接...");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket!= null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}

	}

}
