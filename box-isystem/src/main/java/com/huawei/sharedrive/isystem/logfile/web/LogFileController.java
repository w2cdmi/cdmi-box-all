/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.logfile.web;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.Region;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.cluster.service.RegionService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.logfile.domain.LogAgent;
import com.huawei.sharedrive.isystem.logfile.domain.LogAgentNode;
import com.huawei.sharedrive.isystem.logfile.domain.LogFile;
import com.huawei.sharedrive.isystem.logfile.domain.QueryCondition;
import com.huawei.sharedrive.isystem.logfile.domain.QueryRegionInfo;
import com.huawei.sharedrive.isystem.logfile.domain.QueryResult;
import com.huawei.sharedrive.isystem.logfile.service.DownloadFileResponse;
import com.huawei.sharedrive.isystem.logfile.service.LogAgentService;
import com.huawei.sharedrive.isystem.logfile.service.LogFileService;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;

import pw.cdmi.common.log.UserLog;

/**
 * 
 * @author s90006125
 * 
 */
@Controller
@RequestMapping(value = "/log/logfile")
public class LogFileController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileController.class);
    
    @Autowired
    private LogAgentService logAgentService;
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private LogFileService logFileService;
    
    @Autowired
    private UserLogService userLogService;
    
    @Value("${logfile.search.mindate}")
    private String minDate;
    
    @Value("${logfile.search.maxresult}")
    private long maxResult;
    
    private static final int TYPE_UAS = 0;
    
    private static final int IS_SEARCH_RESULT = 1;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(@RequestParam(value = "type", required = true) int type, Model model)
    {
        setBaseData(model, type);
        try
        {
            QueryCondition condition = new QueryCondition("", "", getDateStart(), getDateEnd());
            model.addAttribute("queryCondition", condition);
        }
        catch (ParseException e)
        {
            LOGGER.warn("parse date exception", e);
        }
        
        if (TYPE_UAS == type)
        {
            initUASSearchPage(model);
            
            return "logManage/uasLogFile";
        }
        else
        {
            initDSSSearchPage(model, null, null);
            
            return "logManage/dssLogFile";
        }
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String seacheLogFile(QueryRegionInfo regionInfo, QueryCondition condition, Model model,
        String token)
    {
        super.checkToken(token);
        setBaseData(model, regionInfo.getClusterType());
        condition.setMaxResult(maxResult);
        
        if (condition.getStartTime() != null && condition.getEndTime() != null
            && condition.getStartTime().after(condition.getEndTime()))
        {
            throw new InvalidParameterException("Start EndTime Exception");
        }
        QueryResult queryResult = null;
        
        if (TYPE_UAS != regionInfo.getClusterType() && regionInfo.getClusterType() != 1)
        {
            throw new InvalidParameterException("clusterType Exception");
        }
        if (null == regionInfo.getClusterId())
        {
            queryResult = new QueryResult();
            queryResult.setCondition(condition);
            queryResult.setLogFiles(new ArrayList<LogFile>(0));
            queryResult.setTotal(0);
        }
        else
        {
            checkParamter(regionInfo.getClusterType(), regionInfo.getClusterId(), regionInfo.getRegionId());
            queryResult = logFileService.searchFile(regionInfo.getClusterId(), condition);
        }
        
        condition.setFileName(condition.getFileName());
        model.addAttribute("queryResult", queryResult);
        model.addAttribute("queryCondition", condition);
        
        model.addAttribute("isSearch", IS_SEARCH_RESULT);
        
        if (TYPE_UAS == regionInfo.getClusterType())
        {
            initUASSearchPage(model);
            return "logManage/uasLogFile";
        }
        else
        {
            initDSSSearchPage(model, regionInfo.getRegionId(), regionInfo.getClusterId());
            return "logManage/dssLogFile";
        }
    }
    
    public void checkParamter(int clusterType, Integer clusterId, Integer regionId)
    {
        
        if (TYPE_UAS != clusterType)
        {
            List<Region> regions = regionService.listRegion();
            List<DataCenter> dssList = logAgentService.listDataCenterWithoutMergeDC(regionId);
            boolean tempRegion = false;
            for (Region region : regions)
            {
                if (region.getId() == regionId)
                {
                    tempRegion = true;
                    break;
                }
            }
            if (!tempRegion)
            {
                throw new InvalidParameterException("regionId Exception");
            }
            boolean tempDC = false;
            for (DataCenter dc : dssList)
            {
                if (dc.getId() == clusterId)
                {
                    tempDC = true;
                    break;
                }
            }
            if (!tempDC)
            {
                throw new InvalidParameterException("regionId Exception");
            }
            
        }
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "/listdss/{regionId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> listDss(@PathVariable(value = "regionId") int regionId, Model model)
    {
        List<DataCenter> dssList = logAgentService.listDataCenterWithoutMergeDC(regionId);
        
        List<LogAgentNode> nodes = null;
        
        if (null == dssList)
        {
            dssList = new ArrayList<DataCenter>(0);
        }
        else if (!dssList.isEmpty())
        {
            nodes = getLogAgentNodesByDC(dssList.get(0));
        }
        
        if (null == nodes)
        {
            nodes = new ArrayList<LogAgentNode>(0);
        }
        
        Map<String, List<?>> map = new HashMap<String, List<?>>(2);
        map.put("dssList", dssList);
        map.put("logAgentNodeList", nodes);
        
        return new ResponseEntity(map, HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "/listLogAgentNode/{clusterId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<LogAgentNode> listLogAgentNode(@PathVariable(value = "clusterId") int clusterId,
        Model model)
    {
        List<LogAgentNode> nodes;
        // 如果是AC集群，就通过logagent提供的接口获取集群节点列表
        // 如果是DC集群，则直接查询resourcegroup_node表获取集群节点列表
        if (LogAgent.DEFAULT_CLUSTERID == clusterId)
        {
            nodes = logAgentService.getLogAgentNodeList(clusterId);
        }
        else
        {
            DataCenter dc = dcService.getDataCenter(clusterId);
            nodes = getLogAgentNodesByDC(dc);
        }
        if (null == nodes)
        {
            nodes = new ArrayList<LogAgentNode>(0);
        }
        return new ResponseEntity(nodes, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{clusterId}/{logFileId}", method = RequestMethod.GET)
    public void downLoadLogFile(@PathVariable("clusterId") int clusterId,
        @PathVariable("logFileId") String logFileId, OutputStream outputStream, HttpServletRequest request,
        HttpServletResponse response)
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.LOGAGENT_DOWN, new String[]{
            clusterId + "", logFileId});
        userLogService.saveUserLog(userLog);
        
        DownloadFileResponse downloadFileResponse = null;
        try
        {
            downloadFileResponse = logFileService.downLoadLogFile(clusterId, logFileId);
            outputStream(response, outputStream, downloadFileResponse);
            userLog.setDetail(UserLogType.LOGAGENT_DOWN.getDetails(new String[]{clusterId + "", logFileId}));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
        }
        catch (Exception e)
        {
            String message = "download file [ " + clusterId + " ; " + logFileId + " ] failed";
            LOGGER.warn(message, e);
            throw new BusinessException(message, e);
        }
        finally
        {
            IOUtils.closeQuietly(downloadFileResponse);
        }
    }
    
    private void outputStream(HttpServletResponse response, OutputStream outputStream,
        DownloadFileResponse downloadFileResponse)
    {
        if (null == downloadFileResponse.getLogFile()
            || null == downloadFileResponse.getLogFile().getInputStream())
        {
            String message = "logfile not exists";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        
        InputStream is = downloadFileResponse.getLogFile().getInputStream();
        if (null == is)
        {
            String message = "logfile not exists";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        
        try
        {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Connection", "close");
            response.setHeader("Content-Disposition", "attachment; filename=\""
                + downloadFileResponse.getLogFile().getFileName() + "\"");
            response.setHeader("Content-Length", String.valueOf(downloadFileResponse.getLogFile().getSize()));
            
            byte[] b = new byte[1024 * 64];
            int length = is.read(b);
            while (length > 0)
            {
                outputStream.write(b, 0, length);
                outputStream.flush();
                length = is.read(b);
            }
        }
        catch (Exception e)
        {
            String message = "out put log file failed";
            LOGGER.warn(message, e);
            throw new BusinessException(message, e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(is);
        }
    }
    
    @InitBinder
    public void initBinder(ServletRequestDataBinder binder)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    private Date getDateStart() throws ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        
        Calendar cal = Calendar.getInstance();
        
        cal.add(Calendar.DAY_OF_YEAR, -15);
        
        return dateFormat.parse(dateFormat.format(cal.getTime()));
    }
    
    private Date getDateEnd() throws ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        
        return dateFormat.parse(dateFormat.format(new Date()));
    }
    
    private void setBaseData(Model model, int clusterType)
    {
        model.addAttribute("minDate", minDate);
        model.addAttribute("clusterType", clusterType);
    }
    
    /**
     * 初始化UAS的查询界面
     * 
     * @param model
     */
    private void initUASSearchPage(Model model)
    {
        List<LogAgentNode> nodes = logAgentService.getLogAgentNodeList(LogAgent.DEFAULT_CLUSTERID);
        model.addAttribute("clusterId", LogAgent.DEFAULT_CLUSTERID);
        model.addAttribute("logAgentNodes", nodes);
    }
    
    private void initDSSSearchPage(Model model, Integer regionId, Integer dssId)
    {
        List<Region> regions = regionService.listRegion();
        model.addAttribute("regionList", regions);
        model.addAttribute("regionId", regionId);
        model.addAttribute("clusterId", dssId);
        
        if (null == regions || regions.isEmpty())
        {
            return;
        }
        
        if (null == regionId)
        {
            regionId = regions.get(0).getId();
        }
        
        List<DataCenter> dssList = logAgentService.listDataCenterWithoutMergeDC(regionId);
        model.addAttribute("dssList", dssList);
        
        if (null == dssList || dssList.isEmpty())
        {
            return;
        }
        
        DataCenter defaultDC = dssList.get(0);
        List<LogAgentNode> logAgentNodes = getLogAgentNodesByDC(defaultDC);
        
        model.addAttribute("logAgentNodes", logAgentNodes);
    }
    
    private List<LogAgentNode> getLogAgentNodesByDC(DataCenter dc)
    {
        if (null == dc)
        {
            String message = "dc is null";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        
        List<ResourceGroupNode> nodes = dc.getResourceGroup().getNodes();
        
        List<LogAgentNode> logAgentNodes = new ArrayList<LogAgentNode>(nodes.size());
        
        LogAgentNode agent = null;
        for (ResourceGroupNode n : nodes)
        {
            agent = new LogAgentNode(n.getName());
            logAgentNodes.add(agent);
        }
        
        return logAgentNodes;
    }
}
