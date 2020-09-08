package com.huawei.sharedrive.uam.enterprise.manager;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface NetRegionManager
{
    
    long create(NetRegion netRegion) throws IOException;
    
    long create(NetRegionIp netRegionIp) throws IOException;
    
    long create(NetRegionIp netRegionIp, NetRegion netRegion, HttpServletRequest req) throws IOException;
    
    void delete(int id) throws IOException;
    
    void modify(NetRegion netRegion) throws IOException;
    
    void modify(NetRegionIp netRegionIp) throws IOException;
    
    void modify(NetRegionIp netRegionIp, long id) throws IOException;
    
    Page<NetRegion> getFilterd(NetRegion filter, PageRequest pageRequest);
    
    Page<NetRegionIp> getFilterd(NetRegionIp filter, PageRequest pageRequest);
    
    NetRegion getById(long id);
    
    NetRegionIp getByNetRegionIpId(long id);
    
    long getByDomainExclusiveId(NetRegion netRegion);
    
    void updateEnterpriseInfo(NetRegion netRegion);
    
    List<NetRegion> getFilterdNetRegionList(NetRegion filterr);
    
    List<NetRegionIp> getFilterdList(NetRegionIp filterr);
    
    List<NetRegionIp> getListRegionIp(NetRegionIp netRegionIp);
    
    void delete(NetRegion netRegion) throws IOException;
    
}
