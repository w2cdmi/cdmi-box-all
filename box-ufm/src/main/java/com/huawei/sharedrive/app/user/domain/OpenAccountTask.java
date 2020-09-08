/**
 * 
 */
package com.huawei.sharedrive.app.user.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author s00108907
 * 
 */
public class OpenAccountTask implements Serializable
{
    private static final long serialVersionUID = 3872725093371831142L;
    
    private Date beginDate;
    
    private Date endDate;
    
    private int fail;
    
    private String searchBase;
    
    private String searchFiler;
    
    private int success;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getBeginDate()
    {
        if (beginDate == null)
        {
            return null;
        }
        return (Date) beginDate.clone();
    }
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getEndDate()
    {
        if (endDate == null)
        {
            return null;
        }
        return (Date) endDate.clone();
    }
    
    public int getFail()
    {
        return fail;
    }
    
    public String getSearchBase()
    {
        return searchBase;
    }
    
    public String getSearchFiler()
    {
        return searchFiler;
    }
    
    public int getSuccess()
    {
        return success;
    }
    
    public void setBeginDate(Date beginDate)
    {
        if (beginDate == null)
        {
            this.beginDate = null;
        }
        else
        {
            this.beginDate = (Date) beginDate.clone();
        }
    }
    
    public void setEndDate(Date endDate)
    {
        if (endDate == null)
        {
            this.endDate = null;
        }
        else
        {
            this.endDate = (Date) endDate.clone();
        }
    }
    
    public void setFail(int fail)
    {
        this.fail = fail;
    }
    
    public void setSearchBase(String searchBase)
    {
        this.searchBase = searchBase;
    }
    
    public void setSearchFiler(String searchFiler)
    {
        this.searchFiler = searchFiler;
    }
    
    public void setSuccess(int success)
    {
        this.success = success;
    }
}
