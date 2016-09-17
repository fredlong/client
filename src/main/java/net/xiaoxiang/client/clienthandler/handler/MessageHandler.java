package net.xiaoxiang.client.clienthandler.handler;

import net.xiaoxiang.client.binarystack.util.Action;
import net.xiaoxiang.client.binarystack.entity.CommonResponse;
import net.xiaoxiang.client.binarystack.NetworkManager;
import net.xiaoxiang.client.clienthandler.CommandNumber;
import net.xiaoxiang.client.clienthandler.parameters.SendMessageArgs;

/**
 * 消息处理的handler
 * 将用户消息封装在args中，调用NetworkManager发送
 * Created by fred on 16/9/4.
 */
public class MessageHandler {

    public static void sendMessage(int targetUserId , String content , Action<CommonResponse> callback){
        SendMessageArgs args = new SendMessageArgs();
        args.setTargetUserId(targetUserId);
        args.setMessageContent(content);
        NetworkManager.getInstance().send(CommandNumber.SEND_MESSAGE , args , callback);
    }

}
