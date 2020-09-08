package com.huawei.sharedrive.uam.idgenerate.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.idgenerate.service.TerminalIdGenerateService;
import com.huawei.sharedrive.uam.idgenerate.util.IdGenerateUtil;
import com.huawei.sharedrive.uam.terminal.dao.TerminalDAO;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class TerminalIdGenerateServiceImpl implements SeedInitializer, TerminalIdGenerateService
{
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Value("${zk.root.path}")
    private String rootPath;
    
    @Autowired
    private TerminalDAO terminalDAO;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public long getNextTerminalId()
    {
        return sequenceGenerator.getSequence(IdGenerateUtil.TERMINAL_USER_SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return terminalDAO.getMaxTerminalId();
    }
    
    @PostConstruct
    public void init() throws InternalServerErrorException
    {
        try
        {
            client = zookeeperServer.getClient();
            sequenceGenerator = new SequenceGenerator(client, this, rootPath
                + IdGenerateUtil.TERMINAL_BASE_PATH);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
    }
}
