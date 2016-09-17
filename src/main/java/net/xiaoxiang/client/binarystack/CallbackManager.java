package net.xiaoxiang.client.binarystack;

import net.xiaoxiang.client.binarystack.entity.Callback;
import net.xiaoxiang.client.binarystack.entity.CommonResponse;
import net.xiaoxiang.client.binarystack.message.BPMessage;
import net.xiaoxiang.client.binarystack.util.Action;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 回调函数管理类
 * 客户端在发送请求后，将回调函数和请求都放入到本类中进行管理，待应答回来后，Dispatch类会调用本类执行回调函数
 * 本类自己有一个检查线程，用来检查自己管理的Callback是否过期，一旦过期，从直接执行Callback，返回502错误
 * Created by fred on 16/8/28.
 */
public class CallbackManager implements Runnable{
    private static CallbackManager instance = new CallbackManager();
    private static Object lock = new Object();

    //检查Callback是否超时的时间间隔
    private static long CHECK_INTERVAL = 500;

    //考虑到线程安全问题使用Hashtable
    private Hashtable<Short , Callback> callbackMap = new Hashtable<Short , Callback>();

    //处理应答的线程池数量，一般作为客户端，一个线程就够了，而且一个线程能保证执行的顺序和收到应答的顺序一致
    private ExecutorService fixThreadPool = Executors.newFixedThreadPool(BinaryStackConfiguration.CALLBACK_THREADPOOL_COUNT);

    private static final Logger logger = LoggerFactory.getLogger(CallbackManager.class);

    /**
     * 使用单例模式
     * @return
     */
    public static CallbackManager getInstance(){
        if(null == instance){
            synchronized (lock){
                if(null == instance){
                    instance = new CallbackManager();
                }
            }
        }
        return instance;
    }

    private CallbackManager(){}

    /**
     * 发送请求的同时将Callback放入到本类，等待回调
     * 同时记录放入的时间，用于判断应答超时
     * @param seq
     * 报文序号
     * @param action
     * 回调函数
     */
    public void addCallBack(short seq , Action<CommonResponse> action){
        Callback callBack = new Callback();
        callBack.setCreateTime(System.currentTimeMillis());
        callBack.setAction(action);
        callbackMap.put(seq , callBack);
    }


    /**
     * 将回调函数放入到线程池中执行
     * @param response
     * 服务器返回应答报文
     */
    public void execute(final BPMessage response){
        if(null == response || null == response.getHeader()){
            throw new RuntimeException("Response and header can not be null");
        }

        final short seq  = response.getHeader().getSeq();
        final Callback callback = callbackMap.remove(seq);

        /**
         * 如果没有找到Callback，记录info日志
         * 有可能是在发送的时候没有加入Callback或者由于应答超时，Callback已经被超时线程执行
         */
        if(null == callback){
            if(logger.isInfoEnabled()){
                logger.info("Callback not found with seq {}" , seq);
            }
        }
        else{
            fixThreadPool.execute(new Runnable() {
                public void run() {

                    /**
                     * 从报文中获取报文体重的byte[]
                     * 使用Gson解析出CommonResponse对象实例
                     */
                    CommonResponse commonResponse = null;
                    try {
                        Gson gson = new Gson();
                        //到BeanResourceManager获取带返回内容的responseType
                        Type responseType = BeanResourceManager.getResponse(response.getHeader().getCmd());
                        if(null != responseType) {
                            //从报文体重解析出应答实例
                            commonResponse = gson.fromJson(new String(response.getBody(), BinaryStackConfiguration.CHARSET), responseType);
                        }
                        /**
                         * 如果用户没有设置带内容的responseType，那么使用没有内容的CommonResponse去解析
                         * 调用者在不关心应答中的Entity的时候，可以不设置Response中Entity的类型
                         */
                        else{
                            commonResponse = gson.fromJson(new String(response.getBody(), BinaryStackConfiguration.CHARSET), CommonResponse.class);
                        }
                    }
                    //CommonResponse解析失败，使用506回调
                    catch (JsonSyntaxException ex){
                        callback.getAction().run(new CommonResponse(StackResponseManager.FAIL_TO_PARSE_RESPONSE));
                        return;

                    }

                    /**
                     * 执行回调
                     */
                    try{
                        callback.getAction().run(commonResponse);
                    }
                    catch (Exception ex){
                       logger.error("Execute callback meets error seq is "+ seq , ex);
                    }
                }
            });
        }
    }

    /**
     * 检查Callback是否过期，如果过期直接使用502回调，并且将回调函数移除
     */
    public void run() {

        while(true){

            try {
                Enumeration<Short> seqs = callbackMap.keys();
                while(seqs.hasMoreElements()){
                    short seq = seqs.nextElement();
                    Callback callBack = callbackMap.get(seq);
                    if((System.currentTimeMillis() - callBack.getCreateTime()) > BinaryStackConfiguration.CALLBACK_TIMEOUT){
                        callBack.getAction().run(new CommonResponse(StackResponseManager.CALLBACK_EXPIRED));
                        callbackMap.remove(seq);
                    }
                }
                Thread.sleep(CHECK_INTERVAL);
            }
            catch (Exception ex){
                logger.error("Check callback expiration meets error" , ex);
            }
        }
    }



}
