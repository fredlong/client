package net.xiaoxiang.client.clienthandler;

import net.xiaoxiang.client.binarystack.*;
import net.xiaoxiang.client.binarystack.entity.CommonResponse;
import net.xiaoxiang.client.binarystack.enums.NetworkStatus;
import net.xiaoxiang.client.binarystack.util.Action;
import net.xiaoxiang.client.clienthandler.handler.MessageHandler;
import net.xiaoxiang.client.clienthandler.handler.NewVersionNotifyHandler;
import net.xiaoxiang.client.clienthandler.handler.RegHandler;
import net.xiaoxiang.client.clienthandler.parameters.*;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.BasicConfigurator;


/**
 * 客户端协议栈初始化、启动类
 * Created by fred on 16/8/29.
 */
public class ClientStartup {
    public static void main(String[] args)throws  Exception{
        BasicConfigurator.configure();

        /**
         * 初始化NotifyDispatcher，将通知的Handler和对应请求类装载到NotifyDispatcher中
         * 一旦有对应的通知下发到客户端，NotifyDispatcher可以找到对应的
         */
        BeanResourceManager.addNotifyBean(CommandNumber.NOTIFY_NEW_VERSION , NewVersionNotifyHandler.class, NewVersionRequest.class);
        BeanResourceManager.addHandlerBean(CommandNumber.REG , new TypeToken<CommonResponse<RegResponse>>() {}.getType());
        BeanResourceManager.addHandlerBean(CommandNumber.SEND_MESSAGE , new TypeToken<CommonResponse<SendMessageResponse>>() {}.getType());

        /**
         * 启动网络管理线程和Callback超时检查线程
         */
        new Thread(NetworkManager.getInstance()).start();
        new Thread(CallbackManager.getInstance()).start();

        /**
         * 检查网络状态，如果连接成功才继续向下走
         */
        while (true){
            if(NetworkManager.getInstance().getNetstatus() == NetworkStatus.CONNECTED){
                break;
            }
            Thread.sleep(1000);

        }

        /**
         * 发送登录请求
         */
        RegHandler.reg(10000003, "123456", new Action<CommonResponse>() {
            public void run(CommonResponse response) {
                /**
                 * 登录请求发送成功后发送消息请求
                 */
                System.out.println("Reg response is " + response.getResultCode());
                RegResponse regResponse = (RegResponse) response.getEntity();
                System.out.println("Reg Credential is " + regResponse.getCredential());
                Context.setUserId(10000003);

                MessageHandler.sendMessage(1000004, "Hello", new Action<CommonResponse>() {
                    public void run(CommonResponse response2) {
                        System.out.println("SendMessage response is " + response2.getResultCode());
                        SendMessageResponse sendMessageResponse = (SendMessageResponse)response2.getEntity();
                        System.out.println("MessageId is " + sendMessageResponse.getMessageId());
                    }
                });
            }
        });

    }
}
