package net.xiaoxiang.client.binarystack.entity;

/**
 * 应答码和对应提示信息的实体类
 * Created by fred on 16/9/11.
 */
public class ResponseInformation {

    int resultCode;
    String message;

    public ResponseInformation(int resultCode , String message){
        this.resultCode = resultCode ;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }
}
