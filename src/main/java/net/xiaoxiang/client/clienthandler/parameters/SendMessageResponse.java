package net.xiaoxiang.client.clienthandler.parameters;

/**
 * 发送消息应答
 * Created by fred on 16/9/4.
 */
public class SendMessageResponse {
    String messageId = "";

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
