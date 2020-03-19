package com.ethan.socket.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

/**
 * @author Administrator
 *
 */
public class Client implements Runnable{
	
	final static String IP= "192.168.1.2";
	final static int PORT = 8765;
	
	final static String DEFAULT_CHARSET = "UTF-8";
	
	private AsynchronousSocketChannel asc;
	
	public Client() {
		try {
			this.asc = AsynchronousSocketChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void read(){
		ByteBuffer buf = ByteBuffer.allocate(1024); 
		try {
			asc.read(buf).get();
			buf.flip();
			byte[] resp = new byte[buf.remaining()];
			
			buf.get(resp);
			String msg = new String(resp, DEFAULT_CHARSET);
			System.out.println(msg);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	void write(String content){
		ByteBuffer buf;
		try {
			buf = ByteBuffer.wrap(content.getBytes(DEFAULT_CHARSET));
			asc.write(buf);
			read();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
	}
     void connect() {
		asc.connect(new InetSocketAddress(IP,PORT));
	}
	public static void main(String[] args) {
		Client c1 =new Client();
		Client c2 = new Client();
		Client c3 = new Client();
		c1.connect();
		c2.connect();
		c3.connect();
		
		new Thread(c1, "c1").start();
		new Thread(c2, "c2").start();
		new Thread(c3, "c3").start();
		
		c1.write("c1 msg");
		c2.write("c2 msg");
		c3.write("c3 msg");
		
	}
	
	public void run() {
		while(true) {
			
		}
	}
}
