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
public class ThumbnailFileObject extends FileObjectAttachment
{
    private static final long serialVersionUID = -8063162785917235384L;
    
    // 被压缩的尺寸
    private int resetHeight;
    
    private int resetWidth;
    
    // 原始尺寸
    private int originalHeight;
    
    private int originalWidth;
    
    private static final String SUFFIX_THUMBNAIL = "thumbnail_";
    
    public ThumbnailFileObject(String objectID, int resetHeight, int resetWidth)
    {
        super(objectID);
        this.resetHeight = resetHeight;
        this.resetWidth = resetWidth;
        this.setAttachment(SUFFIX_THUMBNAIL + this.getResetWidth() + '_' + this.getResetHeight());
    }
    
    public int getResetHeight()
    {
        return resetHeight;
    }
    
    public void setResetHeight(int resetHeight)
    {
        this.resetHeight = resetHeight;
    }
    
    public int getResetWidth()
    {
        return resetWidth;
    }
    
    public void setResetWidth(int resetWidth)
    {
        this.resetWidth = resetWidth;
    }
    
    public int getOriginalHeight()
    {
        return originalHeight;
    }
    
    public void setOriginalHeight(int originalHeight)
    {
        this.originalHeight = originalHeight;
    }
    
    public int getOriginalWidth()
    {
        return originalWidth;
    }
    
    public void setOriginalWidth(int originalWidth)
    {
        this.originalWidth = originalWidth;
    }
}
