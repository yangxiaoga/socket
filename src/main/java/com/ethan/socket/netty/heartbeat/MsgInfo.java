package com.ethan.socket.netty.heartbeat;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Administrator
 * 
 * 客户端 - 服务器 传送消息
 *
 */
public class MsgInfo implements Serializable{
	
	private static final long serialVersionUID = 4430003066396237398L;
	private String ip; //ip地址
	private Map<String, Object> cpuMap;//cpu信息
	private Map<String, Object> memoryMap; //内存信息
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Map<String, Object> getCpuMap() {
		return cpuMap;
	}
	public void setCpuMap(Map<String, Object> cpuMap) {
		this.cpuMap = cpuMap;
	}
	public Map<String, Object> getMemoryMap() {
		return memoryMap;
	}
	public void setMemoryMap(Map<String, Object> memoryMap) {
		this.memoryMap = memoryMap;
	}
	
}
