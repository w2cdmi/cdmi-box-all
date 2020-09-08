package com.huawei.sharedrive.uam.wxrobot.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.huawei.sharedrive.uam.wxrobot.dao.WxRobotDao;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobot;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
public class WxRobotDaoImpl extends AbstractDAOImpl implements WxRobotDao{

	public static final int TABLE_COUNT = 1;
	@Override
	public void create(WxRobot WxRobot) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("WxRobot.insert",WxRobot);
	}

	@Override
	public WxRobot getByUinAndUser(WxRobot robot) {
		// TODO Auto-generated method stub
		return (WxRobot) sqlMapClientTemplate.queryForObject("WxRobot.getByUinAndUser",robot);
	}

	@Override
	public List<WxRobot> listByCloudUserId(Long cloudUserId, long accountId) {
		// TODO Auto-generated method stub
		Map<String, Object> prameter=new HashMap<>();
		prameter.put("cloudUserId", cloudUserId);
		prameter.put("accountId", accountId);
		return sqlMapClientTemplate.queryForList("WxRobot.listByCloudUserId",prameter);
	}

	@Override
	public List<WxRobot> listRuning(byte status,int offset,int limit) {
		// TODO Auto-generated method stub
		Map<String, Object> prameter=new HashMap<>();
		prameter.put("status", status);
		prameter.put("offset", offset);
		prameter.put("limit", limit);
		return sqlMapClientTemplate.queryForList("WxRobot.listRuning",prameter);
	}

	@Override
	public void updateRobotStatus(WxRobot robot) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("WxRobot.updateRobotStatus",robot);
	}

	@Override
	public void updateRobot(WxRobot robot) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("WxRobot.updateRobot",robot);
	}

	@Override
	public void deleteRobot(WxRobot robot) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.delete("WxRobot.deleteRobot",robot);
	}

	@Override
	public long getMaxUserId() {

		WxRobot wxRobot = new WxRobot();
		long max = 1L;
		long temp = 0L;
		Object maxUserId = null;
        for (int i = 0; i < TABLE_COUNT; i++) {
            wxRobot.setTableSuffix(i);
			maxUserId = sqlMapClientTemplate.queryForObject("WxRobot.getMaxRobotId", wxRobot);
			temp = (maxUserId == null) ? 0L : (long) maxUserId;
            if (temp > max) {
				max = temp;
			}
		}
		return max;
	
	}

	@Override
	public void stopRobot(long id) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("WxRobot.stopRobot",id);
	}
	

}
