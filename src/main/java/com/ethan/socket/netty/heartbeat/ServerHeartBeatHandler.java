package com.ethan.socket.netty.heartbeat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Administrator
 *
 */
public class ServerHeartBeatHandler extends ChannelHandlerAdapter {

	private static HashMap<String, String> AUTH_IP_MAP = 
			new HashMap<String, String>(); //存储客户端的IP与key值对应关系
	
	private static final String SUCCESS_KEY = "auth_success_key"; //成功
	
	static {
		String ip = null;
		try {
			//由于测试时，客户端和服务器端是一台机器，暂时先用这个相同的IP
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		print("server ip: "+ip);
		AUTH_IP_MAP.put(ip, "1234");//初始化客户端的token
	}
	
	boolean auth (ChannelHandlerContext ctx, Object msg) {
		String[] ret = ((String)msg).split(",");
		String auth = AUTH_IP_MAP.get(ret[0]);
		
		if (auth!=null && auth.equals(ret[1])) {//token验证通过
			ctx.channel().writeAndFlush(SUCCESS_KEY);
			return true;
		} else {
			//验证失败
			ctx.channel().writeAndFlush("auth failed!").addListener(ChannelFutureListener.CLOSE);
			return false;
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		
		if(msg instanceof String) {//第一次是String类型的验证
			auth(ctx, msg);
		}
		
		else if(msg instanceof MsgInfo) {//客户端心跳包
			MsgInfo info = (MsgInfo)msg;
			
			print("----------主机信息--------------");
			print("       主机IP: "+ info.getIp());
			print("主机CPU使用情况: ");
			
			Map<String, Object> cpu = info.getCpuMap();//获取cpu信息
			Map<String, Object> mem = info.getMemoryMap();//获取内存信息
			
			print(" 总使用率: "+ cpu.get("combined"));
			print("用户使用率: "+ cpu.get("user"));
			print("系统使用率: "+ cpu.get("sys"));
			print("   等待率: "+ cpu.get("wait"));
			print("   空闲率: "+ cpu.get("idle"));
			
			print("主机内存使用情况: ");
			
			print("    内存总量: "+ mem.get("total"));
			print("当前内存使用量: "+ mem.get("used"));
			print("当前内存剩余量: "+ mem.get("free"));
			print("----------------------------");
			
			ctx.channel().writeAndFlush("server " + ctx.channel().localAddress() + " has received the message.");
			
		} else {
			ctx.writeAndFlush("connection failed!").addListener(ChannelFutureListener.CLOSE);
		}
		
	}
	
	static void  print(Object o) {
		System.out.println(o);
	}
}