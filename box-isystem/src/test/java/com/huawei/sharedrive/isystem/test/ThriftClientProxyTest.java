package com.huawei.sharedrive.isystem.test;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.common.job.thrift.JobInfo;
import com.huawei.sharedrive.isystem.thrift.client.JobThriftServiceClient;

import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;

public class ThriftClientProxyTest extends AbstractSpringTest
{
    @Autowired
    private ThriftClientProxyFactory factory;
    
    @Test
    public void test() throws Exception
    {
        System.out.println("------------");
        for (int i = 0; i < 1; i++)
        {
            JobThriftServiceClient client1 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client1.getServerIp() + ":" + client1.getServerPort());
            JobThriftServiceClient client2 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client2.getServerIp() + ":" + client2.getServerPort());
            JobThriftServiceClient client3 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client3.getServerIp() + ":" + client3.getServerPort());
            JobThriftServiceClient client4 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client4.getServerIp() + ":" + client4.getServerPort());
            JobThriftServiceClient client5 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client5.getServerIp() + ":" + client5.getServerPort());
            System.out.println("------------");
            System.out.println("--" + client1.listAllJobDetail().size());
            System.out.println("----" + client2.listAllJobDetail().size());
            System.out.println("------" + client3.listAllJobDetail().size());
            System.out.println("--------" + client4.listAllJobDetail().size());
            System.out.println("-----------" + client5.listAllJobDetail().size());
        }
        System.in.read();
        System.in.read();
        for (int i = 0; i < 1; i++)
        {
            JobThriftServiceClient client1 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client1.getServerIp() + ":" + client1.getServerPort());
            JobThriftServiceClient client2 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client2.getServerIp() + ":" + client2.getServerPort());
            JobThriftServiceClient client3 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client3.getServerIp() + ":" + client3.getServerPort());
            JobThriftServiceClient client4 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client4.getServerIp() + ":" + client4.getServerPort());
            JobThriftServiceClient client5 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client5.getServerIp() + ":" + client5.getServerPort());
            System.out.println("------------");
            System.out.println("--" + client1.listAllJobDetail().size());
            System.out.println("----" + client2.listAllJobDetail().size());
            System.out.println("------" + client3.listAllJobDetail().size());
            System.out.println("--------" + client4.listAllJobDetail().size());
            System.out.println("-----------" + client5.listAllJobDetail().size());
        }
        System.in.read();
        System.in.read();
        for (int i = 0; i < 1; i++)
        {
            JobThriftServiceClient client1 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client1.getServerIp() + ":" + client1.getServerPort());
            JobThriftServiceClient client2 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client2.getServerIp() + ":" + client2.getServerPort());
            JobThriftServiceClient client3 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client3.getServerIp() + ":" + client3.getServerPort());
            JobThriftServiceClient client4 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client4.getServerIp() + ":" + client4.getServerPort());
            JobThriftServiceClient client5 = factory.getProxy(JobThriftServiceClient.class);
            //System.out.println("server: " + client5.getServerIp() + ":" + client5.getServerPort());
            System.out.println("------------");
            System.out.println("--" + client1.listAllJobDetail().size());
            System.out.println("----" + client2.listAllJobDetail().size());
            System.out.println("------" + client3.listAllJobDetail().size());
            System.out.println("--------" + client4.listAllJobDetail().size());
            System.out.println("-----------" + client5.listAllJobDetail().size());
        }
        System.in.read();
        System.in.read();
        // for (JobInfo info : list)
        // {
        // System.out.println(info.beanName);
        // }
    }
}
