package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.dao.AccessSpaceConfigDao;
import com.huawei.sharedrive.uam.enterprise.dao.NetRegionDao;
import com.huawei.sharedrive.uam.enterprise.dao.SecurityRoleDao;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.ClientType;
import com.huawei.sharedrive.uam.enterprise.domain.ClientTypeEnum;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.SecOperation;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.service.AccessSpaceConfigService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;
import com.huawei.sharedrive.uam.util.SecurityConfigUtil;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.BundleUtil;

@Component
public class AccessSpaceConfigServiceImpl implements AccessSpaceConfigService
{
    
    @Autowired
    private AccessSpaceConfigDao accessSpaceConfigDao;
    
    @Autowired
    private NetRegionDao netRegionDao;
    
    @Autowired
    private SecurityRoleDao securityDao;
    
    public static final int TYPE_OF_SELECT_ALL = -1;
    
    @Override
    public void create(AccessSpaceConfig accessSpaceConfig, Locale locale)
    {
        checkAccessSpaceConfigParameter(accessSpaceConfig);
        Date now = new Date();
        accessSpaceConfig.setCreatedAt(now);
        accessSpaceConfig.setModifiedAt(now);
        accessSpaceConfig.setClientType((long) SecurityConfigConstants.TYPE_OF_SELECT_ALL);
        accessSpaceConfigDao.create(accessSpaceConfig);
    }
    
    @Override
    public void delete(String id)
    {
        accessSpaceConfigDao.delete(id);
    }
    
    @Override
    public long getByDomainExclusiveId(AccessSpaceConfig accessSpaceConfig)
    {
        return accessSpaceConfigDao.getByDomainExclusiveId(accessSpaceConfig);
    }
    
    @Override
    public AccessSpaceConfig getById(String id)
    {
        
        return accessSpaceConfigDao.getById(id);
    }
    
