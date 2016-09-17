package net.xiaoxiang.client.binarystack;

import net.xiaoxiang.client.binarystack.entity.BeanResource;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * 信令资源管理类
 * 协议栈启动之后需要先初始化本类
 * 需要把所有Notify对应的Handler和Request的Clazz和Handler对应的Response类型初始化到本类中
 * 本类主要给NotifyDispath在收到通知时和CallbackManager在收到Response的时候调用
 * Created by fred on 16/9/15.
 */
public class BeanResourceManager {
    public static HashMap<Short , BeanResource> beanResources = new HashMap<Short, BeanResource>();

    /**
     * 初始化接收到的通知信令相关资源数据
     * @param cmd
     * 信令编号
     * @param notifyHandlerClazz
     * 信令Handler类
     * @param notifyRequestClazz
     * 信令Request类
     * @throws InstantiationException
     * Handler初始化时可能抛出本错误
     * @throws IllegalAccessException
     * Handler初始化时可能抛出本错误
     */
    public static void addNotifyBean(Short cmd , Class notifyHandlerClazz , Class notifyRequestClazz) throws InstantiationException, IllegalAccessException{
        beanResources.put(cmd , new BeanResource(notifyHandlerClazz , notifyRequestClazz));
    }

    /**
     * 初始化客户端发出的信令相关资源数据
     * @param cmd
     * 信令编号
     * @param responseType
     * 信令Response类
     * @throws InstantiationException
     * Handler初始化时可能抛出本错误
     * @throws IllegalAccessException
     * Handler初始化时可能抛出本错误
     */
    public static void addHandlerBean(Short cmd , Type responseType){
        beanResources.put(cmd , new BeanResource(responseType));
    }

    public static Handler getHandler(Short cmd) throws InstantiationException, IllegalAccessException{
        BeanResource cmdResource = beanResources.get(cmd);
        if(null != cmdResource){
            return cmdResource.getHandler();
        }
        return null;
    }

    public static Class getRequest(Short cmd){
        BeanResource cmdResource = beanResources.get(cmd);
        if(null != cmdResource){
            return cmdResource.getRequest();
        }
        return null;
    }

    public static Type getResponse(Short cmd){
        BeanResource cmdResource = beanResources.get(cmd);
        if(null != cmdResource){
            return cmdResource.getResponse();
        }
        return null;
    }
}
