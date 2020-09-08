package com.huawei.sharedrive.app.files.dao;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;
import com.huawei.sharedrive.app.files.domain.ObjectSecretLevel;

public interface ObjectSecretLevelDAO
{
    /**
     *创建指纹索引密级对象
     * 
     * @param objFpIndex
     */
	void create(ObjectSecretLevel sbjectSecretLevel);
       
    /**
     * 根据sha1获取指纹索引密级设置
     * 
     * @param sha1
     * @return
     */
	ObjectSecretLevel getByAccountId(String sha1, int regionId, long accountId);


	void createOrUpdate(String sha1, Long accountId, int regionId, byte secretLevel);

	
}