    @Override
    public Page<AccessSpaceConfig> getFilterd(AccessSpaceConfig filter, PageRequest pageRequest, Locale locale)
    {
        if (filter.getSafeRoleId() != null && filter.getSafeRoleId() == TYPE_OF_SELECT_ALL)
        {
            filter.setSafeRoleId(null);
        }
        if (filter.getNetRegionId() != null && filter.getNetRegionId() == TYPE_OF_SELECT_ALL)
        {
            filter.setNetRegionId(null);
        }
        if (filter.getClientType() != null && filter.getClientType() == TYPE_OF_SELECT_ALL)
        {
            filter.setClientType(null);
        }
        int total = accessSpaceConfigDao.getFilterdCount(filter);
        List<AccessSpaceConfig> content = accessSpaceConfigDao.getFilterd(filter,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        
        long accountId = filter.getAccountId();
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        
        NetRegion nr = new NetRegion();
        nr.setAccountId(accountId);
        List<NetRegion> listRegion = netRegionDao.getFilterd(nr, null, null);
        
        List<ClientType> listClientType = getClientTypeList(locale);
        
        for (AccessSpaceConfig accessSpaceConfig : content)
        {
            
            accessSpaceConfig.setSafeRoleName(HtmlUtils.htmlEscape(SecurityConfigUtil.getSecurityRileById(accessSpaceConfig.getSafeRoleId(),
                listSecurityRole,
                locale)));
            accessSpaceConfig.setNetRegionName(HtmlUtils.htmlEscape(getNetRegionNameById(accessSpaceConfig.getNetRegionId(),
                listRegion,
                locale)));
            accessSpaceConfig.setClientName(getClientTypeById(accessSpaceConfig.getClientType(),
                listClientType,
                locale));
            accessSpaceConfig.setTargetSafeRoleName(HtmlUtils.htmlEscape(SecurityConfigUtil.getSecurityRileById(accessSpaceConfig.getTargetSafeRoleId(),
                listSecurityRole,
                locale)));
        }
        
        Page<AccessSpaceConfig> page = new PageImpl<AccessSpaceConfig>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public List<AccessSpaceConfig> getFilterdList(AccessSpaceConfig filter)
    {
        
        List<AccessSpaceConfig> content = accessSpaceConfigDao.getFilterd(filter, null, null);
        return content;
        
    }
    
    @Override
    public AccessSpaceConfig getObject(AccessSpaceConfig spaceConfig)
    {
        return accessSpaceConfigDao.getObject(spaceConfig);
    }
    
    @Override
    public boolean isDuplicateNetConfigValues(AccessSpaceConfig accessSpaceConfig)
    {
        return accessSpaceConfigDao.isDuplicateValues(accessSpaceConfig);
    }
    
    @Override
    public boolean isDuplicateValues(AccessSpaceConfig accessSpaceConfig)
    {
        return accessSpaceConfigDao.isDuplicateValues(accessSpaceConfig);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public boolean judgeSpaceMatrix(long accountId, Integer secRole, Long netRegionId, Integer spaceSecRole,
        SecOperation secOperation, Integer deviceType)
    {
        List<AccessSpaceConfig> list = this.accessSpaceConfigDao.getListBySafeRoleId(secRole,accountId,
            secOperation.getIntValue());
        for (AccessSpaceConfig dbAccessSpaceConfig : list)
        {
            if (match(dbAccessSpaceConfig, secRole, netRegionId, spaceSecRole, deviceType))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void update(AccessSpaceConfig accessSpaceConfig, Locale locale)
    {
        checkAccessSpaceConfigParameter(accessSpaceConfig);
        Date now = new Date();
        accessSpaceConfig.setModifiedAt(now);
        accessSpaceConfig.setClientType((long) SecurityConfigConstants.TYPE_OF_SELECT_ALL);
        accessSpaceConfigDao.update(accessSpaceConfig);
    }
    
    private void checkAccessSpaceConfigParameter(AccessSpaceConfig accessSpaceConfig)
    {
        checkNetRegionIdRule(accessSpaceConfig);
        
        checkSecurityIdRule(accessSpaceConfig);
        
        checkTargetSecurityIdRule(accessSpaceConfig);
        
    }
    
    private void checkNetRegionIdRule(AccessSpaceConfig accessSpaceConfig)
    {
        NetRegion nr = new NetRegion();
        nr.setAccountId(accessSpaceConfig.getAccountId());
        List<NetRegion> listRegion = netRegionDao.getFilterd(nr, null, null);
        long id = accessSpaceConfig.getNetRegionId();
        if (id == -1)
        {
            return;
        }
        boolean isAllowed = false;
        for (NetRegion s : listRegion)
        {
            if (s.getId() == id)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
    }
    
    private void checkSecurityIdRule(AccessSpaceConfig accessSpaceConfig)
    {
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accessSpaceConfig.getAccountId());
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        long id = accessSpaceConfig.getSafeRoleId();
        if (id == -1)
        {
            return;
        }
        boolean isAllowed = false;
        for (SecurityRole s : listSecurityRole)
        {
            if (s.getId() == id)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
    }
    
    private void checkTargetSecurityIdRule(AccessSpaceConfig accessSpaceConfig)
    {
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accessSpaceConfig.getAccountId());
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        long id = accessSpaceConfig.getTargetSafeRoleId();
        if (id == -1)
        {
            return;
        }
        boolean isAllowed = false;
        for (SecurityRole s : listSecurityRole)
        {
            if (s.getId() == id)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
    }
    
    private String getClientTypeById(long id, List<ClientType> listClientType, Locale locale)
    {
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
    
    private List<ClientType> getClientTypeList(Locale locale)
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
    
    private String getNetRegionNameById(long id, List<NetRegion> listNetRegion, Locale locale)
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
    
    private boolean match(AccessSpaceConfig dbAccessSpaceConfig, Integer secRole, Long netRegionId,
        Integer spaceSecRole, Integer deviceType)
    {
        if (!matchRegion(dbAccessSpaceConfig.getNetRegionId(), netRegionId))
        {
            return false;
        }
        if (!matchSecRole(dbAccessSpaceConfig.getSafeRoleId(), secRole))
        {
            return false;
        }
        if (!matchSecRole(dbAccessSpaceConfig.getTargetSafeRoleId(), spaceSecRole))
        {
            return false;
        }
        if (!matchDeviceType(dbAccessSpaceConfig.getClientType(), deviceType))
        {
            return false;
        }
        return true;
        
    }
    
    private boolean matchDeviceType(Long clientType, Integer deviceType)
    {
        if (null == clientType)
        {
            return false;
        }
        if (clientType.byteValue() == AccessSpaceConfig.ALL)
        {
            return true;
        }
        if (null == deviceType)
        {
            return false;
        }
        if (clientType.intValue() == deviceType.intValue())
        {
            return true;
        }
        return false;
    }
    
    private boolean matchRegion(Long netRegionId, Long accessRegionId)
    {
        if (null == netRegionId)
        {
            return false;
        }
        if (netRegionId.byteValue() == AccessSpaceConfig.ALL)
        {
            return true;
        }
        if (null == accessRegionId)
        {
            return false;
        }
        if (netRegionId.intValue() == accessRegionId.intValue())
        {
            return true;
        }
        return false;
    }
    
    private boolean matchSecRole(Long targetSafeRoleId, Integer spaceSecRole)
    {
        if (null == targetSafeRoleId)
        {
            return false;
        }
        if (targetSafeRoleId.byteValue() == AccessSpaceConfig.ALL)
        {
            return true;
        }
        if (null == spaceSecRole)
        {
            return false;
        }
        if (targetSafeRoleId.intValue() == spaceSecRole.intValue())
        {
            return true;
        }
        return false;
    }
    
    @Override
    public void deleteByCondition(AccessSpaceConfig accessSpaceConfig)
    {
        accessSpaceConfigDao.deleteByCondition(accessSpaceConfig);
        
    }
    
}
