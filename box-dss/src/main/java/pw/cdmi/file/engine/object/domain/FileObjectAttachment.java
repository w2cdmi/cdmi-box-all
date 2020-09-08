/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.domain;

import pw.cdmi.file.engine.core.ibatis.Namingspace;

/**
 * 文件对象的附件，例如预览文件、缩略图等
 * 
 * @author s90006125
 * 
 */
@Namingspace("fileObjectAttachment")
public class FileObjectAttachment extends FileObject
{
    private static final long serialVersionUID = -5951803039145952885L;
    
    private String attachment;
    
    public FileObjectAttachment()
    {
        this.setStatus(FileObjectStatus.COMPLETED);
    }
    
    public FileObjectAttachment(String objectID)
    {
        super(objectID);
        this.setStatus(FileObjectStatus.COMPLETED);
    }
    
    public FileObjectAttachment(String objectID, String attachment)
    {
        super(objectID);
        this.attachment = attachment;
        this.setStatus(FileObjectStatus.COMPLETED);
    }
    
    public String getAttachment()
    {
        return attachment;
    }
    
    public void setAttachment(String attachment)
    {
        this.attachment = attachment;
    }
}
