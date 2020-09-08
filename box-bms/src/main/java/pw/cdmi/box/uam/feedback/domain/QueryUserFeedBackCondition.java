package pw.cdmi.box.uam.feedback.domain;


import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import pw.cdmi.box.uam.feedback.util.PageRequest;


/**
 * 
 * 查看用户反馈查询条件
 * 
 * Project Name:cloudapp_cmb_v1
 * 
 * File Name:QueryUserFeedBackCondition.java
 * 
 * @author onebox
 * 
 *         修改时间：2016年8月11日 下午2:26:43
 */
public class QueryUserFeedBackCondition
{



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
      * 企业虚拟用户名
      */
     private String customerName;

     /**
      * 分页
      */
     private PageRequest pageRequest;
   

     public Date getTwBeginTime()
     {
          if (this.twBeginTime != null)
          {
               return new Date(this.twBeginTime.getTime());
          }
          return null;
     }


     public void setTwBeginTime(Date twBeginTime)
     {
          if (twBeginTime != null)
          {
               this.twBeginTime = new Date(twBeginTime.getTime());
          }
          else
          {
               this.twBeginTime = null;
          }

     }


     public Date getTwEndTime()
     {
          if (this.twEndTime != null)
          {
               return new Date(this.twEndTime.getTime());
          }
          return null;
     }


     public void setTwEndTime(Date twEndTime)
     {
          if (twEndTime != null)
          {
               this.twEndTime = new Date(twEndTime.getTime());
          }
          else
          {
               this.twEndTime = null;
          }
     }


     public Date getDfBeginTime()
     {
          if (this.dfBeginTime != null)
          {
               return new Date(this.dfBeginTime.getTime());
          }
          return null;
     }


     public void setDfBeginTime(Date dfBeginTime)
     {
          if (dfBeginTime != null)
          {
               this.dfBeginTime = new Date(dfBeginTime.getTime());
          }
          else
          {
               this.dfBeginTime = null;
          }
     }


     public Date getDfEndTime()
     {
          if (this.dfEndTime != null)
          {
               return new Date(this.dfEndTime.getTime());
          }
          return null;
     }


     public void setDfEndTime(Date dfEndTime)
     {
          if (dfEndTime != null)
          {
               this.dfEndTime = new Date(dfEndTime.getTime());
          }
          else
          {
               this.dfEndTime = null;
          }
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


     public String getCustomerName()
     {
          return customerName;
     }


     public void setCustomerName(String customerName)
     {
          this.customerName = customerName;
     }


     public PageRequest getPageRequest()
     {
          return pageRequest;
     }


     public void setPageRequest(PageRequest pageRequest)
     {
          this.pageRequest = pageRequest;
     }

}
