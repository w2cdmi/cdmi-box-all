/*  
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service;

import pw.cdmi.file.engine.object.domain.FileObject;

/**
 * 
 * @author s90006125
 * 
 */
public interface FileObjectService
{
    /**
     * 保存文件对象
     * 
     * @param fileObject
     */
    void saveFileObject(FileObject fileObject);
    
    /**
     * 根据ObjectID，获取FileObject对象
     * 
     * @param objectID
     * @return
     */
    FileObject getFileObject(String objectID);
    
    /**
     * 判断该对象是否已经存在
     * 
     * @param fileObject
     * @return
     */
    boolean isExist(String objectID);
    
    /**
     * 更新文件对象信息
     * 
     * @param fileObject
     */
    void updateFileObject(FileObject fileObject);
    
    /**
     * 删除文件对象
     * 
     * @param objectID
     * @return
     */
    boolean deleteFileObject(FileObject fileObject);
}
