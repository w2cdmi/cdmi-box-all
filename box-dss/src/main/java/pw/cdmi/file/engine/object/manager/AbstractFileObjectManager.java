/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;

/**
 * 
 * @author s90006125
 * 
 */
public abstract class AbstractFileObjectManager
{
    /**
     * 通过存储路径，获取存储上的文件对象
     * 
     * @param storagePath
     * @return
     * @throws FSException
     */
    protected FSObject getFSObject(String storagePath) throws FSException
    {
        FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(storagePath);
        
        FSObject fsObject = fileSystem.transToFSObject(storagePath);
        
        // 整体下载文件，支持缓存
        return fileSystem.getObject(fsObject);
    }
    
    /**
     * 通过存储路径，获取存储上的文件对象
     * 
     * @param storagePath
     * @return
     * @throws FSException
     */
    protected FSObject getFSObject(String storagePath, Long start, Long end) throws FSException
    {
        FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(storagePath);
        
        FSObject fsObject = fileSystem.transToFSObject(storagePath);
        
        return fileSystem.getObject(fsObject, start, end);
    }
}
