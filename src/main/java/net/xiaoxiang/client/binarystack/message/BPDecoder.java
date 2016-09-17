package net.xiaoxiang.client.binarystack.message;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 协议栈反序列化类
 * 供Netty在接收到请求数据时调用
 * 负责将接收到的byte[]转化为BPMessage对象
 */
public class BPDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
        //从流中读取报文的长度
        int messageLength = byteBuf.getUnsignedShort(byteBuf.readerIndex());

        //检查流中是否有整个报文，如果没有，下次再读取
        if (byteBuf.readableBytes() >= messageLength) {

            //将整个报文读取到缓存中
            ByteBuf buffer = byteBuf.readBytes(messageLength);

            //实例化一个报文实例出来，释放缓存
            BPMessage message = new BPMessage(buffer);
            buffer.release();

            //将实例化的报文对象放入数组中，供Dispacher分发
            list.add(message);
        }

    }
}