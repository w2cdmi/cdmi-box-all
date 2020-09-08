package pw.cdmi.box.uam.feedback.service;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.uam.feedback.dao.UserFeedBackDAO;
import pw.cdmi.box.uam.feedback.domain.QueryUserFeedBackCondition;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackCreateRequest;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackDetail;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackInfo;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackSubInfo;
import pw.cdmi.box.uam.feedback.domain.RestUserFeedBackListRequest;


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
	public void logicDeleteUserFeedBack(long problemID) {
   		 feedBackDAO.logicDeleteUserFeedBack(problemID);
	}


	@Override
	public void physicsDeleteUserFeedBack(Date date) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		rightNow.add(Calendar.MONTH, -3);
		List<RestFeedBackInfo> queryDeleteFeedBackSub = feedBackDAO.queryDeleteFeedBackSub(rightNow.getTime());
		for (RestFeedBackInfo restFeedBackInfo : queryDeleteFeedBackSub) {
			feedBackDAO.deleteUserFeedBackSub(restFeedBackInfo.getProblemID());
		}
		feedBackDAO.physicsDeleteUserFeedBack(rightNow.getTime());
		
	}

}
