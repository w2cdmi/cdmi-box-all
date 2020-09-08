/**
 * 
 */
package com.huawei.sharedrive.app.user.service.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.openapi.rest.AsyncContextWrapper;
import com.huawei.sharedrive.app.user.domain.UserSyncVersion;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * @author q90003805
 *         
 */
@Component("userSyncVersionService")
@Lazy(false)
public class UserSyncVersionServiceImpl implements UserSyncVersionService, SeedInitializer
{
    private class PackageReceiver implements Runnable
    {
        @Override
        public void run()
        {
            while (running)
            {
                try
                {
                    doTask();
                }
                catch (SocketException e)
                {
                    if (running && !"socket closed".equals(e.getMessage()))
                    {
                        LOGGER.error("receive user sync version error", e);
                    }
                }
                catch (Exception tx)
                {
                    LOGGER.warn("receive user sync version error", tx);
                }
            }
            
        }
        
        private void doTask() throws IOException, InvalidProtocolBufferException, InterruptedException
        {
            receiveMulticast.receive(receivePacket);
            UserSyncVersion userSyncVersion = UserSyncVersionParser.parseFromBytes(receivePacket.getData(),
                receivePacket.getLength());
                
            notifyVersionChanged(userSyncVersion.getUserId(), userSyncVersion.getSyncVersion());
            Thread.sleep(0);
        }
    }
    
    private static final String BASE_PATH = "/user_synv_version";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncVersionServiceImpl.class);
    
    private static final int RECEIVE_BUFFER_SIZE = 16 * 1024 * 1024;
    
    private static final int RECEIVE_LENGTH = 1024;
    
    private CuratorFramework client;
    
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, AsyncContextWrapper>> CONTEXT_MAP = new ConcurrentHashMap<Long, ConcurrentHashMap<String, AsyncContextWrapper>>();
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Value("${user.sync.version.multicast.port}")
    private int multicasePort;
    
    @Value("${user.sync.version.multicast.host}")
    private String multicastHost;
    
    private MulticastSocket receiveMulticast;
    
    private DatagramPacket receivePacket = new DatagramPacket(new byte[RECEIVE_LENGTH], RECEIVE_LENGTH);
    
    private boolean running;
    
    @Value("${self.privateAddr}")
    private String selfPrivateIp;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public void completeContext(long userId, String clientId)
    {
        Map<String, AsyncContextWrapper> map = CONTEXT_MAP.get(userId);
        if (map != null)
        {
            AsyncContextWrapper context = map.remove(clientId);
            if (context != null)
            {
                context.complete();
            }
        }
    }
    
    @Override
    public void delete(long userId)
    {
        sequenceGenerator.delete(String.valueOf(userId));
    }
    
    @PreDestroy
    public void destory()
    {
        running = false;
        if (receiveMulticast != null)
        {
            receiveMulticast.close();
        }
    }
    
    @Override
    public long getNextUserSyncVersion(long userId)
    {
        return sequenceGenerator.getSequence(String.valueOf(userId));
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return iNodeDAO.getMaxSyncVersion(Long.parseLong(subPath));
    }
    
    @Override
    public long getUserCurrentSyncVersion(long userId)
    {
        return iNodeDAO.getMaxSyncVersion(userId);
    }
    
    @PostConstruct
    public void init()
    {
        InetAddress multicastAddress = null;
        try
        {
            multicastAddress = InetAddress.getByName(multicastHost);
        }
        catch (UnknownHostException e)
        {
            throw new IllegalStateException(multicastHost + " is a unknow host", e);
        }
        if (!multicastAddress.isMulticastAddress())
        {
            throw new IllegalArgumentException(multicastHost + " is not a multicast address");
        }
        try
        {
            receiveMulticast = new MulticastSocket(multicasePort);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("can not init multicast socket for " + multicasePort, e);
        }
        try
        {
            receiveMulticast.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);
        }
        catch (SocketException e)
        {
            throw new IllegalStateException("can not init receive buffer for size " + RECEIVE_BUFFER_SIZE, e);
        }
        try
        {
            receiveMulticast.joinGroup(multicastAddress);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("can not join multicast address for " + multicastHost, e);
        }
        client = zookeeperServer.getClient();
        try
        {
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("can not init sequenceGenerator for " + BASE_PATH, e);
        }
        
        running = true;
        Thread t = new Thread(new PackageReceiver(), "User Sync Version Receive Thread");
        t.start();
        LOGGER.info("start receive multicast package from " + multicastHost + ':' + multicasePort);
    }
    
    @Override
    public void notifyUserCurrentSyncVersionChanged(long userId, long newVersion) throws IOException
    {
        UserSyncVersion version = new UserSyncVersion();
        version.setUserId(userId);
        version.setSyncVersion(newVersion);
        byte[] data = UserSyncVersionParser.toBytes(version);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(selfPrivateIp, 0);
        InetAddress destAddress = InetAddress.getByName(multicastHost);
        MulticastSocket multiSocket = null;
        try
        {
            multiSocket = new MulticastSocket(inetSocketAddress);
            DatagramPacket dp = new DatagramPacket(data, data.length, destAddress, multicasePort);
            multiSocket.send(dp);
        }
        finally
        {
            if (multiSocket != null)
            {
                multiSocket.close();
            }
        }
    }
    
    @Override
    public void registContext(AsyncContextWrapper context)
    {
        long userId = context.getUserId();
        String clientId = context.getClientId();
        ConcurrentHashMap<String, AsyncContextWrapper> map = new ConcurrentHashMap<String, AsyncContextWrapper>();
        ConcurrentHashMap<String, AsyncContextWrapper> oldMap = CONTEXT_MAP.putIfAbsent(userId, map);
        if (oldMap != null)
        {
            map = oldMap;
        }
        map.put(clientId, context);
    }
    
    private void notifyVersionChanged(long userId, long newVersion)
    {
        Map<String, AsyncContextWrapper> map = CONTEXT_MAP.remove(userId);
        if (map == null)
        {
            return;
        }
        for (AsyncContextWrapper context : map.values())
        {
            try
            {
                context.getResponse().getWriter().println(newVersion);
                LOGGER.info("notifyVersionChanged end, userid: " + userId + ", newVersion:" + newVersion
                    + ", srcip:" + receivePacket.getAddress().getHostAddress());
            }
            catch (IOException e)
            {
                LOGGER.warn("error occur when write notify to client", e);
            }
            context.complete();
        }
    }
    
}
