package com.huawei.sharedrive.uam.wxrobot.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huawei.sharedrive.uam.wxrobot.dao.WxRobotDao;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobot;
import com.huawei.sharedrive.uam.wxrobot.service.WxRobotService;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;
@Service
public class WxRobotServiceImpl implements SeedInitializer,WxRobotService{
	
    @Autowired
	private WxRobotDao wxRobotDao;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private static final String BASE_PATH = "/wxrobot";
    
    private static final String SUB_PATH = "robotId";
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Override
	public long getNextRobotId()
	{
	        return sequenceGenerator.getSequence(SUB_PATH);
    }
    @PostConstruct
    public void init() throws Exception
    {
        try
        {
            client = zookeeperServer.getClient();
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }
    
	
	@Override
	public void create(WxRobot userRobot) {
		// TODO Auto-generated method stub
		wxRobotDao.create(userRobot);
	}
	@Override
	public void updateRobot(WxRobot robot) {
		// TODO Auto-generated method stub
		wxRobotDao.updateRobotStatus(robot);
		
	}
	@Override
	public void updateRobotStatus(WxRobot robot) {
		// TODO Auto-generated method stub
		wxRobotDao.updateRobotStatus(robot);
		
	}
	@Override
	public List<WxRobot> listByCloudUserId(Long cloudUserId, long accountId) {
		// TODO Auto-generated method stub
		return wxRobotDao.listByCloudUserId(cloudUserId,accountId);
	}
	@Override
	public List<WxRobot> listRuning(byte status, int offset, int limit) {
		// TODO Auto-generated method stub
		return  wxRobotDao.listRuning(status,offset,limit);
	}
	@Override
	public WxRobot getByUinAndUser(WxRobot robot) {
		// TODO Auto-generated method stub
		return wxRobotDao.getByUinAndUser(robot);
	}

	@Override
	public long getSeed(String subPath) {
		// TODO Auto-generated method stub
		return wxRobotDao.getMaxUserId();
	}
	@Override
	public void stopRobot(long robotId) {
		// TODO Auto-generated method stub
		wxRobotDao.stopRobot(robotId);
	}

}
