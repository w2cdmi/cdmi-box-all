package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;

public class LdapFilterField implements Serializable
{
    
    private static final long serialVersionUID = 3584591664924007880L;
    
    public static final String EQUALS = "=";
    
    public static final String NOT_EQUALS = "!=";
    
    public static final String GT = ">";
    
    public static final String LT = "<";
    
    public static final String GE = ">=";
    
    public static final String LE = "<=";
    
    public static final String START_WITH = "start with";
    
    public static final String END_WITH = "end with";
    
    public static final String CONTAINS = "contains";
    
    private String field;
    
    private String condition;
    
    private String value;
    
    public String getField()
    {
        return field;
    }
    
    public void setField(String field)
    {
        this.field = field;
    }
    
    public String getCondition()
    {
        return condition;
    }
    
    public void setCondition(String condition)
    {
        this.condition = condition;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    @Override
    public String toString()
    {
        return '\'' + field + '\'' + condition + '\'' + value + '\'';
    }
}
