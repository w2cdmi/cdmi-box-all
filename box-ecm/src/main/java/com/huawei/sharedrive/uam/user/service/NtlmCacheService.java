package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.user.domain.NtlmCaches;

public interface NtlmCacheService
{
    String CACHE_KEY_CHALLENGE_PREFIX = "challenge_";
    
    void deleteNtlmCaches(String sessionId);
    
    void createNtlmCaches(String sessionId, String currentServer, String currentNetBios, byte[] challenge,
        String ip) throws InternalServerErrorException;
    
    NtlmCaches getNtlmCaches(String sessionId);
}
