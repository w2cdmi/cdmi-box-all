package com.huawei.sharedrive.app.test.dataserver.thrift;

import pw.cdmi.common.thrift.ThriftServer;

/**
 * Thrift Server
 *
 * @author c90006080
 */
public class ThriftServerTest {
    public static void main(String[] args) {
        ThriftServer server = new ThriftServer();
        try {
            server.setUseSSL(false);
            server.setPort(13003);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
