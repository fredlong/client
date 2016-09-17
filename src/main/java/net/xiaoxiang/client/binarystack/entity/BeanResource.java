package net.xiaoxiang.client.binarystack.entity;

import net.xiaoxiang.client.binarystack.Handler;

import java.lang.reflect.Type;

/**
 * 信令资源实体类
 * Created by fred on 16/9/4.
 */
public class BeanResource{
    /**
     * 信令的处理类的clazz
     */
    Class handlerClazz;

    /**
     * 信令的处理类的实例
     */
    Handler handler;

    /**
     * 请求Clazz
     */
    Class request;

    /**
     * 信令应答类型，这块需要注意的是，因为需要指定统一的CommonResponse<ResponseEntity>类型，便于Gosn反序列化，请使用以下方法初始化：
     * new TypeToken<CommonResponse<SendMessageResponse>>() {}.getType()
     */
    Type response;

    Object lock = new Object();


    /**
     * Notify类信令资源初始化方法
     * @param handlerClazz
     * 信令的处理类的clazz
     * @param request
     * 信令的处理类的实例
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public BeanResource(Class handlerClazz , Class request) throws InstantiationException, IllegalAccessException{
        this.handlerClazz = handlerClazz;
        this.handler = (Handler)handlerClazz.newInstance();
        this.request = request;
    }

    /**
     * 客户端发送类信令资源初始化方法
     * @param response
     * 信令应答类型
     */
    public BeanResource(Type response){
        this.response = response;
    }

    /**
     * 获取信令handler的实例
     * 出现意外情况信令handler示例丢失，重建返回
     * 重建失败抛出异常InstantiationException、IllegalAccessException
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Handler getHandler()  throws InstantiationException, IllegalAccessException{
        if(null == handler){
            synchronized (lock){
                handler = (Handler) handlerClazz.newInstance();
            }
        }
        return handler;
    }

    public Class getRequest() {
        return request;
    }

    public Type getResponse() {
        return response;
    }

}
