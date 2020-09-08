package com.huawei.sharedrive.uam.user.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.user.domain.NtlmCaches;
import com.huawei.sharedrive.uam.user.service.NtlmCacheService;

import pw.cdmi.common.cache.CacheClient;

@Component
public class NtlmCacheServiceImpl implements NtlmCacheService
{
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Value("${ntml.challenge.expire}")
    private int challengeExpiredAt;
    
    @Override
    public void deleteNtlmCaches(String sessionId)
    {
        String key = getChallengeKey(sessionId);
        cacheClient.deleteCache(key);
    }
    
    @Override
    public void createNtlmCaches(String sessionId, String currentServer, String currentNetBios,
        byte[] challenge, String ip) throws InternalServerErrorException
    {
        String key = getChallengeKey(sessionId);
        NtlmCaches ntlmCaches = new NtlmCaches();
        ntlmCaches.setServerChallenge(challenge);
        ntlmCaches.setCurrentServer(currentServer);
        ntlmCaches.setCurrentNetBios(currentNetBios);
        ntlmCaches.setIp(ip);
        Date expireTime = new Date(System.currentTimeMillis() + challengeExpiredAt);
        boolean success = cacheClient.setCache(key, ntlmCaches, expireTime);
        if (!success)
        {
            throw new InternalServerErrorException("Store challenge to memcache failed.");
        }
    }
    
    @Override
    public NtlmCaches getNtlmCaches(String sessionId)
    {
        String key = getChallengeKey(sessionId);
        return (NtlmCaches) cacheClient.getCache(key);
    }
    
    private String getChallengeKey(String sessionId)
    {
        return CACHE_KEY_CHALLENGE_PREFIX + sessionId;
    }
}
