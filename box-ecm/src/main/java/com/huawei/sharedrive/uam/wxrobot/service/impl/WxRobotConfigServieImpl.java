package com.huawei.sharedrive.uam.wxrobot.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.wxrobot.dao.WxRobotConfigDao;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobotConfig;
import com.huawei.sharedrive.uam.wxrobot.service.WxRobotConfigService;

@Service
public class WxRobotConfigServieImpl  implements WxRobotConfigService{
	
	@Autowired
	WxRobotConfigDao wxRobotConfigDaoImpl;

	@Override
	public List<WxRobotConfig> listByRobotId(long robotId) {
		// TODO Auto-generated method stub
		return wxRobotConfigDaoImpl.listByRobotId(robotId);
	}

	@Override
	public void cerate(WxRobotConfig wxRobotConfig) {
		// TODO Auto-generated method stub
		wxRobotConfigDaoImpl.cerate(wxRobotConfig);
	}

	@Override
	public void delete(WxRobotConfig wxRobotConfig) {
		// TODO Auto-generated method stub
		wxRobotConfigDaoImpl.delete(wxRobotConfig);
	}

	@Override
	public void updateConfig(WxRobotConfig wxRobotConfig) {
		// TODO Auto-generated method stub
		wxRobotConfigDaoImpl.updateConfig(wxRobotConfig);
	}
	
}
