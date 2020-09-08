package com.huawei.sharedrive.uam.wxrobot.dao;

import java.util.List;

import com.huawei.sharedrive.uam.wxrobot.domain.WxRobot;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobotConfig;

public interface WxRobotConfigDao {

	List<WxRobotConfig> listByRobotId(long robotId);
	
	void delete (WxRobotConfig wxRobotConfig);
	
	void cerate(WxRobotConfig wxRobotConfig);

	void updateConfig(WxRobotConfig wxRobotConfig);

}
