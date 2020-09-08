package com.huawei.sharedrive.uam.feedback.domain;


import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;




/**
 * 
 * 查看用户反馈查询条件
 * 
 * Project Name:cloudapp_cmb_v1
 * 
 * File Name:QueryUserFeedBackCondition.java
 * 
 * 
 *         修改时间：2016年8月11日 下午2:26:43
 */
public class RestUserFeedBackListRequest  implements Serializable
{

	private long enterpriseId;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1988726611997350688L;

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
      * 周期
      * ""全部
      * dayRound日
      * weekRound周
      * monthRound月
      */
     private String cycle;

     
     
     private static final int DEFAULT_LIMIT = 100;
     
     private static final long DEFAULT_OFFSET = 0L;
     
     private static final int MAX_LIMIT = 1000;
     
     private Integer limit;
     
     private Long offset;
     
     public RestUserFeedBackListRequest()
     {
         limit = DEFAULT_LIMIT;
         offset = DEFAULT_OFFSET;
     }
     
     public RestUserFeedBackListRequest(Integer limit, Long offset)
     {
         this.limit = limit != null ? limit : DEFAULT_LIMIT;
         this.offset = offset != null ? offset : DEFAULT_OFFSET;
     }
     
     public void checkParameter() throws InvalidParameterException
     {
         if (limit != null && (limit < 1 || limit > MAX_LIMIT))
         {
             throw new InvalidParameterException();
         }
         if (offset != null && offset < 0)
         {
             throw new InvalidParameterException();
         }

     }
     

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

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
     
     
     
}
