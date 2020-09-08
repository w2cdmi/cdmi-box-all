/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.dao;

import java.util.List;

import pw.cdmi.core.db.dao.BaseDAO;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectAttachment;

/**
 * 
 * @author s90006125
 * 
 */
public interface FileObjectAttachmentDao extends BaseDAO<FileObjectAttachment>
{
    void deleteByFileObject(FileObject fileObject);
    
    List<FileObjectAttachment> queryAll(FileObject fileObject);
}
