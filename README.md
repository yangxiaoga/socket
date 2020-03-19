# socket 网络编程
Socket_ServerSocket_BIO_NIO

1 什么是网络编程
	Java Socket/IO/NIO/AIO


2、netty

	Netty 是一个广泛使用的 Java 网络编程客户端/服务器框架
	API,原理，实际使用，序列化，编解码，TCP/IP

3、netty
	心跳检测的机制,文件传输，数据的传输
	长连接短连接

4、netty
	springboot spring 数据通信，自定义协议


5、Socket使用场景
	视频流
	打车工具
	游戏
	
6、Socket
	
	又称为套接字，向网络发出请求或者应答请求
	Socket和ServerSocket类位于java.net中，ServerSocket用于服务器端
	Socket是建立网络连接时使用

	在连接成功时，应用程序两端都会禅城Socket实例，操作这个实例，完成所需的会话
	对于一个网络连接来说，套接字是平等的，不因为在服务器端或客户端，而产生不同
	级别，不管是Socket还是ServerSocket，他们的工作都是通过SocketImpl类及其子类完成的

	连接的建立过程

		1、服务器监听：是服务器套接字，并不定位具体的客户端套接字
		   而是出于等待连接的状态，实时监控网络状态

		2、客户端请求：由客户端套接字提出请求，要连接的目标是服务器端的套接字，客户端的
		   套接字必须首先描述他要连接的服务器的套接字，指出服务器端套接字的地址和端口号
		   然后向服务器端套接字提出连接请求

		3、服务器连接确认：服务器套接字监听到或者说接收到客户端套接字的连接请求，服务器端
		   响应客户端套接字的请求，建立一个新的线程，把服务器端套接字的描述发给客户端	           

		4、客户端连接确认：一旦客户端确认了此描述连接就建立好了，双方开始通信，而服务器端
		   套接字继续处于监听状态，继续接收其他客户端套接字的连接请求

		

	
7、BIO和NIO的区别
	
	本质就是阻塞和非阻塞的区别

	阻塞
		应用程序在获取网略数据的时候，如果网络传输数据很慢，那么程序就会一直等待，直到传输完毕


	非阻塞
		应用程序直接可以获取已经准备就绪好的数据，无需等待

	BIO为同步阻塞形式
	NIO为同步非阻塞形式
	NIO并没有实现异步

	在JDK1.7之后，升级了NIO库包，支持异步非阻塞通信模型NIO2.0即AIO

	同步和异步
		同步和异步一般是面向操作系统与应用程序对IO操作的层面上来区别的

	同步
	应用程序会直接参与读写操作，并且我们的应用程序会字节阻塞到某一个方法上直到数据
	准备就绪，或者采用轮询的策略实时检查数据的就绪状态，如果就绪则获取数据

	异步
	所有的IO读写操作交给操作系统处理，与我们的应用系统没有直接关系，我们程序不关系IO读写
	当操作系统完成IO读写操作时，会给我们应用程序发送通知，我们应用程序直接拿走数据

	同步说的是你的server服务器端的执行方式
	阻塞说的是具体的技术，接收数据的方式，状态（io,nio）

		

	Http请求头
		Accept
		Accept-Encoding
		Cookie
		Host
		User-Agent
				
	不是网络直接传输，而是将数据刷到一块缓冲区，操作系统取出数据

	
8、传统BIO编程

	
	三次握手 - Socket
	
	客户端和服务端分别有一个对应的Socket
                               Socket
	Client Socket <->  ServerSocket.accept ->实例化线程处理socket
	
	缺点：如果socket过多，会创建大量的线程

	
	伪异步的IO

		采用线程池和任务队列，实现一种伪异步的IO通信
		将客户端的Socket封装成一个task任务,实现Runnable接口
		然后投递到线程池中去

	
		
9、NIO
	非阻塞IO
	NIO的本质就是避免原始的TCP连接使用3次握手的操作，减少连接的开销

	Buffer 缓冲区
	Chaneel 管道

	Selector 选择器，多路复用器，轮询所有注册的通道，根据通道状态，执行相关操作

	Connect 连接状态
	Accept  阻塞状态
	Read    可读状态
	Write   可写状态
-----------------------------------------------------------	
               注册到选择器
Client1    ---------------- |       Server端
  SocketChannel->ByteBuffer    ServerSocketChannel->ByteBuffer                  

              注册到选择器  |       
Client2    ----------------   —   Selector多路复用器
                         
               注册到选择器 | 
Client3..N  ----------------       
                                  Connect Accept Read Write
-----------------------------------------------------------

	1、Buffer:包含要写入或者读取的数据
	在面向流的IO中，直接将数据写入或者读取到Stream对象中
	
	在NIO中，所有数据都是用缓冲区处理的
	通常是一个字节数组ByteBBuffer,或者
	CharBuffer,ShorBuffer,IntBuffer,LongBuffer,FloatBuffer,DoubleBuffer

	
	2、Channel
	网络数据通过Channel读取和写入
	通道与流的不同之处在于，通道是双向的，而流是一个方向上的移动
	而通道可以用于读，写，或者二者同时进行
	
	最关键的是可以于多路复用器结合起来，有多种状态位，方便多路复用器去识别
	
	通道的分类
		用于网络读写的 SelectableChannel
		用于文件操作的 FileChannel
		SocketChannel和ServerSocketChannel都是SelectableChannel的子类


	3、Selector

		1、功能
		提供选择已经就绪任务的能力

		不断轮询注册在其上的通道，如果某个通道发生了读写操作
		这个通道就会处于就绪状态，会被Selector轮询出来
		然后通过SelectionKey可以取得就绪的Channel集合，从而进行后续的IO操作
		
		一个Selector可以负责成千上万Channel通道，没有上限
		这也是JDK使用了epoll代替了传统的select实现
		
		获得连接句柄没有限制
		只要一个线程负责Selector的轮询，就可以接入成千上万个客户端
		这个JDK NIO库的巨大进步

		Selector线程就类似一个Master，管理成千上万个管道
		轮询哪个管道的数据已经准备好，通知CPU执行IO的读取或写入操作

		2、模式

			当IO事件（管道）注册到选择器以后，selector会分配给每个管道一个

		key值，相当于标签，selector选择器是以轮询的方式进行查找注册的所有IO事件(管道)，

		当我们的IO事件(管道)准备就绪后，select就会识别，会通过key值来找到对应的管道，进行

		相关的数据处理操作(从管道读取或写数据，写到缓冲区中)

		SelectionKey.OP_CONNECT
		SelectionKey.OP_ACCEPT
		SelectionKey.OP_READ
		SelectionKey.OP_WRITE

		netty是NIO框架的一种实现

10、AIO
	
	在NIO基础之上引入了异步通道的概念，并提供了异步文件和异步套接字通道的实现
	真正实现了异步非阻塞

	NIO只是非阻塞并非异步

	AsynchronousServerSocketChannel
	AsynchronousSocketChannel

	
		
	





















		 



