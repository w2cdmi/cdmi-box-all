package com.huawei.sharedrive.uam.feedback.manager;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.feedback.domain.QueryUserFeedBackCondition;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackCreateRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackDetail;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackSubInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListResponse;

import pw.cdmi.box.domain.Page;



public interface UserFeedBackManager
{

     List <RestFeedBackInfo> queryUserFeedBackList(QueryUserFeedBackCondition condition);

     int countUserFeedBack(QueryUserFeedBackCondition condition);

     void logicDeleteUserFeedBack(long problemID);
     
     void physcisDeleteUserFeedBack(long problemID);

     void addUserFeedBack(RestFeedBackCreateRequest restFeedBackCreateRequest);

     void updateUserFeedBack(RestFeedBackInfo restFeedBackInfo);

     Page <RestFeedBackInfo> queryUserFeedBackByPage(QueryUserFeedBackCondition condition);

     RestFeedBackInfo getFeedBackByID(long problemID);

     void deleteUserFeedBackSub(long problemID);

     List <RestFeedBackSubInfo> getFeedBackSubList(long problemID);

     void updateTeedBackTime(RestFeedBackDetail feedBackDetail);

     void addNewFeedBackSub(RestFeedBackSubInfo restFeedBackInfo);

     RestUserFeedBackListResponse queryRestUserFeedBackList(RestUserFeedBackListRequest condition);

	int countRestFeedBack(RestUserFeedBackListRequest condition);

	EnterpriseUser getByIdAndEnterprise(long id, long enterpriseId);

}
