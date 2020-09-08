/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.domain;

/**
 * 
 * @author s90006125
 * 
 */
public enum PreviewFileType
{
    SWF("swf"), PDF("pdf");
    
    private String type;
    
    private PreviewFileType(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return type;
    }
    
    public static PreviewFileType find(String type)
    {
        for (PreviewFileType t : PreviewFileType.values())
        {
            if (t.getType().equalsIgnoreCase(type))
            {
                return t;
            }
        }
        
        return null;
    }
}
