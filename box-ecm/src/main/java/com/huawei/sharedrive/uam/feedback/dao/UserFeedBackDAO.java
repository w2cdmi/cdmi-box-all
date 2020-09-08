package com.huawei.sharedrive.uam.feedback.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.feedback.domain.QueryUserFeedBackCondition;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackCreateRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackDetail;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackSubInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListRequest;



public interface UserFeedBackDAO
{

     List<RestFeedBackInfo> queryUserFeedBackList(QueryUserFeedBackCondition condition);

     int countUserFeedBack(QueryUserFeedBackCondition condition);

     void logicDeleteUserFeedBack(long problemID);
     
     void physicsDeleteUserFeedBack(long problemID);

     void addUserFeedBack(RestFeedBackCreateRequest restFeedBackCreateRequest);

     void updateUserFeedBack(RestFeedBackInfo restFeedBackInfo);

     RestFeedBackInfo getFeedBackByID(long problemID);

     void deleteUserFeedBackSub(long problemID);

     List <RestFeedBackSubInfo> getFeedBackSubList(long problemID);

     void updateTeedBackTime(RestFeedBackDetail feedBackDetail);

     void addNewFeedBackSub(RestFeedBackSubInfo restFeedBackInfo);

	List<RestFeedBackInfo> queryRestUserFeedBackList(RestUserFeedBackListRequest condition);

	int countRestUserFeedBack(RestUserFeedBackListRequest condition);

	EnterpriseUser countRestUserFeedBack(long id, long enterpriseId);

}
