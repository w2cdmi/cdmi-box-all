package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.files.domain.ObjectSecretLevel;

public interface ObjectSecretLevelService {

	/**
     *创建指纹索引对象
     * 
     * @param objFpIndex
     */
	void create(ObjectSecretLevel objectSecretLevel);
       
    /**
     * 根据sha1获取指纹索引密级设置
     * 
     * @param sha1
     * @return
     */
	ObjectSecretLevel getByAccountId(String sha1, int regionId, long accountId);


	void updateSecretLevel(String sha1, Long accountId, int regionId, byte secretLevel);
}
