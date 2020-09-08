package com.huawei.sharedrive.uam.enterpriseuser.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.enterpriseuser.dao.IMigrationRecordDao;
import com.huawei.sharedrive.uam.enterpriseuser.domain.MigrationRecord;

/**
 * 数据迁移记录接口实现
 * @author 77235
 *
 */
@Component("migrationRecordDao")
@SuppressWarnings("deprecation")
public class MigrationRecordDaoImpl implements IMigrationRecordDao {
    
    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;
    
    @Override
    public void save(MigrationRecord migrationRecord) {
        migrationRecord.setTableSuffix(getTableSuffix(migrationRecord.getEnterpriseId()));
        
        sqlMapClientTemplate.insert("MigrationRecord.insert", migrationRecord);
    }
    
	@SuppressWarnings("unchecked")
	public List<MigrationRecord> findBeCleanedRecords(int tableIndex) {
		Map<String, Object> paramObj = new HashMap<String, Object>();
		paramObj.put("tableSuffix", tableIndex);

		List<MigrationRecord> retObj = sqlMapClientTemplate.queryForList("MigrationRecord.findBeCleandRecords",
				paramObj);
		
		return retObj;
	}
	

	@Override
	public boolean isMigratedForSpecialDepartureUser(long enterpriseId, long departureUserId) {
		boolean isMigrated = true;
		Map<String, Object> paramObj = new HashMap<String, Object>();
		paramObj.put("tableSuffix", getTableSuffix(enterpriseId));
		paramObj.put("departureUserId", departureUserId);

		Object retObj = sqlMapClientTemplate.queryForObject("MigrationRecord.isMigratedForSpecialDepartureUser",
				paramObj);
		if (retObj == null || Integer.valueOf(retObj.toString()) <= 0){
			isMigrated = false;
		}
		
		return isMigrated;
	}
    
	@Override
    public long getMaxRecordId(){
        long maxUserId = 1L;
        long selMaxUserId;
        Object maxUserIdObject;
        
        for (int i = 0; i < TABLE_COUNT; i++) {
            maxUserIdObject = sqlMapClientTemplate.queryForObject("MigrationRecord.getMaxRecordId",  i + "");
            
            selMaxUserId = maxUserIdObject == null ? 0L : (long) maxUserIdObject;
            if (maxUserId < selMaxUserId) {
                maxUserId = selMaxUserId;
            }
        }
        
        return maxUserId;
    }
	
    public static String getTableSuffix(long enterpriseId) {
        int table = (int) (HashTool.apply(String.valueOf(enterpriseId)) % TABLE_COUNT);
        
        return table + "";
    }

	@Override
	public void updateRecordStatus(int tableIndex, long recordId, int status) {
		try {
			Map<String, Object> paramObj = new HashMap<String, Object>();
			paramObj.put("tableSuffix", tableIndex);
			paramObj.put("status", status);
			paramObj.put("id", recordId);
			paramObj.put("clearDate", new Date());
			
			sqlMapClientTemplate.update("MigrationRecord.updateStatus", paramObj);
		} catch (DataAccessException e) {
			// 记录日志
		}
	}
}
