package net.xiaoxiang.client.binarystack.message;

import net.xiaoxiang.client.binarystack.BinaryStackConfiguration;
import io.netty.buffer.ByteBuf;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 报文头实现
 * 包括报文头结构
 * 包括从Netty的ByteBuf读取数据，将数据写入Netty的ByteBuf中的实现，写入和读取的顺序必须一致
 */
public class BPHeader {

    /**
     * 为了保证线程安全，使用AtomicInteger来做报文序号的自增
     */
    static AtomicInteger seqAtomic = new AtomicInteger(0);

    /**
     * 头部除去opt之外的所有字段的大小
     * 用来计算opt的长度
     */
    static final byte FIX_HEADER_LENGHT = 15;

    static final byte MAX_OPT_LENGTH = Byte.MAX_VALUE - FIX_HEADER_LENGHT;

    /**
     * seq的长度一旦达到Short.MAX_VALUE的时候，seq需要归零
     * -1000的目的是因为考虑到效率，generateSeq()方法内没有加锁，提前结束循环为了避免seq在多线程情况下short越界的情况
     */
    final short seqMax = Short.MAX_VALUE - 1000;

    /**
     * 报文尺寸
     * 在协议中我们定义这块用2 byte来传输，两个byte最大可以到65535，Java中的short最大是32767，因此这块我们用int存储
     */
    protected int packageLength;

    /**
     * 报文头尺寸，默认为固定字段总和
     * 在协议中我们定义这块用1 byte来传输，一个byte最大可以到255，Java中的byte最大是127，因此这块我们用short存储
     */
    protected short headerLength = FIX_HEADER_LENGHT;

    /**
     * 用户Id，默认为0
     */
    protected int userId = 0;

    /**
     * 命令字
     */
    protected short cmd;

    /**
     * 报文序号
     */
    protected short seq;


    /**
     * 报文种类：
     * 0:request
     * 1:response
     */
    protected byte packageType = 0;

    /**
     * 客户端类型：
     *  0:iPhone
     *  1:Android
     *  3:iPad
     *  4:Android Pad
     *  5:Web
     */
    protected byte clientType = 1;

    /**
     * 客户端版本号
     */
    protected byte clientVersion = 0;

    /**
     * 二进制协议版本号
     */
    protected byte protocalVersion = 0;

    /**
     * 扩展保留字段
     */
    protected byte[] opt;


    /**
     * 生成报文序号
     * @return
     * 报文序号
     */
    private short generateSeq(){
        if(seqAtomic.get() > seqMax){
            seqAtomic.set(0);
        }

        return (short)seqAtomic.getAndIncrement();
    }


    /**
     * 生成请求报文时的BPHeader的构造函数
     * @param userId
     * @param cmd
     */
    BPHeader(int userId , short cmd , byte[] opt){
        this.userId = userId;
        this.cmd = cmd;
        this.opt = opt;
        this.clientType = BinaryStackConfiguration.CLIENT_TYPE;
        this.clientVersion = BinaryStackConfiguration.CLIENT_VERSION;
        this.protocalVersion = BinaryStackConfiguration.PROTOCOL_VERSION;
        this.seq = generateSeq();
        if(null != opt && opt.length > 0){
            if(opt.length > MAX_OPT_LENGTH){
                throw new RuntimeException("opt byte array length shall not great than " + MAX_OPT_LENGTH);
            }
            this.headerLength = (byte)(FIX_HEADER_LENGHT + opt.length);
        }

    }

    /**
     * 生成请求报文时的BPHeader的构造函数
     * @param userId
     * @param cmd
     */
    BPHeader(int userId , short cmd){
        this(userId, cmd, null);
    }

    /**
     * 从Netty底层读取数据时BPHeader的构造函数
     * @param buffer
     * Netty的传输buffer
     */
    BPHeader(ByteBuf buffer){
        this.packageLength = buffer.readUnsignedShort();
        this.headerLength = buffer.readUnsignedByte();
        this.cmd = buffer.readShort();
        this.seq = buffer.readShort();
        this.userId = buffer.readInt();
        this.packageType = buffer.readByte();
        this.clientType = buffer.readByte();
        this.clientVersion = buffer.readByte();
        this.protocalVersion = buffer.readByte();
        if(this.headerLength > FIX_HEADER_LENGHT){
            opt = buffer.readBytes(this.headerLength - FIX_HEADER_LENGHT).array();
        }
    }

    /**
     * 将报文头按照协议规定的顺序，写入Netty的缓存对象
     * 为了保证所有字段的读和写保持一致，这两块逻辑写到一块
     * @param buffer
     * Netty的传输缓存buffer
     */
    void writeToBuffer(ByteBuf buffer){
        buffer.writeShort(this.packageLength);
        buffer.writeByte(this.headerLength);
        buffer.writeShort(this.cmd);
        buffer.writeShort(this.seq);
        buffer.writeInt(this.userId);
        buffer.writeByte(this.getPackageType());
        buffer.writeByte(this.clientType);
        buffer.writeByte(this.clientVersion);
        buffer.writeByte(this.protocalVersion);
        if(this.headerLength > FIX_HEADER_LENGHT){
            buffer.writeBytes(opt);
        }
    }

    public void computerLength(short bodyLength){
        this.packageLength = (short)(this.headerLength + bodyLength);
    }


    public static AtomicInteger getSeqAtomic() {
        return seqAtomic;
    }

    public int getPackageLength() {
        return packageLength;
    }

    public short getHeaderLength() {
        return headerLength;
    }

    public int getUserId() {
        return userId;
    }

    public short getCmd() {
        return cmd;
    }

    public short getSeq() {
        return seq;
    }

    public byte getPackageType() {
        return packageType;
    }

    public byte getClientType() {
        return clientType;
    }

    public byte getClientVersion() {
        return clientVersion;
    }

    public byte[] getOpt() {
        return opt;
    }

    public byte getProtocalVersion() {
        return protocalVersion;
    }

}