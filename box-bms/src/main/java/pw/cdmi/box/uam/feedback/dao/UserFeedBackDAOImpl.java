package pw.cdmi.box.uam.feedback.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.feedback.domain.QueryUserFeedBackCondition;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackCreateRequest;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackDetail;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackInfo;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackSubInfo;
import pw.cdmi.box.uam.feedback.domain.RestUserFeedBackListRequest;
import pw.cdmi.core.utils.HashTool;

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
		RestFeedBackInfo feedBackByID = getFeedBackByID(problemID);
		Map<String,Object> map = new HashMap<>();
		map.put("problemID", problemID);
		if ("4".equals(feedBackByID.getProblemStatus())) {
			map.put("poblemStatus", "5");
		} else {
			map.put("poblemStatus", "3");
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


	public static String getTableSuffix(long enterpriseId) {
		String tableSuffix = null;
		int table = (int) (HashTool.apply(String.valueOf(enterpriseId)) % TABLE_COUNT);
		if (table > 0) {
			tableSuffix = "_" + table;
		}
		return tableSuffix;
	}

	@Override
	public void physicsDeleteUserFeedBack(Date date) {
		sqlMapClientTemplate.delete("UserFeedBack.physicsDeleteFeedBack", date);
	}
	
	@Override
	public List<RestFeedBackInfo> queryDeleteFeedBackSub(Date date) {

		try {

			List<RestFeedBackInfo> result = sqlMapClientTemplate.queryForList("UserFeedBack.queryDeleteFeedBackSub", date);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
}
