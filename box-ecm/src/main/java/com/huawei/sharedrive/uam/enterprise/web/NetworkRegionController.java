package com.huawei.sharedrive.uam.enterprise.web;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;
import com.huawei.sharedrive.uam.enterprise.manager.NetRegionManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.NoSuchSecurityLevelException;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/security")
public class NetworkRegionController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(NetworkRegionController.class);
    
    @Autowired
    private NetRegionManager manager;
    
    @Autowired
    private AdminLogManager adminlogManager;
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @RequestMapping(value = "listNetRegion/{appId}/", method = {RequestMethod.GET})
    public String enterNetRegion(Model model, @PathVariable(value = "appId") String appId)
    {
        NetRegion nr = new NetRegion();
        nr.setAccountId(getAccoutId(appId));
        List<NetRegion> list = manager.getFilterdNetRegionList(nr);
        model.addAttribute("netRegionList", list);
        model.addAttribute("appId", appId);
        return "enterprise/security/netRegionList";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "listNetRegion/{appId}/", method = {RequestMethod.POST})
    public ResponseEntity list(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber, NetRegion netRegion,
        String token)
    {
        super.checkToken(token);
        PageRequest request = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);
        netRegion.setAccountId(getAccoutId(appId));
        if (netRegion.getId() != null && netRegion.getId() == -1)
        {
            netRegion.setId(null);
        }
        Page<NetRegion> securityRolePage = manager.getFilterd(netRegion, request);
        
        return new ResponseEntity(securityRolePage, HttpStatus.OK);
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "createNetRegion/{appId}/", method = RequestMethod.GET)
    public String enterCreateLocal(Model model, @PathVariable(value = "appId") String appId)
    {
        model.addAttribute("appId", appId);
        return "enterprise/security/createNetRegion";
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modifyNetRegion/{appId}/", method = RequestMethod.GET)
    public String enterModify(@PathVariable(value = "appId") String appId, long id, Model model)
    {
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        NetRegion admin = manager.getById(id);
        if (null != admin)
        {
            model.addAttribute("securityRole", admin);
        }
        else
        {
            logger.error("the modified data is not exists.");
        }
        return "enterprise/security/modifyNetRegion";
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "modifyNetRegion/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modify(@PathVariable(value = "appId") String appId, NetRegion netRegion,
        HttpServletRequest req, String token) throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        String[] description = new String[]{getEnterpriseName(), appId, netRegion.getNetRegionName(),
            netRegion.getNetRegionDesc()};
        try
        {
            checkRequestValue(netRegion);
            manager.modify(netRegion);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_UPDATE, description);
        }
        catch (RuntimeException e)
        {
            logger.error("modify netregion fail");
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_UPDATE_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "createNetRegion/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createLocal(@PathVariable(value = "appId") String appId,
        NetRegion netRegion, HttpServletRequest req, String token) throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        String[] description = new String[]{getEnterpriseName(), appId, netRegion.getNetRegionName(),
            netRegion.getNetRegionDesc()};
        try
        {
            checkRequestValue(netRegion);
            netRegion.setAccountId(getAccoutId(appId));
            manager.create(netRegion);
            
            String sessionId = req.getSession().getId();
            String sessionKey = "temp_netRegionIp_" + sessionId;
            Object cache = cacheClient.getCache(sessionKey);
            List<NetRegionIp> list = (List<NetRegionIp>) cache;
            if (list != null)
            {
                for (NetRegionIp item : list)
                {
                    item.setAccountId(getAccoutId(appId));
                    item.setNetRegionId(netRegion.getId());
                    manager.create(item, netRegion, req);
                }
                cacheClient.deleteCache(sessionKey);
            }
            
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_ADD, description);
        }
        catch (RuntimeException e)
        {
            logger.error("create NetRegion fail", e);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_ADD_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void checkRequestValue(NetRegion netRegion)
    {
        Set violations = validator.validate(netRegion);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
    }
    
    @RequestMapping(value = "deleteNetworkRegion/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteNetworkRegion(@PathVariable(value = "appId") String appId, long id,
        HttpServletRequest req, String token) throws IOException
    {
        super.checkToken(token);
        NetRegion netRegion = new NetRegion();
        netRegion.setId(id);
        netRegion.setAccountId(getAccoutId(appId));
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        NetRegion dbSafeLevel = manager.getById(id);
        if (null == dbSafeLevel)
        {
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_DELETE_ERROR, new String[]{
                getEnterpriseName(), appId});
            throw new NoSuchSecurityLevelException();
        }
        String name = dbSafeLevel.getNetRegionName();
        String[] description = new String[]{getEnterpriseName(), appId, name, dbSafeLevel.getNetRegionDesc()};
        manager.delete(netRegion);
        adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_DELETE, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
