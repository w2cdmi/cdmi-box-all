/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.service;

import java.util.List;

import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskPart;

public interface RealTimeCopyTaskPartService
{
    /**
     * 更新分片任务状态
     * @param taskPart
     */
    void updateTaskPartsStatus(List<RealTimeCopyTaskPart> taskPart);
    /**
     * 取出所有分片
     * @param taskId
     */
    List<RealTimeCopyTaskPart> listRealTimeCopyTaskPartByTaskId(String taskId);
    /**
     * 插入新任务分片
     * @param taskpart
     */
    void batchInsertOrReplace(List<RealTimeCopyTaskPart> parts);
    /**
     * 任务分片是否存在
     * @param taskId
     */
    boolean hasRealTimeCopyTaskPart(String taskId);
    /**
     * 任务分片是否全部成功
     * @param taskId
     */
    boolean allPartsSuccess(String taskId);
    /**
     * 删除分片任务
     * @param 
     */
    void deleteUnExistTaskPart();   
}
