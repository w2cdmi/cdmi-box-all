/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import pw.cdmi.file.engine.object.domain.FileObject;

/**
 * 
 * @author s90006125
 *
 */
public class DownFileObjectContent
{
    private FileObject fileObject;
    private long contentLength;
    
    public DownFileObjectContent(FileObject fileObject, long contentLength)
    {
        this.fileObject = fileObject;
        this.contentLength = contentLength;
    }

    public FileObject getFileObject()
    {
        return fileObject;
    }

    public void setFileObject(FileObject fileObject)
    {
        this.fileObject = fileObject;
    }

    public long getContentLength()
    {
        return contentLength;
    }

    public void setContentLength(long contentLength)
    {
        this.contentLength = contentLength;
    }
}
