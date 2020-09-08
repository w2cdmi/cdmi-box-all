package com.huawei.sharedrive.uam.openapi.manager;

import com.huawei.common.ntlmv2.liferay.NtlmUserAccount;
import com.huawei.sharedrive.uam.exception.BaseRunException;

public interface NtlmApiManager
{
    String getChallenge(String usernameHash, String key, long authServerId, String ip)
        throws BaseRunException;
    
    NtlmUserAccount validateChallenge(String challenge, String key, long authServerId, String ip)
        throws BaseRunException;
}
