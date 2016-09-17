package net.xiaoxiang.client.binarystack;

import net.xiaoxiang.client.binarystack.message.BPMessage;
import net.xiaoxiang.client.binarystack.entity.ResponseInformation;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 通知分发类
 * Created by fred on 16/9/3.
 */
public class NotifyDispatcher {

    private static NotifyDispatcher instance = new NotifyDispatcher();
    private static Object lock = new Object();
    private ExecutorService fixThreadPool = Executors.newFixedThreadPool(BinaryStackConfiguration.NOTIFY_THREADPOOL_COUNT);
    private static final Logger logger = LoggerFactory.getLogger(NotifyDispatcher.class);

    public static NotifyDispatcher getInstance(){
        if(null == instance){
            synchronized (lock){
                if(null == instance){
                    instance = new NotifyDispatcher();
                }
            }
        }

        return instance;
    }


    /**
     * 根据CmdNumber到BeanResourceManager找到对应的handler和request的class，将通知对象分发到对应的handler中
     * @param tx
     */
    public void execute(final Transaction tx){
        fixThreadPool.execute(new Runnable() {
            public void run() {
                try {
                    /**
                     * 从BeanResourceManager找到对应的Notify handler和对应的Request class
                     * 没找到说明初始化有问题，抛exception，返回500错误
                     */
                    BPMessage request = tx.getRequest();
                    Handler handler = BeanResourceManager.getHandler(request.getHeader().getCmd());
                    Class requestClass = BeanResourceManager.getRequest(request.getHeader().getCmd());

                    if(null ==  handler || null == requestClass){
                        throw new Exception("Please initial BeanResourceManager first");
                    }

                    Gson gson = new Gson();
                    handler.handle(tx , gson.fromJson(new String(request.getBody(), BinaryStackConfiguration.CHARSET), requestClass));
                }
                catch (Exception ex){
                    logger.error("invoke callback meets error" , ex);
                    tx.sendResponse(new ResponseInformation(500 , ex.getMessage()));
                }
            }
        });
    }
}


