package com.huawei.sharedrive.isystem.system.service;

import pw.cdmi.common.domain.DirectChainConfig;


public interface DirectChainService
{
    /**
     * 保存DirectChain的配置信息
     * 
     * @param directChain
     */
    void save(DirectChainConfig directChain,String secmatrixconfig);
    /**
     * 获取已配置的DirectChain服务器配置
     * 
     * @return
     */
    DirectChainConfig getDirectChain();
    DirectChainConfig getSecmatrixn();
    
}
