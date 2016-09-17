package net.xiaoxiang.client.binarystack.message;

import net.xiaoxiang.client.binarystack.BinaryStackConfiguration;
import net.xiaoxiang.client.binarystack.entity.CommonResponse;
import net.xiaoxiang.client.binarystack.Context;
import com.google.gson.Gson;

/**
 *  BPMessageFactory
 *  BPMessage工厂类，所有的BPMessage实例均由本工厂生成
 * Created by fred on 16/8/28.
 */
public class BPMessageFactory {

    /**
     * 生成请求消息
     * @param userId
     * 用户Id
     * @param cmd
     * 信令编码
     * @param opt
     * 协议扩展字段
     * @param body
     * 消息体
     * @return
     * 新生成的报文
     */
    public static BPMessage generateBPMessage(int userId ,short cmd , byte[] opt , byte[] body){
        BPHeader header = new BPHeader(userId, cmd, opt);
        return new BPMessage(header, body);
    }



    /**
     * 根据用户的请求报文生成应答报文
     * @param request
     * 请求报文
     * @param resultCode
     * 应答报文中CommonResponse对象中的应答码
     * @param message
     * 应答报文中CommonResponse对象中的应答提示消息
     * @param entity
     * 应答报文中CommonResponse对象中携带的应答内容
     * @return
     * 生成的报文
     */
    public static BPMessage generateBPMessage(BPMessage request , int resultCode , String message , Object entity){
        Gson gson = new Gson();
        CommonResponse response = new CommonResponse(resultCode , message , entity);
        response.setEntity(entity);

        return new BPMessage(request , gson.toJson(response).getBytes(BinaryStackConfiguration.CHARSET));
    }

}
