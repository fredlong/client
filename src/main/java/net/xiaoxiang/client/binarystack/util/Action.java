package net.xiaoxiang.client.binarystack.util;


/**
 * 回调辅助类
 */
public interface Action<T>
{
    void run(T a);
}