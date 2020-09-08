/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.core.config;

import pw.cdmi.file.engine.core.common.AutoToStringEntity;
import pw.cdmi.file.engine.core.ibatis.Namingspace;

/**
 * 常量、配置对象
 * 
 * @author s90006125
 * 
 */
@Namingspace("systemConfig")
public class SystemConfig extends AutoToStringEntity
{
    private static final long serialVersionUID = -14907706033565017L;
    
    /** 是否可修改 */
    private boolean changeAble;
    
    private String description;
    
    private String key;
    
    /** 是否可展示 */
    private boolean showAble;
    
    private String value;
    
    public SystemConfig()
    {
        
    }
    
    public SystemConfig(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
    
    public SystemConfig(String key, String value, boolean showAble, boolean changeAble)
    {
        this(key, value);
        this.showAble = showAble;
        this.changeAble = changeAble;
    }
    
    public SystemConfig(String key, String value, String description)
    {
        this(key, value);
        this.description = description;
    }
    
    public SystemConfig(String key, String value, String description, boolean showAble, boolean changeAble)
    {
        this(key, value, showAble, changeAble);
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public boolean isChangeAble()
    {
        return changeAble;
    }
    
    public boolean isShowAble()
    {
        return showAble;
    }
    
    public void setChangeAble(boolean changeAble)
    {
        this.changeAble = changeAble;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setShowAble(boolean showAble)
    {
        this.showAble = showAble;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
}
