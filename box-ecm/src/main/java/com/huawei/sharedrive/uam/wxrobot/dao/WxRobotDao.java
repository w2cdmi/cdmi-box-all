package com.huawei.sharedrive.uam.wxrobot.dao;

import java.util.List;

import com.huawei.sharedrive.uam.wxrobot.domain.WxRobot;

public interface WxRobotDao {


	void create(WxRobot wxRobot);

	WxRobot getByUinAndUser(WxRobot wxRobot);

	List<WxRobot> listByCloudUserId(Long cloudUserId, long accountId);

	void updateRobotStatus(WxRobot wxRobot);

	void updateRobot(WxRobot wxRobot);

	void deleteRobot(WxRobot wxRobot);

	List<WxRobot> listRuning(byte status, int offset, int limit);

	long getMaxUserId();

	void stopRobot(long robotId);

}
