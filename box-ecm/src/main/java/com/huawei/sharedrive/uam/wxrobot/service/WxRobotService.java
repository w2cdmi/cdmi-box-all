package com.huawei.sharedrive.uam.wxrobot.service;

import java.util.List;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobot;

public interface WxRobotService {


	void create(WxRobot wxRobot);

	void updateRobot(WxRobot robot);
	
	List<WxRobot> listByCloudUserId(Long cloudUserId, long accountId);

	WxRobot getByUinAndUser(WxRobot robot);

	void updateRobotStatus(WxRobot robot);

	List<WxRobot> listRuning(byte status, int offset, int limit);

	long getNextRobotId();

	void stopRobot(long robotId);

	

}
