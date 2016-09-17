package net.xiaoxiang.client.binarystack.message;

import io.netty.buffer.ByteBuf;

/**
 *二进制报文类
 * 包括二进制报文头和报文体
 */
public class BPMessage {

    /**
     * 报文头
     */
    private BPHeader header;

    /**
     * 报文体
     */
    private byte[] body;


    /**
     * 发送请求报文时，实例化请求报文
     *
     * @param header
     * 请求的报文头
     *
     * @param body
     * 请求的报文体
     */
    BPMessage(BPHeader header , byte[] body){
        header.computerLength((short) body.length);
        this.header = header;
        this.body = body;
    }

    /**
     * 生成应答报文时，实例化报文
     *
     * @param request
     * 对应的请求报文
     *
     * @param body
     * 应答报文体
     */
    BPMessage(BPMessage request , final byte[] body){
        this.header = request.getHeader();
        header.packageType = (byte)1;
        header.computerLength((short)body.length);
        this.body = body;
    }

    /**
     * 从Netty中读取报文
     * @param buffer
     * Netty的二进制数组缓存
     */
    BPMessage(ByteBuf buffer){
        this.header = new BPHeader(buffer);
        this.body = new byte[header.getPackageLength() - header.getHeaderLength()];
        buffer.readBytes(this.body);
    }


    /**
     * 将报文内容按照协议规定的顺序，写入Netty的缓存对象
     * @param buffer
     * Netty的缓存对象
     */
    public void writeToBuffer(ByteBuf buffer){
        this.header.writeToBuffer(buffer);
        buffer.writeBytes(body);
    }

    /**
     * 判断报文是否是应答报文
     * @return
     */
    public boolean isResponse(){
        if(null != this.getHeader()){
            return this.getHeader().getPackageType() != 0;
        }
        else
        {
            throw new RuntimeException("Header can not be null");
        }
    }


    public BPHeader getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }



}
