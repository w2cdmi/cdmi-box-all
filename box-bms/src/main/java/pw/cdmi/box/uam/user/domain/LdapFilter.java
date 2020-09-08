package pw.cdmi.box.uam.user.domain;

import java.io.Serializable;

public class LdapFilter implements Serializable
{
    
    private static final long serialVersionUID = 891907474471093776L;
    
    public static final String EQUALS = "=";
    
    public static final String NOT_EQUALS = "!=";
    
    public static final String GT = ">";
    
    public static final String LT = "<";
    
    public static final String GE = ">=";
    
    public static final String LE = "<=";
    
    public static final String START_WITH = "start with";
    
    public static final String END_WITH = "end with";
    
    public static final String CONTAINS = "contains";
    
    private long id;
    
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
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
}
