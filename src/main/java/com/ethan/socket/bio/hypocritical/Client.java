package com.ethan.socket.bio.hypocritical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 客户端
 * Client
 */
public class Client {
	
	final static String SERVER_ADDR = "192.168.1.2";
	final static int SERVER_PORT = 8765;
	
	public static void main(String[] args) {
		request();
	}
	
	public static void request() {
		Socket socket = null;
		BufferedReader reader = null;
		PrintWriter writer = null;
		
		try {
			socket = new Socket(SERVER_ADDR, SERVER_PORT);
			System.out.println("client socket: "+socket.hashCode());
			
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			writer.println("a message from client");
			String response = reader.readLine();
			
			System.out.println("client接收数据: "+response);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
