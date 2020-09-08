/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.core.JsonMapper;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.exception.errorcode.ErrorCode;
import pw.cdmi.file.engine.core.spring.ext.BeanHolder;
import pw.cdmi.file.engine.core.web.CommonResponseEntiy;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.domain.MultipartPart;
import pw.cdmi.file.engine.object.exception.BadRequestException;
import pw.cdmi.file.engine.object.exception.InvalidParameterException;
import pw.cdmi.file.engine.object.rest.support.BaseFileObjectRestInterface;
import pw.cdmi.file.engine.object.rest.support.Method;
import pw.cdmi.file.engine.object.rest.support.MethodType;
import pw.cdmi.file.engine.object.rest.support.RequestConstants;

/**
 * 分片规格<br>
 * 单个文件大小限制：5GB<br>
 * 多段单个文件限制：5TB<br>
 * 多段上传对象段号：【1 10000】<br>
 * 多段每一个段对象大小限制：【5MB 5GB】<br>
 * 最后一个段对象大小限制 ：【0 5GB】同一个文件的每片可以不一样<br>
 * 多段的段号范围为什么是10000：这个是当时做特性时拍的，主要是为了和亚马逊保持一致。<br>
 * 
 * @author s90006125
 * 
 */

@Controller
public class MultipartFileObjectRestInterface extends BaseFileObjectRestInterface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartFileObjectRestInterface.class);
    
    private JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
    
    private static final int MIN_PARTID = 1;
    
    private static final String LINE_END = System.lineSeparator();
    
    /**
     * 只执行初始化分片
     * 
     * @param objectID
     * @param request
     * @param response
     */
    @MethodType(Method.PUT_PART)
    @RequestMapping(params = RequestConstants.REQUEST_OBJECT_PARTS_START, method = RequestMethod.PUT)
    public @ResponseBody
    CommonResponseEntiy initMultipartUpload(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            multipartFileObjectManager.initMultipartUpload(objectID, getCallBackKey(request));
        }
        catch (FSException e)
        {
            
            String message = "Init Multipart For [ " + objectID + " ] Failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        
        response.setHeader("Connection", "close");
        return new CommonResponseEntiy(getRequestID(request));
    }
    
    /**
     * 上传文件分片 <br>
     * 该接口已经废弃，接口文档中，partID已经修改成partId了，为了兼容老版本客户端，才保留该接口
     * 
     * @param objectID
     * @param request
     * @param response
     * @throws FSException 
     */
    @Deprecated
    @MethodType(Method.PUT_PART)
    @RequestMapping(params = RequestConstants.REQUEST_OBJECT_PART_ID, method = RequestMethod.PUT)
    public @ResponseBody
    CommonResponseEntiy putFileObjectPartOld(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @RequestParam(value = RequestConstants.REQUEST_OBJECT_PART_ID, required = true) int partID,
        InputStream inputStream,
        HttpServletRequest request,
        HttpServletResponse response) throws FSException
    {
        return putObjectPart(objectID, partID, inputStream, request, response);
    }
    
    /**
     * 上传文件分片
     * 
     * @param objectID
     * @param request
     * @param response
     * @throws FSException 
     */
    @MethodType(Method.PUT_PART)
    @RequestMapping(params = "partId", method = RequestMethod.PUT)
    public @ResponseBody
    CommonResponseEntiy putFileObjectPart(@PathVariable(RequestConstants.OBJECT_ID) String objectId,
        @RequestParam(value = "partId", required = true) int partId,
        InputStream inputStream,
        HttpServletRequest request, HttpServletResponse response) throws FSException
    {
        return putObjectPart(objectId, partId, inputStream, request, response);
    }
    
    @SuppressWarnings("boxing")
    private CommonResponseEntiy putObjectPart(String objectId, int partId,
        InputStream inputStream, HttpServletRequest request, HttpServletResponse response) throws FSException
    {
        try
        {
            if (partId < MIN_PARTID
                || partId > SystemConfigContainer.getInteger("fs.storage.multipart.maxpartid", 10000))
            {
                String message = "Required parameter 'partID' is incorrect. partID: " + partId;
                LOGGER.warn(message);
                throw new BadRequestException(message);
            }
            
           // add by shenqing 2016-5-23 begin
            String objLengthStr = request.getHeader(RequestConstants.REQUEST_CONTENT_LENGTH);
            long objLength = -1;
            if (StringUtils.isNotBlank(objLengthStr))
            {
                try
                {
                    objLength = Long.parseLong(objLengthStr);
                }
                catch (NumberFormatException e)
                {
                    throw new InvalidParameterException("empty Content-Length: " + objLengthStr, e);
                }
            }
            
            LOGGER.info("fileobject length : " + objLengthStr);
            // add by shenqing 2016-5-23 end
            
            multipartFileObjectManager.createMultipartPart(objectId,
                objLength,
                getCallBackKey(request),
                partId,
                inputStream);
            response.setHeader("Connection", "close");
            return new CommonResponseEntiy(getRequestID(request));
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    /**
     * 执行完成分片操作
     * 
     * @param objectID
     * @param request
     * @param response
     */
    @MethodType(Method.PUT_COMMIT)
    @RequestMapping(params = RequestConstants.REQUEST_OBJECT_UPLOAD_COMMIT, method = RequestMethod.PUT)
    public @ResponseBody
    CommonResponseEntiy completeMultipartUpload(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @PathVariable(value = RequestConstants.REQUEST_TOKEN) String token, InputStream inputStream,
        HttpServletRequest request)
    {
        // 执行提交分片操作
        TreeSet<MultipartPart> parts = parseParts(inputStream);
        
        if (null == parts)
        {
            String message = "Required parameter 'partList' is not present";
            LOGGER.warn(message);
            throw new BadRequestException(ErrorCode.MissingParameterException.getCode(), message);
        }
        if(LOGGER.isDebugEnabled())
        {
            for (MultipartPart p : parts)
            {
                LOGGER.debug(ReflectionToStringBuilder.toString(p));
            }
        }
        multipartFileObjectManager.completeMultipartUpload(objectID, parts);
        return new CommonResponseEntiy(getRequestID(request));
    }
    
    /**
     * 列举分片
     * 
     * @param objectID
     * @param request
     * @param response
     */
    @MethodType(Method.GET_PARTS)
    @RequestMapping(method = RequestMethod.GET)
    public void listMultipartParts(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        OutputStream outputStream, HttpServletResponse response)
    {
        MultipartFileObject partFileObject = multipartFileObjectManager.listMultipartParts(objectID);
        
        try
        {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/plain");
            
            // 判断是否需要兼容老版本，老版本的partId写成了partID
            if (needCompatible())
            {
                CompatibleMultipartFileObject object = new CompatibleMultipartFileObject(
                    partFileObject.getParts());
                
                jsonMapper.toJson(outputStream, object);
            }
            else
            {
                jsonMapper.toJson(outputStream, partFileObject);
            }
        }
        catch (IOException e)
        {
            String message = "Convert To Json Faile [ " + objectID + " ]";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
    
    /**
     * 取消分片
     * 
     * @param objectID
     * @param outputStream
     * @param response
     */
    @MethodType(Method.DELETE_PART)
    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseBody
    CommonResponseEntiy abortMultipartUpload(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @PathVariable(value = RequestConstants.REQUEST_TOKEN) String token, OutputStream outputStream,
        HttpServletResponse response, HttpServletRequest request)
    {
        
        multipartFileObjectManager.abortMultipartUpload(objectID);
        return new CommonResponseEntiy(getRequestID(request));
    }
    
    @SuppressWarnings("PMD.LooseCoupling")
    private TreeSet<MultipartPart> parseParts(InputStream inputStream)
    {
        try
        {
            if (needCompatible())
            {
                String json = null;
                try
                {
                    json = convertToString(inputStream);
                }
                catch (IOException e)
                {
                    LOGGER.warn("read json failed.", e);
                    return null;
                }
                
                if (StringUtils.isBlank(json))
                {
                    LOGGER.warn("json is null.");
                    return null;
                }
                
                json = json.replaceAll("partID", "partId");
                
                MultipartFileObject object = jsonMapper.fromJson(json, MultipartFileObject.class);
                if (null == object)
                {
                    return null;
                }
                
                return object.getParts();
            }
            else
            {
                MultipartFileObject object = jsonMapper.fromJson(inputStream, MultipartFileObject.class);
                if (null == object)
                {
                    return null;
                }
                
                return object.getParts();
            }
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private String convertToString(InputStream is) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        
        LineIterator iterator = new LineIterator(reader);
        while (iterator.hasNext())
        {
            sb.append(iterator.next() + LINE_END);
        }
        return sb.toString();
    }
    
    private boolean needCompatible()
    {
        String needCompatible = BeanHolder.getMessage("compatible.interface", "false");
        return Boolean.parseBoolean(needCompatible);
    }
}
