package net.xiaoxiang.client.binarystack.entity;

import java.io.Serializable;

/**
 * 业务应答父类
 * 所有业务应答都使用本父类，如果应答中有返回的业务数据，放入Entity中
 * Created by fred on 16/9/3.
 */
public class CommonResponse<T>{
    int resultCode;
    String message;
    T entity = null;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public CommonResponse(int resultCode){
        this(resultCode , null , null);
    }

    public CommonResponse(int resultCode , String message){
        this(resultCode , message , null);
    }

    public CommonResponse(int resultCode , T entity){
        this(resultCode , null , entity);
    }


    public CommonResponse(int resultCode , String message , T entity){
        this.resultCode = resultCode;
        this.message = message;
        this.entity = entity;
    }

    public CommonResponse(ResponseInformation responseInformation){
        this(responseInformation.getResultCode() , responseInformation.getMessage() , null);
    }

    public CommonResponse(ResponseInformation responseInformation  , T entity){
        this(responseInformation.getResultCode() , responseInformation.getMessage() , entity);
    }

}
