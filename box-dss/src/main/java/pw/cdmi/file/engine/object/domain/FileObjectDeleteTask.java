/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.domain;

import java.util.Date;

import pw.cdmi.file.engine.core.ibatis.Namingspace;

/**
 * 对象彻底删除任务，dss收到删除命令后，会将数据移动到fileobject_delete_task_0表，等待被删除
 * @author s90006125
 *
 */
@Namingspace("fileObjectDeleteTask")
public class FileObjectDeleteTask extends FileObject
{
    private static final long serialVersionUID = 1781921552147815842L;

    /**
     * 开始删除的时间（接收到删除命令的时间）
     */
    private Date deleteAt;
    
    /**
     * 删除次数
     */
    private int deleteTimes;
    
    /**
     * 最后更新时间，即为上一次删除的时间
     */
    private Date modified;

    public FileObjectDeleteTask()
    {
    }
    
    public FileObjectDeleteTask(FileObject fileObject)
    {
        this.setObjectID(fileObject.getObjectID());
        this.setStoragePath(fileObject.getStoragePath());
        this.setSha1(fileObject.getSha1());
        this.setObjectLength(fileObject.getObjectLength());
    }
    
    public Date getDeleteAt()
    {
        if(null == deleteAt)
        {
            return null;
        }
        
        return (Date) deleteAt.clone();
    }

    public void setDeleteAt(Date deleteAt)
    {
        if(null == deleteAt)
        {
            this.deleteAt = null;
            return;
        }
        this.deleteAt = (Date) deleteAt.clone();
    }

    public int getDeleteTimes()
    {
        return deleteTimes;
    }

    public void setDeleteTimes(int deleteTimes)
    {
        this.deleteTimes = deleteTimes;
    }

    public Date getModified()
    {
        if(null == modified)
        {
            return null;
        }
        return (Date) modified.clone();
    }

    public void setModified(Date modified)
    {
        if(null == modified)
        {
            this.modified = null;
            return;
        }
        
        this.modified = (Date) modified.clone();
    }
    
    @Override
    public String logFormat()
    {
        StringBuilder sb = new StringBuilder(FileObject.class.getCanonicalName()).append(START)
            .append("objectID=")
            .append(this.getObjectID())
            .append(SPLIT)
            .append(this.getStoragePath())
            .append(SPLIT)
            .append("objectLength=")
            .append(this.getObjectLength())
            .append(SPLIT)
            .append("sha1=")
            .append(this.getSha1())
            .append(SPLIT)
            .append("deleteAt=")
            .append(this.getDeleteAt())
            .append(SPLIT)
            .append("deleteTimes=")
            .append(this.getDeleteTimes())
            .append(END);
        return sb.toString();
    }
}
