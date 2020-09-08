/**
 * 
 */
package pw.cdmi.file.engine.mirro.service;

import java.util.List;

import pw.cdmi.file.engine.mirro.domain.CopyTaskPart;

/**
 * @author w00186884
 * 
 */
public interface CopyTaskPartService
{
    
    /**
     * 是否已经保存分片任务
     * 
     * @param taskId
     * @return
     */
    boolean hasCopyTaskPart(String taskId);
    
    /**
     * 批量插入任务
     * @param parts
     */
    void batchInsertOrReplace(List<CopyTaskPart> parts);

    /**
     * 根据任务Id加载所有分片任务
     * @param taskId
     * @return
     */
    List<CopyTaskPart> listCopyTaskPartByTaskId(String taskId);

    /**
     * 是否已经全部复制成功
     * @param taskId
     * @return
     */
    boolean allPartsSuccess(String taskId);

    /**
     * 批量更新子任务的状态
     * @param taskPart
     */
    void updateTaskPartsStatus(List<CopyTaskPart> taskPart);
    
}
