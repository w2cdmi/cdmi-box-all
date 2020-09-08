package com.huawei.sharedrive.app.dns.service;

import java.util.List;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dns.domain.DssDomain;

public interface DssDomainService
{
    
    List<DssDomain> getAll();
    
    List<DssDomain> getByDssId(int dssId);
    
    /**
     * 上传、下载返回外部访问的域名
     * @param dssId
     * @return
     */
    String getOuterDomainByDssId(ResourceGroup group);
    
    String getDomainByDssId(ResourceGroup group);
    
}