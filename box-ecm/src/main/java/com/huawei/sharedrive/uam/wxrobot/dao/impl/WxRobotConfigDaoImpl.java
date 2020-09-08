package com.huawei.sharedrive.uam.wxrobot.dao.impl;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.huawei.sharedrive.uam.wxrobot.dao.WxRobotConfigDao;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobotConfig;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
public class WxRobotConfigDaoImpl  extends AbstractDAOImpl implements WxRobotConfigDao{

	@Override
	public List<WxRobotConfig> listByRobotId(long robotId) {
		// TODO Auto-generated method stub
		return sqlMapClientTemplate.queryForList("WxRobotConfig.listByRobotId",robotId);
	}

	@Override
	public void cerate(WxRobotConfig wxRobotConfig) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("WxRobotConfig.insert",wxRobotConfig);
	}

	@Override
	public void delete(WxRobotConfig wxRobotConfig) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("WxRobotConfig.delete",wxRobotConfig);
	}

	@Override
	public void updateConfig(WxRobotConfig wxRobotConfig) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("WxRobotConfig.update",wxRobotConfig);
	}

}
