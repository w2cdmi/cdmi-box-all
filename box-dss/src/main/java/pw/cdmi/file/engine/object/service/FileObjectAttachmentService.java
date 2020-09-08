/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service;

import java.util.List;

import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectAttachment;

/**
 * 
 * @author s90006125
 * 
 */
public interface FileObjectAttachmentService
{
    /**
     * 保存附件文件对象
     * 
     * @param attachment
     * @return
     */
    void saveFileObjectAttachment(FileObjectAttachment attachment);
    
    /**
     * 获取指定的附件文件
     * 
     * @param attachment
     * @return
     */
    FileObjectAttachment getFileObjectAttachment(FileObjectAttachment attachment);
    
    /**
     * 获取指定文件的所有附件
     * 
     * @param attachment
     * @return
     */
    List<FileObjectAttachment> getFileObjectAttachments(FileObject fileObject);
    
    /**
     * 删除指定对象的附加对象
     * 
     * @param fileObject
     */
    void deleteFileObjectAttachment(FileObject fileObject);
}
