package com.ethan.socket.netty.heartbeat;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

/**
 * @author Administrator
 * 
 * 创建Marshalling编解码器
 */
public class MarshallingCodeFactory {
	
		private static final int UNIT = 1024;
		/**
	    * 创建Jboss Marshalling解码器MarshallingDecoder
	    * @return MarshallingDecoder
	    */
	    public static MarshallingDecoder buildMarshallingDecoder(){
	 
	        /**通过Marshalling工具类的静态方法
	    	获取Marshalling实例对象 参数为serial标识创建的是java序列化工厂对象*/
	        //final MarshallerFactory marshallerFactory =   
	        //	  Marshalling.getProvidedMarshallerFactory("serial");
	 
	        /**创建MarshallingConfiguration对象，配置版本号为5*/
	        //final MarshallingConfiguration configuration = 
	        //	  new MarshallingConfiguration();
	        //configuration.setVersion(5);
	 
	        /**根据marshallerFactory和configuration创建provider*/
	        //UnmarshallerProvider provider = 
	        //	  new DefaultUnmarshallerProvider(marshallerFactory, configuration);
	 
	        /**构建Netty的marshallingDecoder对象，两个参数分别为provider和单个消息序列化后的最大长度*/
	        //MarshallingDecoder decoder = 
	        //      new MarshallingDecoder(provider, UNIT*1);
	    
	        //return decoder;
	        
	        
	        
	        
	        
	        
	        // 首先通过Marshalling工具类的精通方法获取Marshalling实例对象 参数serial标识创建的是java序列化工厂对象。
	        final MarshallerFactory marshallerFactory = Marshalling
	                .getProvidedMarshallerFactory("serial");
	        // 创建了MarshallingConfiguration对象，配置了版本号为5
	        final MarshallingConfiguration configuration = new MarshallingConfiguration();
	        configuration.setVersion(5);
	        // 根据marshallerFactory和configuration创建provider
	        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(
	                marshallerFactory, configuration);
	        // 构建Netty的MarshallingDecoder对象，俩个参数分别为provider和单个消息序列化后的最大长度
	        MarshallingDecoder decoder = new MarshallingDecoder(provider,
	                UNIT);
	        return decoder;
	        
	        
	    
	        
	        
	    }
	 
	    /**
	    * 创建Jboss Marshalling编码器MarshallingEncoder
	    * @return MarshallingDecoder
	    */
	    public static MarshallingEncoder buildMarshallingEncoder(){
	 
	    	//MarshallingEncoder encoder = null;
	    	//try{
	        /**通过Marshalling工具类的静态方法
	         * 获取Marshalling实例对象 参数为serial标识创建的是java序列化工厂对象
	         */
	        //final MarshallerFactory marshallerFactory = 
	        //	  Marshalling.getProvidedMarshallerFactory("serial");
	 
	        /**创建MarshallingConfiguration对象，配置版本号为5*/
	        //final MarshallingConfiguration configuration = 
	        //		new MarshallingConfiguration();
	        //configuration.setVersion(5);
	 
	        /**根据marshallerFactory和configuration创建provider*/
	        //MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
	 
	        /**
	         * 构建Netty的marshallingDecoder对象，
	         * 用于将实现序列化接口的POJO对象序列化成二进制数组
	         */
	        //encoder = new MarshallingEncoder(provider);
	    	//} catch(Exception e) {
	    	//	e.printStackTrace();
	    	//}
	 
	        //return encoder;

	    	
	    	
	    	 final MarshallerFactory marshallerFactory = Marshalling
	                 .getProvidedMarshallerFactory("serial");
	         final MarshallingConfiguration configuration = new MarshallingConfiguration();
	         configuration.setVersion(5);
	         MarshallerProvider provider = new DefaultMarshallerProvider(
	                 marshallerFactory, configuration);
	         // 构建Netty的MarshallingEncoder对象，MarshallingEncoder用于实现序列化接口的POJO对象序列化为二进制数组
	         MarshallingEncoder encoder = new MarshallingEncoder(provider);
	         return encoder;

	    	
	    }

}
