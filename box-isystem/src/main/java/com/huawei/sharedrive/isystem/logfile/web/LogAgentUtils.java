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

import java.security.InvalidParameterException;
import java.util.Set;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.huawei.sharedrive.isystem.cluster.FileSystemConstant;
import com.huawei.sharedrive.isystem.cluster.domain.filesystem.UdsStorage;
import com.huawei.sharedrive.isystem.exception.BadRquestException;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 
 * @author s90006125
 * 
 */
@Service("logAgentUtils")
public class LogAgentUtils
{
    private static Logger logger = LoggerFactory.getLogger(LogAgentUtils.class);
    
    @Value("${logagent.s3.default.protocol}")
    private String defaultS3Protocol;
    
    @Value("${logagent.s3.default.http.port}")
    private String defaultS3HttpPort;
    
    @Value("${logagent.s3.default.https.port}")
    private String defaultS3HttpsPort;
    
    @Value("${logagent.s3.default.bucket.prefix}")
    private String defaultS3BucketPrefix;
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String validateAndGetRealEndpoint(Validator validator, String fsType, String endpoint) throws BusinessException
    {
        if (FileSystemConstant.FILE_SYSTEM_UDS.equalsIgnoreCase(fsType))
        {
            String[] temp = StringUtils.trimToEmpty(endpoint).split(FileSystemConstant.SPLIT_FSENDPOINT);
            if (temp.length != 4)
            {
                logger.warn("format of uds endpoint [ " + endpoint + " ] is mistake.");
                throw new BadRquestException();
            }
            
            UdsStorage udsStorage = new UdsStorage();
            udsStorage.setDomain(temp[0]);
            udsStorage.setPort(Integer.parseInt(temp[1]));
            udsStorage.setAccessKey(temp[2]);
            udsStorage.setSecretKey(temp[3]);
            
            Set violations = validator.validate(udsStorage);
            if (!violations.isEmpty())
            {
                throw new ConstraintViolationException(violations);
            }
            if (!FormValidateUtil.isValidPort(udsStorage.getPort()))
            {
                throw new InvalidParameterException("port exception [" + udsStorage.getPort() + "]");
            }
            
            return new StringBuilder(defaultS3Protocol).append(FileSystemConstant.SPLIT_FSENDPOINT)
                .append(udsStorage.getDomain())
                .append(FileSystemConstant.SPLIT_FSENDPOINT)
                .append(udsStorage.getPort())
                .append(FileSystemConstant.SPLIT_FSENDPOINT)
                .append(defaultS3HttpsPort)
                .append(FileSystemConstant.SPLIT_FSENDPOINT)
                .append(udsStorage.getAccessKey())
                .append(FileSystemConstant.SPLIT_FSENDPOINT)
                .append(udsStorage.getSecretKey())
                .append(FileSystemConstant.SPLIT_FSENDPOINT)
                .append(defaultS3BucketPrefix + new RandomGUID().getValueAfterMD5())
                .toString();
        }
        else if (FileSystemConstant.FILE_SYSTEM_NAS.equalsIgnoreCase(fsType))
        {
            if (StringUtils.isBlank(endpoint) || StringUtils.trimToEmpty(endpoint).charAt(0) != '/')
            {
                logger.warn("format of nas endpoint [ " + endpoint + " ] is mistake.");
                throw new BadRquestException();
            }
            if (endpoint.length() > 128)
            {
                logger.warn("format of nas endpoint length[ " + endpoint + " ] is mistake.");
                throw new BadRquestException();
            }
            
            return endpoint;
        }
        else
        {
            logger.warn("FileSystem [ " + fsType + " ] is not supported.");
            throw new BadRquestException();
        }
    }
    
    public void setModelInfo(Model model, String fsType, String endpoint)
    {
        model.addAttribute("fsType", fsType);
        
        if (FileSystemConstant.FILE_SYSTEM_UDS.equalsIgnoreCase(fsType))
        {
            String domain = "";
            String port = defaultS3HttpPort;
            String ak = "";
            String sk = "";
            
            if (null != endpoint && StringUtils.isNotBlank(endpoint))
            {
                String[] temp = StringUtils.trimToEmpty(endpoint).split(FileSystemConstant.SPLIT_FSENDPOINT);
                domain = temp[1];
                port = temp[2];
                ak = temp[4];
            }
            
            model.addAttribute("domain", domain);
            model.addAttribute("port", port);
            model.addAttribute("ak", ak);
            model.addAttribute("sk", sk);
        }
        else if (FileSystemConstant.FILE_SYSTEM_NAS.equalsIgnoreCase(fsType))
        {
            model.addAttribute("path", endpoint);
        }
    }
}
