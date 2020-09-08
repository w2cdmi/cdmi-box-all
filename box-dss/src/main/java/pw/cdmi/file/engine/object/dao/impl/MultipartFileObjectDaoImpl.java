/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.object.dao.MultipartFileObjectDao;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;

/**
 * 
 * @author s90006125
 * 
 */
@SuppressWarnings({"unused", "deprecation"})
@Repository("multipartFileObjectDao")
public class MultipartFileObjectDaoImpl extends IbatisSupportDAO<MultipartFileObject> implements
    MultipartFileObjectDao
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartFileObjectDaoImpl.class);
    
    @Override
    protected void doInsert(MultipartFileObject obj)
    {
        sqlMapClientTemplate.insert(warpSqlstatement(MultipartFileObject.class, SQL_INSERT), obj);
    }
    
    @Override
    protected void doDelete(MultipartFileObject obj)
    {
        sqlMapClientTemplate.delete(warpSqlstatement(MultipartFileObject.class, SQL_DELETE), obj);
    }
    
    @Override
    protected MultipartFileObject doSelect(MultipartFileObject obj)
    {
        return (MultipartFileObject) sqlMapClientTemplate.queryForObject(warpSqlstatement(MultipartFileObject.class, SQL_SELECT), obj);
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public List<MultipartFileObject> selectMultipartForUploadId(String fileObjectId, String uploadId){
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("objectID", fileObjectId);
        parameters.put("uploadID", uploadId);
    	return sqlMapClientTemplate.queryForList(warpSqlstatement(MultipartFileObject.class, "selectMultipartForUploadId"), parameters);
    }
    
    @Override
    protected int doUpdate(MultipartFileObject obj)
    {
        return sqlMapClientTemplate.update(warpSqlstatement(MultipartFileObject.class, SQL_UPDATE), obj);
    }
    
    @Override
    public MultipartFileObject selectCommittingMultipartFileObject(Date timeout, int maxMergeTimes)
    {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("timeout", timeout);
        parameters.put("maxMergeTimes", maxMergeTimes);
        return (MultipartFileObject)sqlMapClientTemplate.queryForObject(warpSqlstatement(MultipartFileObject.class, "selectCommittingMultipartFileObject"), parameters);
    }

    @SuppressWarnings({"unchecked", "boxing"})
    @Override
    public List<MultipartFileObject> selectWaitClearMultipartFileObject(Date reserveTime, int maxClearTimes, int limit)
    {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("reserveTime", reserveTime);
        parameters.put("maxClearTimes", maxClearTimes);
        parameters.put("limit", limit);
        return sqlMapClientTemplate.queryForList(warpSqlstatement(MultipartFileObject.class, "selectWaitClearMultipartFileObject"), parameters);
    }
    
    @Override
    public void deleteInvalidData(Date timeout, int maxMergeTimes, int maxClearTimes)
    {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("timeout", timeout);
        parameters.put("maxMergeTimes", maxMergeTimes);
        parameters.put("maxClearTimes", maxClearTimes);
        sqlMapClientTemplate.delete(warpSqlstatement(MultipartFileObject.class, "deleteInvalidData"), parameters);
    }

    @Override
    public void updateLastModifyTime(MultipartFileObject obj)
    {
        sqlMapClientTemplate.update(warpSqlstatement(MultipartFileObject.class, "updateLastModifyTime"), obj);
    }
}
