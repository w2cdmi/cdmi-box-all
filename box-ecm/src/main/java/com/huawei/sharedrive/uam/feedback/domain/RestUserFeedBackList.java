package com.huawei.sharedrive.uam.feedback.domain;

import java.util.List;



public class RestUserFeedBackList
{
     List<RestFeedBackInfo> feedBackList;
     
     private long offset;
     
     private long totalCount;
     
     private int limit;

     public List <RestFeedBackInfo> getFeedBackList()
     {
          return feedBackList;
     }

     
     
     public void setFeedBackList(List <RestFeedBackInfo> feedBackList)
     {
          this.feedBackList = feedBackList;
     }

     
     
     public long getOffset()
     {
          return offset;
     }

     
     
     public void setOffset(long offset)
     {
          this.offset = offset;
     }

     
     
     public long getTotalCount()
     {
          return totalCount;
     }

     
     
     public void setTotalCount(long totalCount)
     {
          this.totalCount = totalCount;
     }

     
     
     public int getLimit()
     {
          return limit;
     }

     
     
     public void setLimit(int limit)
     {
          this.limit = limit;
     }
     
     
}
