package com.ethan.socket.bio.hypocritical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 	处理类
 *  将客户端的socket封装成任务，装到任务队列中
 */
public class Task implements Runnable {

	private Socket socket;
	
	public Task(Socket socket) {
		
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
