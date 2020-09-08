package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterprise.dao.EnterpriseAccountDao;
import com.huawei.sharedrive.uam.enterpriseuser.dao.EnterpriseUserDao;
import com.huawei.sharedrive.uam.enterpriseuser.dao.IMigrationRecordDao;
import com.huawei.sharedrive.uam.enterpriseuser.domain.MigrationRecord;
import com.huawei.sharedrive.uam.enterpriseuser.dto.DataMigrationRequestDto;
import com.huawei.sharedrive.uam.enterpriseuser.service.IMigrationRecordService;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;

@Service("migrationRecordService")
public class MigrationRecordServiceImpl implements IMigrationRecordService {
    
    @Autowired
    private IMigrationRecordDao migrationRecordDao;
    
    @Autowired
    private EnterpriseAccountDao enterpriseAccountDao;
    
    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    
    @Resource
    private RestClient ufmClientService;
    
    @Override
    public void save(MigrationRecord migrationRecord) {
        migrationRecordDao.save(migrationRecord);
    }

	@Override
	public void cleanExpiredMigrationRecord(Date optTime, int submeterIndex) {
		List<MigrationRecord> migtationRecords = migrationRecordDao.findBeCleanedRecords(submeterIndex);
		if (null != migtationRecords) {
			EnterpriseAccount enterpriseAccount = null;
			
			for (MigrationRecord mr : migtationRecords){
				try {
					
					switch (mr.getMigrationType()) {
					case DataMigrationRequestDto.CONST_MIGRATION_TO_OPEN_ACCOUNT_USER:
						// 1、清理移交数据记录
						UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
						enterpriseAccount = enterpriseAccountDao.getByEnterpriseApp(mr.getEnterpriseId(), mr.getAppId()); // 企业账户信息
						boolean result = userHttpClient.cleanDepartureUserInfo(mr, enterpriseAccount);
						
						// 2、更新状态
						int recordResult = MigrationRecord.CONST_MIGRATION_TYPE_CLEAN_FAIL;
						if (result){
							recordResult = MigrationRecord.CONST_MIGRATION_TYPE_CLEAN_SUCCESS;
						}
						
						migrationRecordDao.updateRecordStatus(submeterIndex, mr.getId(), recordResult);
						break;
						
					case DataMigrationRequestDto.CONST_MIGRATION_TO_UNOPEN_ACCOUNT_USER:
						enterpriseUserDao.deleteById(mr.getEnterpriseId(), mr.getId());
						migrationRecordDao.updateRecordStatus(submeterIndex, mr.getId(), MigrationRecord.CONST_MIGRATION_TYPE_CLEAN_SUCCESS);
						break;
						
					default:
						break;
					}
					
				} catch (Exception e) {
					// 记录日志
				}
			}
		}
	}

	@Override
	public boolean isMigratedForUser(long enterpriseId, long userId) {
		
		return migrationRecordDao.isMigratedForSpecialDepartureUser(enterpriseId, userId);
	}
}
