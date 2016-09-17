package net.xiaoxiang.client.binarystack.util;

import net.xiaoxiang.client.binarystack.entity.ResponseInformation;

/**
 * 协议栈异常类
 * Created by fred on 16/9/10.
 */
public class BPException extends Exception {
    int resultCode;
    String message;

    public BPException(ResponseInformation responseInformation){
        super(responseInformation.getMessage());
        resultCode = responseInformation.getResultCode();
        message = responseInformation.getMessage();
    }

    public BPException(int resultCode, String resultMessage){
        super(resultMessage);
        this.resultCode = resultCode;
        this.message = resultMessage;
    }

    public BPException(int resultCode, String resultMessage, Exception ex){
        super(resultMessage , ex);
        this.resultCode = resultCode;
        this.message = resultMessage;
    }



    public int getResultCode() {
        return this.getResultCode();
    }


    public String getResultMessage() {
        return this.getMessage();
    }


}
