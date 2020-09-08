/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.dao;

import java.util.List;
import java.util.Map;

import pw.cdmi.core.db.dao.BaseDAO;
import pw.cdmi.file.engine.object.domain.FileObjectDeleteTask;

/**
 * 
 * @author s90006125
 *
 */
public interface FileObjectDeleteTaskDao extends BaseDAO<FileObjectDeleteTask>
{
    /**
     * 恢复删除失败的对象
     * @param parameter
     */
    void resumeFailedFileObjectDeleteTask(Map<String, Object> parameter);
    
    /**
     * 列举待删除的对象数据
     * @param parameter
     * @return
     */
    List<FileObjectDeleteTask> listFileObjectDeleteTask(Map<String, Object> parameter);
    
    /**
     * 标记删除任务
     * @param fileObjectDeleteTask
     * @return
     */
    int markFileObjectDeleteTask(FileObjectDeleteTask fileObjectDeleteTask);
}
