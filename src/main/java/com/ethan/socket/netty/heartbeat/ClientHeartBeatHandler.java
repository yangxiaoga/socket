package com.ethan.socket.netty.heartbeat;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * @author Administrator
 *
 */
public class ClientHeartBeatHandler extends ChannelHandlerAdapter {
	
	private static final long UNIT = 2014L;
	
	private InetAddress addr;
	
	private ScheduledExecutorService scheduler = 
			Executors.newScheduledThreadPool(1); //线程池
	
	private ScheduledFuture<?> heartBeat;//A delayed result-bearing action that can be cancelled
	 
	private static final String AUTHON_SUCCESS_KEY = "auth_success_key"; //验证成功的标志
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress();
		
		System.out.println("client ip: "+ip);
		
		String key = "1234";
		String auth = ip + "," + key;//发送给服务端的证书：IP+KEY
		
		ctx.channel().writeAndFlush(auth);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try{
			if(msg instanceof String) {
				String retMsg = (String)msg;
				
				if (AUTHON_SUCCESS_KEY.equals(retMsg)) {//验证成功，发送心跳包(每2秒发一次)
					this.scheduler.scheduleWithFixedDelay(new HeartBeatTask(addr, ctx), 0, 2, TimeUnit.SECONDS);
				}
				
				System.out.println(retMsg);
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	
	//心跳任务
	private class HeartBeatTask implements Runnable {
		
		private final InetAddress addr; //地址
		
		private final ChannelHandlerContext ctx;
		
		/**
		 * addr 地址;
		 * ctx 通道上下文对象
		 * 
		 */
		public HeartBeatTask(final InetAddress addr, final ChannelHandlerContext ctx) {
			this.addr  = addr;
			this.ctx = ctx;
		}

		public void run() {
			
			try {
				MsgInfo info = new MsgInfo();
				info.setIp(addr.getHostAddress());//设置IP
				
				Sigar sigar = new Sigar();
				CpuPerc cpuPerc = sigar.getCpuPerc();//cpu
				Mem mem = sigar.getMem();    //内存
				
				Map<String, Object> cpuMap = new HashMap<String, Object>();
				cpuMap.put("combined", cpuPerc.getCombined());
				cpuMap.put("user", cpuPerc.getUser());
				cpuMap.put("sys", cpuPerc.getSys());
				cpuMap.put("wait", cpuPerc.getWait());
				cpuMap.put("idle", cpuPerc.getIdle());
				
				//memory
				Map<String, Object> memoryMap = new HashMap<String, Object>();
				memoryMap.put("total", mem.getTotal()/UNIT);
				memoryMap.put("used", mem.getUsed()/UNIT);
				memoryMap.put("free", mem.getFree()/UNIT);
				
				info.setCpuMap(cpuMap);
				info.setMemoryMap(memoryMap);
				
				ctx.channel().writeAndFlush(info);//写出到服务器
				
			} catch (SigarException e) {
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		cause.printStackTrace();
		if (heartBeat != null) {
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}
}