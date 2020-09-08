package com.huawei.sharedrive.uam.feedback.domain;


import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


public class QueryFeedBackListRequest implements Serializable
{


     /**
      *  <一句话描述>
      */
     private static final long serialVersionUID = 8764131302714033940L;

     /**
      * 提问开始时间
      */
     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private Date twBeginTime;

     /**
      * 提问结束时间
      */
     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private Date twEndTime;

     /**
      * 答复开始时间
      */
     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private Date dfBeginTime;

     /**
      * 答复结束时间
      */
     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private Date dfEndTime;

     /**
      * 问题标题
      */
     private String problemTitle;

     /**
      * 问题状态
      */
     private String problemStatus;

     /**
      * 企业虚拟用户ID
      */
     private String customerID;

     /**
      * 开始位置，从0开始
      */
     private int startPos;

     /**
      * 每页显示记录的条数
      */
     private int pageSize;

     
     
     public Date getTwBeginTime()
     {
          return twBeginTime;
     }

     
     
     public void setTwBeginTime(Date twBeginTime)
     {
          this.twBeginTime = twBeginTime;
     }

     
     
     public Date getTwEndTime()
     {
          return twEndTime;
     }

     
     
     public void setTwEndTime(Date twEndTime)
     {
          this.twEndTime = twEndTime;
     }

     
     
     public Date getDfBeginTime()
     {
          return dfBeginTime;
     }

     
     
     public void setDfBeginTime(Date dfBeginTime)
     {
          this.dfBeginTime = dfBeginTime;
     }

     
     
     public Date getDfEndTime()
     {
          return dfEndTime;
     }

     
     
     public void setDfEndTime(Date dfEndTime)
     {
          this.dfEndTime = dfEndTime;
     }

     
     
     public String getProblemTitle()
     {
          return problemTitle;
     }

     
     
     public void setProblemTitle(String problemTitle)
     {
          this.problemTitle = problemTitle;
     }

     
     
     public String getProblemStatus()
     {
          return problemStatus;
     }

     
     
     public void setProblemStatus(String problemStatus)
     {
          this.problemStatus = problemStatus;
     }

     
     
     public String getCustomerID()
     {
          return customerID;
     }

     
     
     public void setCustomerID(String customerID)
     {
          this.customerID = customerID;
     }

     
     
     public int getStartPos()
     {
          return startPos;
     }

     
     
     public void setStartPos(int startPos)
     {
          this.startPos = startPos;
     }

     
     
     public int getPageSize()
     {
          return pageSize;
     }

     
     
     public void setPageSize(int pageSize)
     {
          this.pageSize = pageSize;
     }
     
     
}
