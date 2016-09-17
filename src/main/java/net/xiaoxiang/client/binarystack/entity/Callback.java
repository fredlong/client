package net.xiaoxiang.client.binarystack.entity;

import net.xiaoxiang.client.binarystack.util.Action;

/**
 * 回调函数实体类
 * 保存回调函数和其对应的创建时间、请求报文实例，用于回调处理和超时判断
 * Created by fred on 16/8/28.
 */
public class Callback {
    Action<CommonResponse> action;
    Long createTime;

    public Action<CommonResponse> getAction() {
        return action;
    }

    public void setAction(Action<CommonResponse> action) {
        this.action = action;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
