/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.dao;

import java.util.Date;
import java.util.List;

import pw.cdmi.core.db.dao.BaseDAO;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;

/**
 * 
 * @author s90006125
 * 
 */
public interface MultipartFileObjectDao extends BaseDAO<MultipartFileObject>
{
    MultipartFileObject selectCommittingMultipartFileObject(Date timeout, int maxMergeTimes);
    
    List<MultipartFileObject> selectMultipartForUploadId(String fileObjectId, String uploadId);
    
    void deleteInvalidData(Date timeout, int maxMergeTimes, int maxClearTimes);
    
    /**
     * 更新最后修改时间
     * @param fileObject
     */
    void updateLastModifyTime(MultipartFileObject fileObject); 
    
    List<MultipartFileObject> selectWaitClearMultipartFileObject(Date reserveTime, int maxClearTimes, int limit);
}
