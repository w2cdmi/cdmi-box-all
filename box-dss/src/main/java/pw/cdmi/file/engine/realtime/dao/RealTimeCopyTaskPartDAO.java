/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.dao;


import java.util.List;

import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskPart;

public interface RealTimeCopyTaskPartDAO
{
    void updateTaskPartsStatus(RealTimeCopyTaskPart paramList);

    int countRealTimeCopyTaskPart(String taskId);

    void batchReplace(List<RealTimeCopyTaskPart> part);

    List<RealTimeCopyTaskPart> listRealTimeCopyTaskPartByTaskId(String taskId);

    int countUnSuccessTaskParts(String taskId);
    
    void deleteByTaskId(String taskId);

    void deleteUnExistTaskPart();
}
