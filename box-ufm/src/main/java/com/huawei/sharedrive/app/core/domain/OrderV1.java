package com.huawei.sharedrive.app.core.domain;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author q90003805
 * 
 */
public class OrderV1 implements Serializable
{
    private static final long serialVersionUID = 1242542965429358826L;
    
    private boolean desc;
    
    private String field;
    
    public OrderV1()
    {
    }
    
    public OrderV1(String field, boolean desc)
    {
        setField(field);
        this.desc = desc;
    }
    
    /**
     * 文件列表，防止SQL盲注
     */
    public void generateFileField()
    {
        String filed = StringUtils.trimToEmpty(field).toLowerCase(Locale.US);
        if ("name".equals(filed))
        {
            field = "convert(name using gb2312) ";
        }
        else if ("size".equals(filed))
        {
            field = "size ";
        }
        else
        {
            field = "modifiedAt ";
        }
    }
    
    /**
     * 重组生成共享排序字段
     */
    public void generateShareField()
    {
        if (this.desc)
        {
            if ("name".equals(field))
            {
                field = "type desc, convert(name using gb2312) desc";
            }
            else if ("ownerName".equals(field))
            {
                field = "ownerName desc,type asc,convert(name using gb2312) asc";
            }
            else
            {
                field = "modifiedAt desc, type asc, convert(name using gb2312) asc";
                
            }
        }
        else
        {
            if ("name".equals(field))
            {
                field = "type asc,convert(name using gb2312) asc";
            }
            else if ("ownerName".equals(field))
            {
                field = "ownerName asc,type asc,convert(name using gb2312) asc";
            }
            else
            {
                field = "modifiedAt asc, type asc, convert(name using gb2312) asc";
            }
        }
    }
    
    public String getField()
    {
        return field;
    }
    
    public boolean isDesc()
    {
        return desc;
    }
    
    public void setDesc(boolean desc)
    {
        this.desc = desc;
    }
    
    public void setField(String field)
    {
        this.field = field;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (desc ? 1231 : 1237);
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof OrderV1)
        {
            OrderV1 other = (OrderV1) obj;
            if (desc != other.desc)
            {
                return false;
            }
            if (field == null)
            {
                if (other.field != null)
                {
                    return false;
                }
            }
            else if (!field.equals(other.field))
            {
                return false;
            }
            return true;
        }
        return false;
    }
    
}
