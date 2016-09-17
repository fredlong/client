package net.xiaoxiang.client.binarystack.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 协议栈序列化类
 * 供Netty在发送报文时调用
 * 负责将BPMessage对象转化为Byte数组，写入传输层
 */
public class BPEncoder  extends MessageToByteEncoder<BPMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BPMessage mcpMessage, ByteBuf byteBuf) throws Exception {
        //调用BPMessage的writeToBuffer方法将BPMessage自己的内容按照协议栈定义的顺序写入传输层
        mcpMessage.writeToBuffer(byteBuf);
    }
}
