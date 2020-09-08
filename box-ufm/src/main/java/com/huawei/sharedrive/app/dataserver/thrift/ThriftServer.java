package com.huawei.sharedrive.app.dataserver.thrift;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.server.TThreadedSelectorServer.Args;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

/**
 * Thrift Server
 * 
 * @author c90006080
 */
public class ThriftServer implements Runnable
{
    private static final int SELECTOR_THREADS = Integer.parseInt(PropertiesUtils.getProperty("thrift.server.maxSelectorThreads", "8"));
    private static final int WORKER_THREADS = Integer.parseInt(PropertiesUtils.getProperty("thrift.server.maxWorkerThreads", "300"));
    private static final int TIME_OUT = Integer.parseInt(PropertiesUtils.getProperty("thrift.server.timeout", "60000"));
    private static Logger logger = LoggerFactory.getLogger(ThriftServer.class);
    
    private String bindAddr;
    
    private int port;
    
    private Map<String, TProcessor> processorMap = new HashMap<String, TProcessor>(BusinessConstants.INITIAL_CAPACITIES);
    
    private TServer server;
    
    public void destroy()
    {
        server.stop();
    }
    
    public String getBindAddr()
    {
        return bindAddr;
    }
    
    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }
    
    /**
     * @return the processorMap
     */
    public Map<String, TProcessor> getProcessorMap()
    {
        return processorMap;
    }
    
    @Override
    public void run()
    {
        try
        {
            TNonblockingServerSocket serverSocket = createServerSocket();
            
            TMultiplexedProcessor processor = new TMultiplexedProcessor();
            for (Map.Entry<String, TProcessor> entry : processorMap.entrySet())
            {
                
                processor.registerProcessor(entry.getKey(), entry.getValue());
            }
            
            Args args = new Args(serverSocket);
            args.processor(processor);
            args.selectorThreads(SELECTOR_THREADS);
            args.workerThreads(WORKER_THREADS);
            args.maxReadBufferBytes = 1024*1024;//设置最大缓存为1M，防止非法的请求（如HTTP请求）连接到该端口后，会导致读内存分配过大，出现OOM。
            server = new TThreadedSelectorServer(args);
            logger.info(">>>>>> Thrift server start...");
            server.serve();
        }
        catch (Exception e)
        {
            logger.error("Thrift server start failed!", e);
        }
    }
    
    public void setBindAddr(String bindAddr)
    {
        this.bindAddr = bindAddr;
    }
    
    /**
     * @param port the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }
    
    /**
     * @param processorMap the processorMap to set
     */
    public void setProcessorMap(Map<String, TProcessor> processorMap)
    {
        this.processorMap = processorMap;
    }
    
    public void start() throws TTransportException
    {
        new Thread(this).start();
    }
    
    private TNonblockingServerSocket createServerSocket() throws TTransportException
    {
        if (StringUtils.isBlank(this.bindAddr))
        {
            return new TNonblockingServerSocket(port);
        }
        return new TNonblockingServerSocket(new InetSocketAddress(bindAddr, port), TIME_OUT);
    }
}
