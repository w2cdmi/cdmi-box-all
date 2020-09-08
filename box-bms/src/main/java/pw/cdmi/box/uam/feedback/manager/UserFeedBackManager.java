package pw.cdmi.box.uam.feedback.manager;

import java.util.List;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.uam.feedback.domain.QueryUserFeedBackCondition;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackCreateRequest;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackDetail;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackInfo;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackSubInfo;
import pw.cdmi.box.uam.feedback.domain.RestUserFeedBackListRequest;
import pw.cdmi.box.uam.feedback.domain.RestUserFeedBackListResponse;



public interface UserFeedBackManager
{

     List <RestFeedBackInfo> queryUserFeedBackList(QueryUserFeedBackCondition condition);

     int countUserFeedBack(QueryUserFeedBackCondition condition);

     void logicDeleteUserFeedBack(long problemID);
     
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

//	EnterpriseUser getByIdAndEnterprise(long id, long enterpriseId);

}
