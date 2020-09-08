package com.huawei.sharedrive.uam.feedback.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.feedback.domain.QueryUserFeedBackCondition;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackCreateRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackDetail;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackSubInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListResponse;
import com.huawei.sharedrive.uam.feedback.service.UserFeedBackService;

import pw.cdmi.box.domain.Page;


@Component
public class UserFeedBackManagerImpl implements UserFeedBackManager
{
     @Autowired
     UserFeedBackService feedBackService;
     
     
     @Override
     public List<RestFeedBackInfo> queryUserFeedBackList(QueryUserFeedBackCondition condition)
     {
         
          List<RestFeedBackInfo> userFeedBackList=feedBackService.queryUserFeedBackList(condition);
         
          return userFeedBackList;
     }


     @Override
     public int countUserFeedBack(QueryUserFeedBackCondition condition)
     {
          int count = feedBackService.countUserFeedBack(condition);
          
          return count;
     }


     @Override
     public void logicDeleteUserFeedBack(long problemID)
     {
          feedBackService.logicDeleteUserFeedBack(problemID);
          
     }
     
     @Override
     public void physcisDeleteUserFeedBack(long problemID)
     {
    	 feedBackService.physicsDeleteUserFeedBack(problemID);
    	 
     }


     @Override
     public void addUserFeedBack(RestFeedBackCreateRequest restFeedBackCreateRequest)
     {
          feedBackService.addUserFeedBack(restFeedBackCreateRequest);
          
     }


     @Override
     public void updateUserFeedBack(RestFeedBackInfo restFeedBackInfo)
     {
          feedBackService.updateUserFeedBack(restFeedBackInfo);
          
     }


     @Override
     public Page <RestFeedBackInfo> queryUserFeedBackByPage(QueryUserFeedBackCondition condition)
     {
          
          return feedBackService.queryUserFeedBackByPage(condition);
     }


     @Override
     public RestFeedBackInfo getFeedBackByID(long problemID)
     {
          return feedBackService.getFeedBackByID(problemID);
     }


     @Override
     public void deleteUserFeedBackSub(long problemID)
     {
          feedBackService.deleteUserFeedBackSub(problemID);
          
     }


     @Override
     public List <RestFeedBackSubInfo> getFeedBackSubList(long problemID)
     {
          return feedBackService.getFeedBackSubList(problemID);
     }


     @Override
     public void updateTeedBackTime(RestFeedBackDetail feedBackDetail)
     {
          feedBackService.updateTeedBackTime(feedBackDetail);
          
     }


     @Override
     public void addNewFeedBackSub(RestFeedBackSubInfo restFeedBackInfo)
     {
          feedBackService.addNewFeedBackSub(restFeedBackInfo);
          
     }


	@Override
	public RestUserFeedBackListResponse queryRestUserFeedBackList(RestUserFeedBackListRequest condition) {
		
		RestUserFeedBackListResponse response = new RestUserFeedBackListResponse();
		
		int totalCount = feedBackService.countRestUserFeedBack(condition);
		
		List<RestFeedBackInfo> feedBackInfoList = feedBackService.queryRestUserFeedBackList(condition);
		
		response.setLimit(condition.getLimit());
		response.setOffset(condition.getOffset());
		response.setTotalCount(totalCount);
		response.setFeedBackInfoList(feedBackInfoList);
		
		return response;
	}


	@Override
	public int countRestFeedBack(RestUserFeedBackListRequest condition) {
		
		return feedBackService.countRestUserFeedBack(condition);
	}


	@Override
	public EnterpriseUser getByIdAndEnterprise(long id, long enterpriseId) {
		
		return feedBackService.getByIdAndEnterprise(id,enterpriseId);
	}

}
