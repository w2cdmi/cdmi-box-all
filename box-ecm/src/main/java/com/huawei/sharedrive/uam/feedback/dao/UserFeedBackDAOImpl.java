package com.huawei.sharedrive.uam.feedback.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.feedback.domain.QueryUserFeedBackCondition;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackCreateRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackDetail;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackSubInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListRequest;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service
@SuppressWarnings({ "unchecked", "deprecation" })
public class UserFeedBackDAOImpl extends AbstractDAOImpl implements UserFeedBackDAO {
	public static final int TABLE_COUNT = 100;

	private static Logger logger = LoggerFactory.getLogger(UserFeedBackDAOImpl.class);

	@Override
	public List<RestFeedBackInfo> queryUserFeedBackList(QueryUserFeedBackCondition condition) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", condition);
		map.put("order", condition.getPageRequest().getOrder());
		map.put("limit", condition.getPageRequest().getLimit());
		/*
		 * System.out.println(condition.getPageRequest().getLimit().getOffset())
		 * ;
		 * System.out.println(condition.getPageRequest().getLimit().getLength())
		 * ;
		 */
		List<RestFeedBackInfo> result = sqlMapClientTemplate.queryForList("UserFeedBack.queryUserFeedBackByPage", map);

		return result;
	}

	@Override
	public int countUserFeedBack(QueryUserFeedBackCondition condition) {

		logger.info("Enter countUserFeedBack,condition={}", condition);

		Map<String, Object> map = new HashMap<String, Object>(1);

		map.put("filter", condition);

		Object count = sqlMapClientTemplate.queryForObject("UserFeedBack.countFeedBack", map);
		if (null == count) {
			logger.error("no search feedBack");
			throw new InvalidParamterException();
		}
		return (int) count;

	}

	@Override
	public void logicDeleteUserFeedBack(long problemID) {
		RestFeedBackInfo queryForObject = (RestFeedBackInfo)sqlMapClientTemplate.queryForObject("UserFeedBack.get", problemID);
		Map<String,Object> map = new HashMap<>();
		map.put("problemID",problemID );
		if(queryForObject!=null&&queryForObject.getProblemStatus().equals("3")){
			map.put("problemStatus", "5");
		}else{
			map.put("problemStatus", "4");
		}
		sqlMapClientTemplate.update("UserFeedBack.updateStatus", map);
	}

	@Override
	public void deleteUserFeedBackSub(long problemID) {
		sqlMapClientTemplate.delete("UserFeedBack.deleteSubByID", problemID);

	}

	@Override
	public void addUserFeedBack(RestFeedBackCreateRequest restFeedBackCreateRequest) {
		sqlMapClientTemplate.insert("UserFeedBack.insert", restFeedBackCreateRequest);

	}

	@Override
	public void updateUserFeedBack(RestFeedBackInfo restFeedBackInfo) {

		sqlMapClientTemplate.update("UserFeedBack.update", restFeedBackInfo);
	}

	@Override
	public RestFeedBackInfo getFeedBackByID(long problemID) {

		return (RestFeedBackInfo) sqlMapClientTemplate.queryForObject("UserFeedBack.get", problemID);
	}

	@Override
	public List<RestFeedBackSubInfo> getFeedBackSubList(long problemID) {
		return sqlMapClientTemplate.queryForList("UserFeedBack.querySubList", problemID);
	}

	@Override
	public void updateTeedBackTime(RestFeedBackDetail feedBackDetail) {
		sqlMapClientTemplate.update("UserFeedBack.updateTime", feedBackDetail);
	}

	@Override
	public void addNewFeedBackSub(RestFeedBackSubInfo restFeedBackInfo) {
		sqlMapClientTemplate.insert("UserFeedBack.addNewFeedBackSub", restFeedBackInfo);

	}

	@Override
	public List<RestFeedBackInfo> queryRestUserFeedBackList(RestUserFeedBackListRequest condition) {

		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put("filter", condition);
		try {
			
			List<RestFeedBackInfo> result = sqlMapClientTemplate.queryForList("UserFeedBack.queryRestFeedBack", map);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

return null;
	}

	@Override
	public int countRestUserFeedBack(RestUserFeedBackListRequest condition) {
		logger.info("Enter countRestUserFeedBack,condition={}", condition);

		Map<String, Object> map = new HashMap<String, Object>(1);

		map.put("filter", condition);

		Object count = sqlMapClientTemplate.queryForObject("UserFeedBack.countRestFeedBack", map);
		if (null == count) {
			logger.error("no search feedBack");
			throw new InvalidParamterException();
		}
		return (int) count;
	}

	@Override
	public EnterpriseUser countRestUserFeedBack(long id, long enterpriseId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("userId", id);
		map.put("enterpriseId", enterpriseId);
		EnterpriseUser enterpriseUser = (EnterpriseUser) sqlMapClientTemplate.queryForObject("EnterpriseUser.get", map);
		return enterpriseUser;
	}

	public static String getTableSuffix(long enterpriseId) {
		String tableSuffix = null;
		int table = (int) (HashTool.apply(String.valueOf(enterpriseId)) % TABLE_COUNT);
		if (table > 0) {
			tableSuffix = "_" + table;
		}
		return tableSuffix;
	}

	@Override
	public void physicsDeleteUserFeedBack(long problemID) {
		sqlMapClientTemplate.delete("UserFeedBack.deleteByID", problemID);
	}
}
