package com.huawei.sharedrive.app.teamspace.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceMembershipsDAO;
import com.huawei.sharedrive.app.teamspace.service.SpaceMemberIdGenerateService;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * SpaceMember Id生成器，按团队空间做自增，zk中无数据时，自动使用TeamSpaceMember表中最大值作为初始种子
 * 
 * @author t00159390
 * 
 */
@Component
public class SpaceMemberIdGenerateServiceImpl implements SpaceMemberIdGenerateService, SeedInitializer
{
    private static final String BASE_PATH = "/space_member_id";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private TeamSpaceMembershipsDAO teamSpaceMembershipsDAO;
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @PostConstruct
    public void init() throws Exception
    {
        client = zookeeperServer.getClient();
        sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return teamSpaceMembershipsDAO.getMaxMembershipsId(Long.parseLong(subPath));
    }
    
    @Override
    public long getNextMemberId(long ownerBy)
    {
        return sequenceGenerator.getSequence(String.valueOf(ownerBy));
    }
    
    @Override
    public void delete(long cloudUserId)
    {
        sequenceGenerator.delete(String.valueOf(cloudUserId));
    }
}
