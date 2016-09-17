package net.xiaoxiang.client.binarystack;

/**
 * 协议栈上下文缓存
 * 用户登录之后，userId和credential缓存在本实体类中
 * 其他信令需要发送请求不再填写userId
 * credential可以用于断线重连逻辑
 * Created by fred on 16/9/4.
 */
public class Context {
    static private int userId  = 0;
    static private String credential = "";

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        Context.userId = userId;
    }

    public static String getCredential() {
        return credential;
    }

    public static void setCredential(String credential) {
        Context.credential = credential;
    }
}
