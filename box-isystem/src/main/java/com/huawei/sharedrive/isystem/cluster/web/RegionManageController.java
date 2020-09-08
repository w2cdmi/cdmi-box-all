/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.web;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.Region;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.RWStatus;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.Status;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.cluster.service.RegionService;
import com.huawei.sharedrive.isystem.cluster.service.ResourceGroupService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;

import pw.cdmi.common.log.UserLog;

/**
 * 
 * 
 * 区域
 * 
 * @author d00199602
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "/cluster/region")
public class RegionManageController extends AbstractCommonController
{
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        return "clusterManage/clusterMain";
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String list(String filter, Integer page, Model model)
    {
        List<Region> regionList = regionService.listRegion();
        if (!regionList.isEmpty())
        {
            List<DataCenter> dataCenters = null;
            for (Region region : regionList)
            {
                dataCenters = dcService.listDataCenterRe(region.getId());
                if (null == dataCenters)
                {
                    continue;
                }
                region.setDataCenters(dataCenters);
            }
        }
        
        model.addAttribute("regionList", regionList);
        fillStatus(model);
        return "clusterManage/regionList";
    }
    
    /**
     * 进入Region创建页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String enterCreate(Model model)
    {
        return "clusterManage/createRegion";
    }
    
    /**
     * 创建Region
     * 
     * @param dc
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> create(Region region, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.REGION_CREATE,
            new String[]{region.getCode()});
        userLogService.saveUserLog(userLog);
        
        Set violations = validator.validate(region);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        regionService.addRegion(region.getName(), region.getCode(), region.getDescription());
        
        userLog.setDetail(UserLogType.REGION_CREATE.getDetails(new String[]{region.getCode()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 进入Region修改页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "change/{regionID}", method = RequestMethod.GET)
    public String enterChange(@PathVariable(value = "regionID") int regionID, Model model)
    {
        
        Region region = regionService.getRegion(regionID);
        region.setCode(region.getCode());
        model.addAttribute("region", region);
        return "clusterManage/changeRegion";
    }
    
    /**
     * 修改region
     * 
     * @param dc
     * @return
     */
    @RequestMapping(value = "change", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> change(Region region, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        Region srcRegion = regionService.getRegion(region.getId());
        String srcname = srcRegion == null ? "" : srcRegion.getName();
        UserLog userLog = userLogService.initUserLog(request, UserLogType.REGION_MODIFY, new String[]{
            srcname, region.getName()});
        userLogService.saveUserLog(userLog);
        region.setCode(srcRegion.getCode());
        Set violations = validator.validate(region);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        regionService.changeRegion(region.getId(), region.getName(), region.getCode(), region.getDescription());
        userLog.setDetail(UserLogType.REGION_MODIFY.getDetails(new String[]{srcname, region.getName()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "enterSetDefaultRegion", method = {RequestMethod.GET})
    public String enterSetDefaultRegion(Model model)
    {
        model.addAttribute("regionList", regionService.listRegion());
        return "clusterManage/setDefaultRegion";
    }
    
    /**
     * 修改默认区域
     * 
     * @param dc
     * @return
     */
    @RequestMapping(value = "setDefaultRegion", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> setDefaultRegion(String region, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request, UserLogType.REGION_DEFAULT, new String[]{""});
        userLogService.saveUserLog(userLog);
        int val = -1;
        val = Integer.parseInt(region);
        regionService.setDefaultRegion(val);
        Region region2 = regionService.getRegion(val);
        userLog.setDetail(UserLogType.REGION_DEFAULT.getDetails(new String[]{region2.getCode()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 删除区域
     * 
     * @param id 要删除的DC资源组ID
     * @return 删除结果
     */
    @RequestMapping(value = "/delete/{regionID}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable(value = "regionID") int regionID, Region region,
        HttpServletRequest request, String token)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.REGION_DELETE,
            new String[]{String.valueOf(regionID), region.getCode()});
        userLogService.saveUserLog(userLog);
        regionService.deleteRegion(regionID);
        userLog.setDetail(UserLogType.REGION_DELETE.getDetails(new String[]{String.valueOf(regionID),
            region.getCode()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/updateStatus/{dcid}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateStatus(@PathVariable(value = "dcid") int dcid, String name,
        HttpServletRequest request,int code, String token)
    {
        super.checkToken(token);
        
        Status status=Status.parseStatus(code);
        if (null == status)
        {
            throw new InvalidParamException("code = " + code);
        }
        
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.UPDATE_REGION_STAUTS,
            new String[]{String.valueOf(dcid), name,status.name()});
        userLogService.saveUserLog(userLog);
        
        resourceGroupService.updateStatus(dcid, status);
        userLog.setDetail(UserLogType.UPDATE_REGION_STAUTS.getDetails(new String[]{String.valueOf(dcid),
            name,status.name()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/updateRWStatus/{dcid}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateRWStatus(@PathVariable(value = "dcid") int dcid,String name,
        HttpServletRequest request,int code, String token)
    {
        super.checkToken(token);
        RWStatus status=RWStatus.parseStatus(code);
        if (null == status)
        {
            throw new InvalidParamException("code = " + code);
        }
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.UPDATE_REGION_RW,
            new String[]{String.valueOf(dcid), name,status.name()});
        userLogService.saveUserLog(userLog);
        resourceGroupService.updateRWStatus(dcid, status);
        userLog.setDetail(UserLogType.UPDATE_REGION_RW.getDetails(new String[]{String.valueOf(dcid),
            name,status.name()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        
        return new ResponseEntity(HttpStatus.OK);
    }
}
