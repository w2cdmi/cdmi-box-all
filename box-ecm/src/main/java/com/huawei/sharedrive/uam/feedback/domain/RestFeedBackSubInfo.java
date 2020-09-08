package com.huawei.sharedrive.uam.feedback.domain;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


public class RestFeedBackSubInfo
{

     protected long problemID;

     protected String userID;

     protected String userName;

     protected String description;

     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     protected Date updateTime;

     protected String isAnswer;

	public long getProblemID() {
		return problemID;
	}


	public void setProblemID(long problemID) {
		this.problemID = problemID;
	}


	public String getUserID()
     {
          return userID;
     }


     public void setUserID(String userID)
     {
          this.userID = userID;
     }


     public String getUserName()
     {
          return userName;
     }


     public void setUserName(String userName)
     {
          this.userName = userName;
     }


     public String getDescription()
     {
          return description;
     }


     public void setDescription(String description)
     {
          this.description = description;
     }


     public Date getUpdateTime()
     {
          return updateTime;
     }


     public void setUpdateTime(Date updateTime)
     {
          this.updateTime = updateTime;
     }


     public String getIsAnswer()
     {
          return isAnswer;
     }


     public void setIsAnswer(String isAnswer)
     {
          this.isAnswer = isAnswer;
     }

}
