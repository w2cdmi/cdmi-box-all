package com.huawei.sharedrive.uam.wxrobot.service;

import java.util.List;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobot;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobotConfig;

public interface WxRobotConfigService {
	
	List<WxRobotConfig> listByRobotId(long robotId);
	
	void delete (WxRobotConfig wxRobotConfig);
	
	void cerate(WxRobotConfig wxRobotConfig);

	void updateConfig(WxRobotConfig wxRobotConfig);


}
