package pw.cdmi.box.disk.enterprisecontrol;

import java.util.Map;

public interface EnterpriseAuthControlManager
{
    Map<String, String> getWebDomainLoginName(String loginName);
    
    boolean isCanNtlm(String appId);
}
