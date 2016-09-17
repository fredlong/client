package net.xiaoxiang.client.clienthandler.handler;

import net.xiaoxiang.client.binarystack.util.Action;
import net.xiaoxiang.client.binarystack.entity.CommonResponse;
import net.xiaoxiang.client.binarystack.NetworkManager;
import net.xiaoxiang.client.clienthandler.CommandNumber;
import net.xiaoxiang.client.clienthandler.parameters.RegArgs;

/**
 * 处理发送登录请求的Handler
 * Created by fred on 16/9/4.
 */
public class RegHandler {
    public static void reg(int userId , String password , Action<CommonResponse> callback){
        RegArgs args = new RegArgs();
        args.setUserId(userId);
        args.setPassword(password);
        NetworkManager.getInstance().send(userId , CommandNumber.REG ,  args , callback);
    }
}
