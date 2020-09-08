package pw.cdmi.box.uam.enterprise.manager;

import java.io.IOException;
import java.util.Locale;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseManager
{
    long create(Enterprise enterprise, Locale locale) throws IOException;
    
    Page<Enterprise> getFilterd(String filter, Integer status, String appId, PageRequest pageRequest);
    
    /** 根据企业的ID获取该企业信息 **/
    Enterprise getById(long id);
    
    /** 获得业务系统的部署企业信息 **/
    Enterprise getByOwnerId(long id);
    
    Enterprise getByContactEmail(String email);
    
    long getByDomainExclusiveId(Enterprise enterprise);
    
    long createEnterprise(Enterprise enterprise)throws IOException;
    
    void createEnterpriseAdmin(Enterprise enterprise,Locale locale)throws IOException;
    
    void updateEnterprise(Enterprise enterprise) throws IOException;
    
}
