package com.huawei.sharedrive.uam.feedback.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.feedback.dao.UserFeedBackDAO;
import com.huawei.sharedrive.uam.feedback.domain.QueryUserFeedBackCondition;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackCreateRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackDetail;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackSubInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListRequest;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;


@Component
public class UserFeedBackServiceImpl implements UserFeedBackService
{


     @Autowired
     private UserFeedBackDAO feedBackDAO;


     @Override
     public List <RestFeedBackInfo> queryUserFeedBackList(QueryUserFeedBackCondition condition)
     {

          return feedBackDAO.queryUserFeedBackList(condition);
     }


     @Override
     public int countUserFeedBack(QueryUserFeedBackCondition condition)
     {
          return feedBackDAO.countUserFeedBack(condition);
     }


     @Override
     public void logicDeleteUserFeedBack(long problemID)
     {
          feedBackDAO.logicDeleteUserFeedBack(problemID);

     }
     @Override
     public void physicsDeleteUserFeedBack(long problemID)
     {
    	 feedBackDAO.physicsDeleteUserFeedBack(problemID);
    	 
     }


     @Override
     public void addUserFeedBack(RestFeedBackCreateRequest restFeedBackCreateRequest)
     {
          feedBackDAO.addUserFeedBack(restFeedBackCreateRequest);

     }


     @Override
     public void updateUserFeedBack(RestFeedBackInfo restFeedBackInfo)
     {
          feedBackDAO.updateUserFeedBack(restFeedBackInfo);

     }


     @Override
     public Page <RestFeedBackInfo> queryUserFeedBackByPage(QueryUserFeedBackCondition condition)
     {

          int total = feedBackDAO.countUserFeedBack(condition);

          List <RestFeedBackInfo> content = feedBackDAO.queryUserFeedBackList(condition);

          Page <RestFeedBackInfo> page = new PageImpl <RestFeedBackInfo>(content , condition.getPageRequest() , total);

          return page;
     }


     @Override
     public RestFeedBackInfo getFeedBackByID(long problemID)
     {

          return feedBackDAO.getFeedBackByID(problemID);
     }


     @Override
     public void deleteUserFeedBackSub(long problemID)
     {
          feedBackDAO.deleteUserFeedBackSub(problemID);

     }


     @Override
     public List <RestFeedBackSubInfo> getFeedBackSubList(long problemID)
     {
          return feedBackDAO.getFeedBackSubList(problemID);
     }


     @Override
     public void updateTeedBackTime(RestFeedBackDetail feedBackDetail)
     {
          feedBackDAO.updateTeedBackTime(feedBackDetail);

     }


     @Override
     public void addNewFeedBackSub(RestFeedBackSubInfo restFeedBackInfo)
     {
          feedBackDAO.addNewFeedBackSub(restFeedBackInfo);

     }


	@Override
	public List<RestFeedBackInfo> queryRestUserFeedBackList(RestUserFeedBackListRequest condition) {
	
		return feedBackDAO.queryRestUserFeedBackList(condition);
	}


	@Override
	public int countRestUserFeedBack(RestUserFeedBackListRequest condition) {
		
		return feedBackDAO.countRestUserFeedBack(condition);
	}


	@Override
	public EnterpriseUser getByIdAndEnterprise(long id, long enterpriseId) {

		return feedBackDAO.countRestUserFeedBack(id,enterpriseId);
	}

}
