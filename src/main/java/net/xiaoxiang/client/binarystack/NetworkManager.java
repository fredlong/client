package net.xiaoxiang.client.binarystack;

import net.xiaoxiang.client.binarystack.entity.CommonResponse;
import net.xiaoxiang.client.binarystack.enums.NetworkStatus;
import net.xiaoxiang.client.binarystack.message.*;
import net.xiaoxiang.client.binarystack.util.Action;
import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * 网络连接管理类
 * 负责和服务器建立连接，断线重连
 * 负责发送请求
 * Created by fred on 16/8/20.
 */
public class NetworkManager  implements Runnable{
    private static NetworkManager netWork = new NetworkManager();
    private static Object lock = new Object();
    //连接通道
    private Channel channel = null;
    //网络状态
    private NetworkStatus networkStatus = NetworkStatus.CLOSED;
    private static final Logger logger = LoggerFactory.getLogger(NetworkManager.class);

    //Netty相关组件类
    EventLoopGroup workerGroup;
    Bootstrap clientBootStrap;
    ChannelFutureListener channelFutureListener;

    /**
     * 使用单例模式
     * @return
     */
    public static NetworkManager getInstance(){
        if(null == netWork){
            synchronized (lock){
                if(null == netWork){
                    netWork = new NetworkManager();
                }
            }
        }
        return netWork;
    }


    private NetworkManager(){
        /**
         * 初始化Netty
         * workerGroup是网络io的角色，客户端一个线程够用
         */
        workerGroup = new NioEventLoopGroup(BinaryStackConfiguration.NIO_WORKER_THREADPOOL_COUNT);
        clientBootStrap = new Bootstrap();
        clientBootStrap.group(workerGroup);
        clientBootStrap.channel(NioSocketChannel.class);
        //Netty自动发送keepalive保持长连接
        clientBootStrap.option(ChannelOption.SO_KEEPALIVE, true);
        //设置连接超时时间
        clientBootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,BinaryStackConfiguration.CONNECT_TIMEOUT);
        /**
         * 增加三个handler：
         * encoder是负责发送请求将BPMessage序列化成byte[]的decoder
         * decoder是负责接收到服务端发送过来的数据是，将byte[]数组反序列化成BPMessage对象
         * handler是负责监听并处理网络事件：
         *  Netty在接收到完整的BPMessage包的时候调用此类的channelRead重载实现，
         *  Netty在网络断掉的时候调用此类的channelInactive重载实现，
         *  Netty在网络出现异常的时候调用此类的exceptionCaught重载实现
         */
        clientBootStrap.handler(new ChannelInitializer<NioSocketChannel>() {

            /**
             * 初始化Channel，指定序列化、反序列化、时间处理对应的类
             * @param nioSocketChannel
             * @throws Exception
             */
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast("encoder" , new BPEncoder())
                        .addLast("decoder" , new BPDecoder())
                        .addLast("handler", new Dispatcher());
            }
        });

        /**
         * 负责建立连接失败自动重连的机制
         * 连接失败后，一秒后自动重试
         */
        channelFutureListener = new ChannelFutureListener() {
            //连接事件完成后调用本方法
            public void operationComplete(ChannelFuture future) throws Exception {
                //连接成功，取到available的channel
                if(future.isSuccess()) {
                    NetworkManager.getInstance().channel = future.channel();
                    networkStatus = NetworkStatus.CONNECTED;
                    logger.info("connected to server");
                //连接失败，一秒钟后自动重连
                } else {
                    logger.info("failed to connect server");
                    //  1秒后重新连接
                    future.channel().eventLoop().schedule(new Runnable() {
                        public void run() {
                            NetworkManager.getInstance().connect();
                        }
                    }, 1, TimeUnit.SECONDS);
                }
            }
        };

    }


    /**
     * 作为客户端使用Netty连接服务器
     * 连接成功后，获取到channel对象
     * @throws InterruptedException
     */
    public void  connect(){
        networkStatus = NetworkStatus.CONNECTING;
        ChannelFuture future = null;
        try {
            future = clientBootStrap.connect(BinaryStackConfiguration.REMOTE_HOST, BinaryStackConfiguration.REMOTE_PORT);
            future.addListener(channelFutureListener);
        }
        catch (Exception ex){
            if(null != future) {
                future.addListener(channelFutureListener);
            }
        }

    }

    /**
     * 负责关闭channel
     */
    public void disconnect(){
        networkStatus = NetworkStatus.CLOSING;
        if(channel != null) {
            channel.disconnect();
        }
        networkStatus = NetworkStatus.CLOSED;
    }

    /**
     * 监听到网络被断掉时调用
     * @throws InterruptedException
     */
    public void onDisconnected() throws InterruptedException {
        disconnect();
        logger.info("disconnected from server ");
        connect();
    }



    private void send(BPMessage message){
        channel.writeAndFlush(message);
    }



    public void send(short cmd , Object arg , Action<CommonResponse> action){
        send(Context.getUserId() , cmd , null , arg , action);
    }

    public void send(int userId , short cmd , Object arg , Action<CommonResponse> action){
        send(userId , cmd , null , arg , action);
    }

    /**
     * 发送BMMessage
     * 调用BPMessageFactory生成Request的BPMessage
     * 把Callback对象放入到CallbackManager中管理
     * 在连接上发送Request对象
     * @param userId
     * 用户Id
     * @param cmd
     * 命令字
     * @param opt
     * 报文头扩展字段
     * @param arg
     * 业务Request对象
     * @param action
     * 回调函数
     */
    public void send(int userId , short cmd , byte[] opt , Object arg , Action<CommonResponse> action){
        Gson gson = new Gson();
        BPMessage message = BPMessageFactory.generateBPMessage(userId , cmd, opt, gson.toJson(arg).getBytes(BinaryStackConfiguration.CHARSET));
        if(action != null) {
            CallbackManager.getInstance().addCallBack(message.getHeader().getSeq() , action);
        }
        this.send(message);
    }


    public void send(short cmd , byte[] opt , Object arg , Action<CommonResponse> action){
        send(Context.getUserId() , cmd , opt , arg , action );
    }


    public NetworkStatus getNetstatus() {
        return networkStatus;
    }


    public void run() {
        try {
            NetworkManager.getInstance().connect();
        }
        catch (Exception ex){
            logger.error("connect server failed" , ex);
        }
    }
}