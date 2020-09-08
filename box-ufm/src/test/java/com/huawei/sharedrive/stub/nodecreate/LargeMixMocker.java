package com.huawei.sharedrive.stub.nodecreate;

import org.junit.Test;

public class LargeMixMocker
{
    
    public static void main(String[] args)
    {
        LargeMixMocker mocker = new LargeMixMocker();
        mocker.createLargeMixFiles();
    }
    
    @Test
    public void createLargeMixFiles()
    {
        System.out.println("begin");
        Thread backThread = new Thread(new CreateBackNodesThread(20112));
        backThread.start();
        Thread normalThread = new Thread(new CreateNormalNodesThread(20112));
        normalThread.start();
        System.out.println("end");
        
    }
    
}
