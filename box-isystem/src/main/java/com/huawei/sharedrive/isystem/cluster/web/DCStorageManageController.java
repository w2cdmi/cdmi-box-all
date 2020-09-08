package com.huawei.sharedrive.isystem.cluster.web;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.thrift.TException;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.cluster.FileSystemConstant;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.filesystem.NasStorage;
import com.huawei.sharedrive.isystem.cluster.domain.filesystem.UdsStorage;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.thrift.client.StorageResourceServiceClient;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.thrift.filesystem.StorageInfo;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "/cluster/dcdetailmanage")
public class DCStorageManageController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(DCStorageManageController.class);
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    @Autowired
    private DCService dcService;
    
    @RequestMapping(value = "enterCreateUDSStorage/{dcId}", method = {RequestMethod.GET})
    public String enterCreateUDSStorage(@PathVariable("dcId") int dcId, Model model)
    {
        model.addAttribute("dcId", dcId);
        return "clusterManage/createUDSStorage";
    }
    
    @RequestMapping(value = "enterCreateNASStorage/{dcId}", method = {RequestMethod.GET})
    public String enterCreateNASStorage(@PathVariable("dcId") int dcId, Model model)
    {
        model.addAttribute("dcId", dcId);
        return "clusterManage/createNASStorage";
    }
    
    /**
     * 进入存储资源新建页面
     * 
     * @return
     * @throws TException
     */
    @RequestMapping(value = "createNASStorage", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> createNASStorage(NasStorage nasStorage, HttpServletRequest request, String token)
        throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STORAGE_ADD,
            new String[]{"", nasStorage.getPath()});
            
        userLogService.saveUserLog(userLog);
        
        Set violations = validator.validate(nasStorage);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        
        if (nasStorage.getRetrieval() >= nasStorage.getMaxUtilization())
        {
            userLog
                .setDetail(UserLogType.STORAGE_ADD.getErrorDetails(new String[]{"", nasStorage.getPath()}));
            userLog.setType(UserLogType.STORAGE_ADD.getValue());
            userLogService.update(userLog);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        
        DataCenter dataCenter = dcService.getDataCenter(nasStorage.getDcId());
        
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setEndpoint(nasStorage.getPath());
        storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_NAS);
        storageInfo.setWriteAlbe(true);
        storageInfo.setAvailAble(true);
        storageInfo.setNoSpace(false);
        storageInfo.setMaxUtilization(nasStorage.getMaxUtilization());
        storageInfo.setRetrieval(nasStorage.getRetrieval());
        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
            .addStorageResource(nasStorage.getDcId(), storageInfo);
        userLog.setDetail(
            UserLogType.STORAGE_ADD.getDetails(new String[]{dataCenter.getName(), nasStorage.getPath()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 进入存储资源新建页面
     * 
     * @return
     * @throws TException
     */
    @RequestMapping(value = "createUDSStorage", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> createUDSStorage(UdsStorage udsStorage, HttpServletRequest request, String token)
        throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STORAGE_ADD,
            new String[]{"", udsStorage.getDomain()});
            
        userLogService.saveUserLog(userLog);
        
        Set violations = validator.validate(udsStorage);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        
        if (udsStorage.getPort() < 1 || udsStorage.getPort() > 65535)
        {
            userLog
                .setDetail(UserLogType.STORAGE_ADD.getErrorDetails(new String[]{"", udsStorage.getDomain()}));
            userLog.setType(UserLogType.STORAGE_ADD.getValue());
            userLogService.update(userLog);
            throw new ConstraintViolationException("port is invaid", null);
        }
        
        DataCenter dataCenter = dcService.getDataCenter(udsStorage.getDcId());
        String udsStorageDesc = new StringBuffer().append(udsStorage.getDomain())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(udsStorage.getPort())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(udsStorage.getAccessKey())
            .toString();
            
        String storageResource = new StringBuffer().append(udsStorage.getDomain())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(udsStorage.getPort())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(udsStorage.getPort())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(udsStorage.getAccessKey())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(udsStorage.getSecretKey())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(udsStorage.getProvider())
            .toString();
            
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setEndpoint(storageResource);
        if("ALIAI".equals(udsStorage.getProvider()) || "ALIOSS".equals(udsStorage.getProvider())){
        	storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_ALIYUN_OSS);
        }else if("QYOS".equals(udsStorage.getProvider())){
        	storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_QCloud_OS);
        }else if("HWOBS".equals(udsStorage.getProvider())){
        	storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_UDS);
        }else if("TENTOBS".equals(udsStorage.getProvider())){
             storageInfo.setFsType(FileSystemConstant.TENCENT_COS_FileSystem);
        }
        else{
        	storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_NAS);
        }
        storageInfo.setWriteAlbe(true);
        storageInfo.setAvailAble(true);
        storageInfo.setNoSpace(false);
        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
            .addStorageResource(udsStorage.getDcId(), storageInfo);
        userLog.setDetail(
            UserLogType.STORAGE_ADD.getDetails(new String[]{dataCenter.getName(), udsStorageDesc}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 进入存储资源新建页面
     * 
     * @return
     * @throws TException
     */
    @RequestMapping(value = "changeNASStorage", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> changeNASStorage(NasStorage changeInfo, HttpServletRequest request, String token)
        throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STORAGE_MODIFY,
            new String[]{changeInfo.getDcId() + "", changeInfo.getPath()});
            
        userLogService.saveUserLog(userLog);
        
        Set violations = validator.validate(changeInfo);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        if (changeInfo.getRetrieval() >= changeInfo.getMaxUtilization())
        {
            userLog.setDetail(UserLogType.STORAGE_MODIFY
                .getErrorDetails(new String[]{changeInfo.getDcId() + "", changeInfo.getPath()}));
            userLog.setType(UserLogType.STORAGE_MODIFY.getValue());
            userLogService.update(userLog);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        
        DataCenter dataCenter = dcService.getDataCenter(changeInfo.getDcId());
        
        StorageInfo storageInfo = ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
            .getStorageResource(changeInfo.getDcId(), changeInfo.getFsId());
        if (null == storageInfo)
        {
            logger.warn("storageInfo not exists [ " + ReflectionToStringBuilder.toString(changeInfo) + " ]");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        // 只能更新endpoint、maxutilization、retrieval三个值
        storageInfo.setId(changeInfo.getFsId());
        storageInfo.setEndpoint(changeInfo.getPath());
        storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_NAS);
        storageInfo.setMaxUtilization(changeInfo.getMaxUtilization());
        storageInfo.setRetrieval(changeInfo.getRetrieval());
        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
            .changeStorageResource(changeInfo.getDcId(), storageInfo);
            
        userLog.setDetail(
            UserLogType.STORAGE_MODIFY.getDetails(new String[]{dataCenter.getName(), changeInfo.getPath()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "changeUDSStorage", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> changeUDSStorage(UdsStorage changeInfo, HttpServletRequest request, String token)
        throws TException
    {
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STORAGE_MODIFY,
            new String[]{changeInfo.getDcId() + "", changeInfo.getDomain()});
            
        userLogService.saveUserLog(userLog);
        
        String domain = changeInfo.getDomain();
        int port = changeInfo.getPort();
        if (StringUtils.isBlank(domain) || domain.length() > 255)
        {
            userLog.setDetail(UserLogType.STORAGE_MODIFY
                .getErrorDetails(new String[]{changeInfo.getDcId() + "", changeInfo.getDomain()}));
            userLog.setType(UserLogType.STORAGE_MODIFY.getValue());
            userLogService.update(userLog);
            throw new InvalidParamException("invalid domain value");
        }
        if (port < 1 || port > 65535)
        {
            userLog.setDetail(UserLogType.STORAGE_MODIFY
                .getErrorDetails(new String[]{changeInfo.getDcId() + "", changeInfo.getDomain()}));
            userLog.setType(UserLogType.STORAGE_MODIFY.getValue());
            userLogService.update(userLog);
            throw new InvalidParamException("invalid port value");
        }
        DataCenter dataCenter = dcService.getDataCenter(changeInfo.getDcId());
        String udsStorageDesc = new StringBuffer().append(changeInfo.getDomain())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(changeInfo.getPort())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(changeInfo.getAccessKey())
            .toString();
            
        StorageInfo storageInfo = ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
            .getStorageResource(changeInfo.getDcId(), changeInfo.getFsId());
        if (null == storageInfo)
        {
            logger.warn("storageInfo not exists [ " + ReflectionToStringBuilder.toString(changeInfo) + " ]");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        UdsStorage oldUdsStorage = new UdsStorage(storageInfo);
        
        // 对于UDS修改的时候，只允许修改domain和port，所以ak、sk两个值无意义，直接采用老的信息
        String storageResource = new StringBuffer().append(changeInfo.getDomain())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(changeInfo.getPort())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(changeInfo.getPort())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(oldUdsStorage.getAccessKey())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(oldUdsStorage.getSecretKey())
            .append(Constants.UDS_STORAGE_SPLIT_CHAR)
            .append(oldUdsStorage.getProvider())
            .toString();
            
        storageInfo.setId(changeInfo.getFsId());
        storageInfo.setEndpoint(storageResource);
        if("ALIAI".equals(oldUdsStorage.getProvider()) || "ALIOSS".equals(oldUdsStorage.getProvider())){
            storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_ALIYUN_OSS);
        }else if("QYOS".equals(oldUdsStorage.getProvider())){
            storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_QCloud_OS);
        }else if("HWOBS".equals(oldUdsStorage.getProvider())){
            storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_UDS);
        }else if("TENTOBS".equals(oldUdsStorage.getProvider())){
            storageInfo.setFsType(FileSystemConstant.TENCENT_COS_FileSystem);
        }
        else{
            storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_NAS);
        }
        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
            .changeStorageResource(changeInfo.getDcId(), storageInfo);
        userLog.setDetail(
            UserLogType.STORAGE_MODIFY.getDetails(new String[]{dataCenter.getName(), udsStorageDesc}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        
        return new ResponseEntity(HttpStatus.OK);
    }
}
