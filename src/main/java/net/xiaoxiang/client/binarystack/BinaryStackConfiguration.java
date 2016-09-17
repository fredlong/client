package net.xiaoxiang.client.binarystack;

import java.nio.charset.Charset;

/**
 * Created by fred on 16/8/24.
 */
public class BinaryStackConfiguration {

    /**
     * 服务器IP
     */
    public final static String REMOTE_HOST =  "127.0.0.1";

    /**
     * 服务端监听端口
     */
    public final static int REMOTE_PORT = 8080;

    /**
     * 客户端类型
     */
    public final static byte CLIENT_TYPE = 1;

    /**
     * 客户端版本号
     */
    public final static byte CLIENT_VERSION = 1;

    /**
     * 客户端版本号
     */
    public final static byte PROTOCOL_VERSION = 1;

    /**
     * 应答超时时间
     */
    public final static int CALLBACK_TIMEOUT = 5000;

    /**
     * 应答超时时间
     */
    public final static int CONNECT_TIMEOUT = 5000;

    /**
     * BPMessage中的Body编码
     */
    public final static Charset CHARSET = Charset.forName("UTF-8");



    /**
     * 处理应答报文线程池的线程数量
     * 作为客户端，一般一个线程就够了
     */
    public final static int CALLBACK_THREADPOOL_COUNT = 1;

    /**
     * 处理请求报文线程池的线程数量
     * 作为客户端，一般一个线程就够了
     */
    public final static int NOTIFY_THREADPOOL_COUNT = 1;

    /**
     * 处理发送请求类报文线程池的线程数量
     * 作为客户端，一般一个线程就够了
     */
    public final static int NIO_WORKER_THREADPOOL_COUNT = 1;
}
