package com.huawei.sharedrive.uam.plugin.Netease.service;

import java.util.Map;

import com.huawei.sharedrive.uam.plugin.Netease.domain.IMAccount;

public interface IMAccountService {

   void	createIMAccount(Map<String, Object> prameter);

   IMAccount get(long accountId, Long cloudUserId);
   
   IMAccount getByIMaccid(String IMaccid);
}
