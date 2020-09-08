/**
 * 
 */
package com.huawei.sharedrive.app.test.thrift;

import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.common.thrift.client.pool.TTransportManager;

/**
 * @author q90003805
 *         
 */
public class TestThriftClientMain
{
    
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
//        TTransportManager manager = new TTransportManager();
//        manager.setMaxActive(2);
//        manager.setMaxIdle(2);
//        manager.setMaxWait(20000);
//        
//        manager.setMinIdle(2);
//        
//        manager.setServiceAddressManager(new TestServiceAddressManager());
//        manager.setTestOnBorrow(false);
//        manager.setTestOnReturn(false);
//        manager.setTestWhileIdle(true);
//        manager.setNumTestsPerEvictionRun(10);
//        manager.setTimeBetweenEvictionRunsMillis(10000);
//        manager.setMinEvictableIdleTimeMillis(300000);
//        manager.setTransportTimout(60000);
//        manager.setUseSSL(false);
//        manager.setKeyStoreFile("com/huawei/sharedrive/app/test/thrift/ufm_thrift.keystore");
//        manager.setKeyStorePwd(
//            "d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzMxNDM0NjM5NDIzMjMzMzU0MzQ1MzgzNDMxNDMzODM4MzQzNjQ0MzAzNjM1NDQzODQ1NDM0NDQ0MzIzOTM1Mzk7OzM1MzAzMDMwMzA7MjE5OEMwNEQyMjE5Q0RCODU0MkE3NEZEMUJFMkNFN0Y7NjFDQjNBNTU4NEQ1MDQ1OTs");
//        manager.setKeyStorePwdKey(
//            "d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzM1MzEzMDMyNDQ0NDQzMzEzMzM2MzkzMzMzMzM0NTMxMzE0MTM3MzIzNTQyMzk0NjMxNDQ0NjM2MzUzODM0MzE0MjM5MzMzOTMyMzA0MjMwNDEzMzMyNDMzNDQyMzkzMzQ1MzY0MTMwNDYzMDQxMzc0NDM3Mzg0NjQ0MzQ0MzMzMzc0MzM1MzkzMTM3MzE0NTQ0NDQzNjQ0MzAzOTM5NDQzNDM3MzgzMzM2NDM0NjQyMzU0NTQ0MzMzMjQ1MzQzMzszMTM0MzUzMjMzMzMzMTM2MzYzODM3MzAzNDszNTMwMzAzMDMwOzU4MUM3NTJCRjlEQzc3M0U1OTkyMjJGMEU1RTMwNTk3Ow");
//        manager.setTrustStoreFile("com/huawei/sharedrive/app/test/thrift/ufm_thrift.truststore");
//        manager.setTrustStorePwd(
//            "d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzMxNDM0NjM5NDIzMjMzMzU0MzQ1MzgzNDMxNDMzODM4MzQzNjQ0MzAzNjM1NDQzODQ1NDM0NDQ0MzIzOTM1Mzk7OzM1MzAzMDMwMzA7MjE5OEMwNEQyMjE5Q0RCODU0MkE3NEZEMUJFMkNFN0Y7NjFDQjNBNTU4NEQ1MDQ1OTs");
//        manager.setTrustStorePwdKey(
//            "d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzM1MzEzMDMyNDQ0NDQzMzEzMzM2MzkzMzMzMzM0NTMxMzE0MTM3MzIzNTQyMzk0NjMxNDQ0NjM2MzUzODM0MzE0MjM5MzMzOTMyMzA0MjMwNDEzMzMyNDMzNDQyMzkzMzQ1MzY0MTMwNDYzMDQxMzc0NDM3Mzg0NjQ0MzQ0MzMzMzc0MzM1MzkzMTM3MzE0NTQ0NDQzNjQ0MzAzOTM5NDQzNDM3MzgzMzM2NDM0NjQyMzU0NTQ0MzMzMjQ1MzQzMzszMTM0MzUzMjMzMzMzMTM2MzYzODM3MzAzNDszNTMwMzAzMDMwOzU4MUM3NTJCRjlEQzc3M0U1OTkyMjJGMEU1RTMwNTk3Ow");
//        manager.setCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_CBC_SHA"});
//        manager.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.1"});
//        manager.setHeartBeatServiceName("ChechService");
//        manager.initPool();
//        
//        ThriftClientProxyFactory factory = new ThriftClientProxyFactory();
//        factory.setTransportManager(manager);
//        
//        TestStorageResourceServiceClient client1 = factory.getProxy(TestStorageResourceServiceClient.class);
//        client1.deleteStorageResource("test");
//        // Thread.sleep(20000);
//        for(int i=0;i<100;i++){
//            TestDCThriftServiceClient client2 = factory.getProxy(TestDCThriftServiceClient.class);
//            client2.reset();
//            client1 = factory.getProxy(TestStorageResourceServiceClient.class);
//            client1.deleteStorageResource("test");
//        }
//        
//        
//        // Thread.sleep(20000);
//        client1 = factory.getProxy(TestStorageResourceServiceClient.class);
//        System.out.println(client1.getAllStorageResource().size());
//        
//        System.in.read();
//        
//        client1 = factory.getProxy(TestStorageResourceServiceClient.class);
//        System.out.println(client1.getAllStorageResource().size());
//        
//        System.in.read();
//        
//        System.in.read();
//        System.in.read();
//        
//        manager.close();
        
    }
    
}
