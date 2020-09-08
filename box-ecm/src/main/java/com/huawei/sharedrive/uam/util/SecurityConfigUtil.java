package com.huawei.sharedrive.uam.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.domain.ClientType;
import com.huawei.sharedrive.uam.enterprise.domain.ClientTypeEnum;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.domain.SecOperation;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.core.utils.BundleUtil;

public final class SecurityConfigUtil
{
    private SecurityConfigUtil()
    {
        
    }
    
    public static final int TYPE_OF_SELECT_ALL = -1;
    
    public static String getSecurityRileById(long id, List<SecurityRole> listSecurityRole, Locale locale)
    {
        if (id == TYPE_OF_SELECT_ALL)
        {
            return BundleUtil.getText("messages", locale, "security.role.any", null);
        }
        String roleName = "";
        for (SecurityRole s : listSecurityRole)
        {
            if (s.getId() == id)
            {
                roleName = s.getRoleName();
            }
            
        }
        return roleName;
    }
    
    public static String getResourceTypeNameById(long id, List<SafeLevel> listSafeLevel, Locale locale)
    {
        if (id == TYPE_OF_SELECT_ALL)
        {
            return BundleUtil.getText("messages", locale, "common.select.all", null);
        }
        String name = "";
        for (SafeLevel s : listSafeLevel)
        {
            if (s.getId() == id)
            {
                name = s.getSafeLevelName();
            }
            
        }
        return name;
    }
    
    public static String getNetRegionNameById(long id, List<NetRegion> listNetRegion, Locale locale)
    {
        if (id == TYPE_OF_SELECT_ALL)
        {
            return BundleUtil.getText("messages", locale, "network.region.any", null);
        }
        String roleName = "";
        for (NetRegion s : listNetRegion)
        {
            if (s.getId() == id)
            {
                roleName = s.getNetRegionName();
            }
            
        }
        return roleName;
    }
    
    public static List<ClientType> getClientTypeList(Locale locale)
    {
        ClientTypeEnum[] clientTypes = ClientTypeEnum.values();
        List<ClientTypeEnum> operateTypeList = new ArrayList<ClientTypeEnum>(10);
        for (ClientTypeEnum operateType : clientTypes)
        {
            operateTypeList.add(operateType);
        }
        List<ClientType> listDomain = new ArrayList<ClientType>(10);
        ClientType operateTypeDomain;
        for (int i = 0; i < operateTypeList.size(); i++)
        {
            operateTypeDomain = new ClientType();
            operateTypeDomain.setId(operateTypeList.get(i).getCode());
            operateTypeDomain.setClientTypeName(operateTypeList.get(i).getDetails(locale, null));
            listDomain.add(operateTypeDomain);
        }
        return listDomain;
    }
    
    public static String getClientTypeById(long id, Locale locale)
    {
        List<ClientType> listClientType = getClientTypeList(locale);
        if (id == TYPE_OF_SELECT_ALL)
        {
            return ClientTypeEnum.CLIENT_ALL.getDetails(locale, null);
        }
        String roleName = "";
        for (ClientType s : listClientType)
        {
            if (s.getId() == id)
            {
                roleName = s.getClientTypeName();
            }
            
        }
        return roleName;
    }
    
    public static String getResourceTypeByIds(String operationIds, Locale locale, List<SafeLevel> list)
    {
        if (StringUtils.isBlank(operationIds))
        {
            return BundleUtil.getText("messages", locale, "accessconfig.resource.type.none", null);
        }
        if (SecurityConfigConstants.TYPE_OF_ALL_RESOURCE_STRATEGY.equals(operationIds))
        {
            return BundleUtil.getText("messages", locale, "common.select.all", null);
        }
        String[] str = operationIds.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        for (String tempStr : str)
        {
            stringBuffer.append(getOperationNameById(Long.parseLong(tempStr), list)).append(' ');
        }
        return stringBuffer.toString();
    }
    
    private static String getOperationNameById(long id, List<SafeLevel> list)
    {
        String name = "";
        for (SafeLevel c : list)
        {
            if (c.getId() == id)
            {
                name = c.getSafeLevelName();
                break;
            }
        }
        return name;
    }
    
    public static long handleOperationMatrix(String[] operTypes)
    {
        long temp = 1;
        for (String operateStr : operTypes)
        {
            if (StringUtils.isNotEmpty(operateStr))
            {
                SecOperation secOperation = SecOperation.getSecOperation(Integer.parseInt(operateStr));
                if (null == secOperation)
                {
                    throw new InvalidParamterException();
                }
                temp = temp * secOperation.getIntValue();
            }
        }
        return temp;
    }
    
    public static List<SafeLevel> getSafeLevelList(List<SafeLevel> list)
    {
        for (SafeLevel sr : list)
        {
            sr.setSafeLevelName(sr.getSafeLevelName());
            sr.setSafeLevelDesc(HtmlUtils.htmlEscape(sr.getSafeLevelDesc()));
        }
        return list;
        
    }
    
}
