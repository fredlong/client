package net.xiaoxiang.client.binarystack;

import net.xiaoxiang.client.binarystack.message.BPMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 报文分发总类
 * Created by fred on 16/8/20.
 */
public class Dispatcher  extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         * 协议栈在接收到报文后，判断报文是请求报文还是应答报文
         */
        if (msg instanceof BPMessage) {
            BPMessage bpMessage = (BPMessage) msg;
            /**
             * 如果是应答报文，那么分发给CallbackManger进一步处理
             */
            if(bpMessage.isResponse()){
                CallbackManager.getInstance().execute(bpMessage);
            }
            /**
             如果是请求报文，那是服务器发送过来的通知报文，分发给NotifyDispatcher进一步处理
             */
            else{
                Transaction tx = new Transaction(bpMessage , ctx);
                NotifyDispatcher.getInstance().execute(tx);
            }
        }
    }

    /**
     * 网络连接被断掉时，触发本方法
     * 调用NetworkManager监听方法，尝试自动重连
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NetworkManager.getInstance().onDisconnected();
        ctx.fireChannelInactive();
    }

    /**
     * 网络层出现异常的时候，断掉连接
     * @param ctx
     * @param cause
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            NetworkManager.getInstance().disconnect();
            ctx.close();
        } catch (Exception e) {
            logger.error("invoke disconnect failed", e);
        }
    }
}