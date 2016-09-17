package net.xiaoxiang.client.binarystack;

import net.xiaoxiang.client.binarystack.util.BPException;

/**
 * Handler接口类
 * Created by fred on 16/8/29.
 */
public interface Handler {
    /**
     * 处理请求报文业务逻辑
     * @param tx
     * @param request
     * @throws BPException
     */
    void handle(Transaction tx , Object request) throws BPException;
}
