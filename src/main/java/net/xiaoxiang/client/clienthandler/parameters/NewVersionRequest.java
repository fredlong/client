package net.xiaoxiang.client.clienthandler.parameters;

/**
 * 新版本通知请求
 * Created by fred on 16/9/3.
 */
public class NewVersionRequest{
    /**
     * 新版本版本号
     */
    String newVersion = "";

    /**
     * 发布日期
     */
    long publishDate = 0l;

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }


}
