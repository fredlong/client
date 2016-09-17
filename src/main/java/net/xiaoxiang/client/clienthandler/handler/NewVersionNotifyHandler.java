package net.xiaoxiang.client.clienthandler.handler;

import net.xiaoxiang.client.binarystack.StackResponseManager;
import net.xiaoxiang.client.binarystack.util.BPException;
import net.xiaoxiang.client.binarystack.Handler;
import net.xiaoxiang.client.binarystack.Transaction;
import net.xiaoxiang.client.clienthandler.parameters.NewVersionRequest;

import java.util.Date;

/**
 * 接收版本变更的通知Handler
 * Created by fred on 16/8/29.
 */
public class NewVersionNotifyHandler implements Handler {
    public void handle(Transaction tx , Object requestObject) throws BPException{
        if(!(requestObject instanceof  NewVersionRequest)){
            throw new BPException(400 , "Request arg format error");
        }

        try {
            NewVersionRequest request = (NewVersionRequest) requestObject;
            System.out.println("Sussess to get NewVersionRequest notification from server");
            System.out.println("NewVersion is " + request.getNewVersion());
            System.out.println("Publish time is " + new Date(request.getPublishDate()));
            tx.sendResponse(StackResponseManager.OK);
        }
        catch (Exception ex){
            throw new BPException(500 , ex.getMessage() , ex);
        }
    }
}
