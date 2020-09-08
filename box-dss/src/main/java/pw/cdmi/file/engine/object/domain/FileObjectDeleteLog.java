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
 * 对象彻底删除日志，删除成功后，会移动到fileobject_delete_log_0表，作为备份日志保存下来
 * @author s90006125
 *
 */
@Namingspace("fileObjectDeleteLog")
public class FileObjectDeleteLog extends FileObjectDeleteTask
{
    private static final long serialVersionUID = 1781921552147815842L;
    
    public FileObjectDeleteLog()
    {
    }
    
    public FileObjectDeleteLog(FileObjectDeleteTask fileObjectDeleteTask)
    {
        super(fileObjectDeleteTask);
        this.setStatus(fileObjectDeleteTask.getStatus());
        this.setDeleteAt(fileObjectDeleteTask.getDeleteAt());
        this.setDeleteTimes(fileObjectDeleteTask.getDeleteTimes());
        this.setModified(fileObjectDeleteTask.getModified());
    }
}
