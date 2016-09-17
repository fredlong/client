package net.xiaoxiang.client.binarystack;

import net.xiaoxiang.client.binarystack.message.BPMessage;
import net.xiaoxiang.client.binarystack.message.BPMessageFactory;
import net.xiaoxiang.client.binarystack.entity.ResponseInformation;
import io.netty.channel.ChannelHandlerContext;

/**
 * 事务上下文类
 * 每一次请求的channel，Request都会放在这个类里，并被传递到handler中
 * handler调用tx方法返回应答
 * Created by fred on 16/9/3.
 */
public class Transaction {
    BPMessage request;
    ChannelHandlerContext context;

    Transaction(BPMessage request , ChannelHandlerContext context){
        this.request = request ;
        this.context = context;
    }

    public BPMessage getRequest() {
        return request;
    }

    public void sendResponse(ResponseInformation responseInformation){
        this.sendResponse(responseInformation , null);
    }

    public void sendResponse(ResponseInformation responseInformation , Object entity){
        context.channel().writeAndFlush(BPMessageFactory.generateBPMessage(request, responseInformation.getResultCode(), responseInformation.getMessage(), entity));
    }

}
