package pw.cdmi.box.disk.core.service;

import java.nio.ByteBuffer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.exception.InternalServerErrorException;


public class IdGenService
{
    private static final String BASE_PATH = "/IDGEN";
    
    private IdGenCallback callback;
    
    private CuratorFramework client;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IdGenService.class);
    
    public IdGenService(CuratorFramework client, IdGenCallback callback)
    {
        this.client = client;
        this.callback = callback;
        
        try
        {
            Stat stat = client.checkExists().forPath(BASE_PATH);
            if (stat == null)
            {
                client.create().forPath(BASE_PATH);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("fail in IdGenService", e);
            throw new IllegalArgumentException("fail in IdGenService", e);
        }
    }
    
    public void deletePath(String path)
    {
        try
        {
            client.delete().forPath(BASE_PATH + '/' + path);
            client.delete().forPath(BASE_PATH + '/' + path + "-start");
        }
        catch (Exception e)
        {
            LOGGER.error("fail in deletePath", e);
            throw new InternalServerErrorException("fail in deletePath", e);
        }
    }
    
    public long getId(String path)
    {
        long start = getStart(path);
        try
        {
            String idPath = client.create()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath(BASE_PATH + '/' + path + "/id-");
            long id = Long.parseLong(idPath.substring(idPath.indexOf("-") + 1));
            client.delete().forPath(idPath);
            return start + id;
        }
        catch (Exception e)
        {
            LOGGER.error("fail in getId", e);
            throw new InternalServerErrorException("fail in getId", e);
        }
    }
    
    private long bytesToLong(byte[] bytes)
    {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();// need flip
        return buffer.getLong();
    }
    
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.PreserveStackTrace"})
    private long getStart(String path)
    {
        Long start;
        try
        {
            byte[] data = client.getData().forPath(BASE_PATH + '/' + path + "-start");
            start = bytesToLong(data);
        }
        catch (KeeperException.NoNodeException e)
        {
            start = callback.getStart(path);
            try
            {
                client.create().forPath(BASE_PATH + '/' + path);
                client.create().forPath(BASE_PATH + '/' + path + "-start", longToBytes(start));
            }
            catch (KeeperException.NodeExistsException e1)
            {
                LOGGER.error("fail in getStart", e1);
                return getStart(path);
            }
            catch (Exception e1)
            {
                LOGGER.error("fail in getStart", e1);
                throw new InternalServerErrorException("fail in getStart", e1);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("fail in getStart", e);
            throw new InternalServerErrorException(e);
        }
        return start;
    }
    
    private byte[] longToBytes(long x)
    {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }
}
