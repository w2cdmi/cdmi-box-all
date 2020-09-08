/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.rest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.JsonMapper;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.spring.ext.BeanHolder;
import pw.cdmi.file.engine.core.util.HttpSafeHeader;
import pw.cdmi.file.engine.core.web.CommonResponseEntiy;
import pw.cdmi.file.engine.core.web.upload.NoTempFileItemFactory;
import pw.cdmi.file.engine.core.web.upload.NoTempFileUpload;
import pw.cdmi.file.engine.core.web.upload.PostData;
import pw.cdmi.file.engine.object.domain.ThumbnailFileObject;
import pw.cdmi.file.engine.object.exception.InvalidParameterException;
import pw.cdmi.file.engine.object.manager.DownFileObjectContent;
import pw.cdmi.file.engine.object.manager.Range;
import pw.cdmi.file.engine.object.rest.support.BaseFileObjectRestInterface;
import pw.cdmi.file.engine.object.rest.support.Method;
import pw.cdmi.file.engine.object.rest.support.MethodType;
import pw.cdmi.file.engine.object.rest.support.RequestConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author s90006125
 * 
 */

@Controller
public class FileObjectRestInterface extends BaseFileObjectRestInterface
{  
    private static Logger logger = LoggerFactory.getLogger(FileObjectRestInterface.class);
    
    private static final int MAX_ORIGIN_LENGTH = 1024;
    
    private static final String START_ORIGIN_HTTP = "http://";
    
    private static final String START_ORIGIN_HTTPS = "https://";
    
    @Autowired(required=false)
    @Qualifier("cacheClient")
    private CacheClient cacheClient;
    
    public static String specialCharDecode(String str)
    {
        String result = str.replace("+", "%20")
            .replace("%21", "!")
            .replace("%24", "$")
            .replace("%20", " ")
            .replace("%25", "%")
            .replace("%28", "(")
            .replace("%29", ")")
            .replace("%2B", "+")
            .replace("%2C", ",")
            .replace("%3A", ":")
            .replace("%3D", "=")
            .replace("%40", "@")
            .replace("%5B", "[")
            .replace("%5D", "]")
            .replace("%5E", "^")
            .replace("%60", "`")
            .replace("%7B", "{")
            .replace("%7D", "}")
            .replace("%27", "'")
            .replace("%26", "&");
        return result;
    }
    
    public static String specialCharDecodeChrome(String str)
    {
        String result = str.replace("+", "%20")
            .replace("%21", "!")
            .replace("%23", "#")
            .replace("%24", "$")
            .replace("%25", "%")
            .replace("%28", "(")
            .replace("%29", ")")
            .replace("%2B", "+")
            .replace("%2C", ",")
            .replace("%3A", ":")
            .replace("%3D", "=")
            .replace("%40", "@")
            .replace("%5B", "[")
            .replace("%5D", "]")
            .replace("%5E", "^")
            .replace("%60", "`")
            .replace("%7B", "{")
            .replace("%7D", "}")
            .replace("%27", "'")
            .replace("%26", "&");
        return result;
    }
    
    private JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
    
    public String decodeDownloadFileName(String showName, HttpServletRequest request)
    {
        try
        {
            showName = URLDecoder.decode(showName, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.warn("Decode ShowName Failed.", e);
        }
        
        return showName;
    }
    
    /**
     * 下载文件
     * 
     * @param objectID
     * @param showName
     * @param request
     * @param response
     * @throws IOException 
     */
    @MethodType(Method.GET_OBJECT)
    @RequestMapping(value = "/{" + RequestConstants.REQUEST_OBJECT_SHOW_NAME + ":.*}", method = RequestMethod.POST)
    public void downLoadFileObjectByPost(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @PathVariable(RequestConstants.REQUEST_OBJECT_SHOW_NAME) String showName,
        @RequestParam(value = "mime", required = false) String contentType, 
        HttpServletRequest request,
        HttpServletResponse response) throws IOException
    {
        downLoadFileObject(objectID, showName, contentType, request, response);
    }
    
    /**
     * 下载文件
     * 
     * @param objectID
     * @param showName
     * @param request
     * @param response
     */
    @MethodType(Method.GET_OBJECT)
    @RequestMapping(value = "/{" + RequestConstants.REQUEST_OBJECT_SHOW_NAME + ":.*}", method = RequestMethod.GET)
    public void downLoadFileObject(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @PathVariable(RequestConstants.REQUEST_OBJECT_SHOW_NAME) String showName,
        @RequestParam(value = "mime", required = false) String contentType,
        HttpServletRequest request,
        HttpServletResponse response)
    {
        // 经过nginx代理后请求参数会自动被转码，根据Http协议参数如果带有分号后的参数会被自动截断，通过spring
        // api会自动造成文件名被截断，因此需要特殊处理
        
        String reqUri = request.getRequestURI();
        String fileName = reqUri.substring(reqUri.lastIndexOf("/") + 1);
        
        if (!fileName.equals(encodeDownloadFileName(showName, request)))
        {
            showName = decodeDownloadFileName(fileName, request);
        }
        logger.error("======= contentType : " + contentType);
		// 原有方式
        String range = request.getHeader(RequestConstants.REQUEST_OBJECT_RANGE);
//        logger.error("======= downLoadFileObject 分片下载==========");
        logger.error("======= 1 、header range : " + request.getHeader(RequestConstants.REQUEST_OBJECT_RANGE));

        Range rangeObj = parseRange(range);
        
        DownFileObjectContent downFileObjectContent = fileObjectManager.downloadFileObject(objectID, rangeObj);
        
        try
        {
            downLoad(downFileObjectContent, showName,request, response, contentType);
        }
        catch (IOException e)
        {
            String message = "Down Object [" + objectID + " ] Failed.";
            logger.warn(message, e);
            throw new InnerException(message, e);
        }
    }
    
    /**
     * 获取预览图片
     * 
     * @param objectID
     * @param showName
     * @param request
     * @param response
     * @throws IOException 
     * @throws InnerException 
     * @throws Exception
     */
    @MethodType(Method.GET_THUMBNAIL)
    @RequestMapping(value = "/{" + RequestConstants.REQUEST_OBJECT_SHOW_NAME + ":.*}/thumbnail", method = RequestMethod.POST)
    public void downLoadThumbnailByPost(
        @PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @PathVariable(RequestConstants.REQUEST_OBJECT_SHOW_NAME) String showName,
        HttpServletRequest request, HttpServletResponse response) throws InnerException, IOException
    {
        downLoadThumbnail(objectID, showName,request, response);
    }
    
    /**
     * 获取预览图片
     * 
     * @param objectID
     * @param showName
     * @param request
     * @param response
     * @throws IOException 
     * @throws Exception
     */
    @MethodType(Method.GET_THUMBNAIL)
    @RequestMapping(value = "/{" + RequestConstants.REQUEST_OBJECT_SHOW_NAME + ":.*}/thumbnail", method = RequestMethod.GET)
    public void downLoadThumbnail(
        @PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @PathVariable(RequestConstants.REQUEST_OBJECT_SHOW_NAME) String showName,
        HttpServletRequest request, HttpServletResponse response) throws IOException, InnerException
    {
        ThumbnailBean thumbnaliBean = ThumbnailBean.getBean(request);
        ThumbnailFileObject attachement = new ThumbnailFileObject(objectID, thumbnaliBean.getHeight(), thumbnaliBean.getWidth());
        if(!fileObjectAttachmentManager.validateWidthAndHeigth(attachement))
        {
            throw new InvalidParameterException();
        }
        DownFileObjectContent downFileObjectContent;
        try
        {
            downFileObjectContent = fileObjectAttachmentManager.getAttachment(attachement);
        }
        catch (Exception e)
        {
            logger.error("Cann't Get Thumbnail.", e);
            if (thumbnaliBean.isNeedDefault())
            {
                downFileObjectContent = fileObjectAttachmentManager.getDefaultThumbnail(attachement);
            }
            else
            {
                throw new InnerException("", e);
            }
        }
        try
        {
            downLoad(downFileObjectContent, showName, request, response, null);
        }
        catch (IOException e)
        {
            String message = "Down Thumbnail For [" + objectID + " ] Failed.";
            logger.warn(message, e);
            throw new InnerException(message, e);
        }
    }
    
    public String encodeDownloadFileName(String showName, HttpServletRequest request)
    {
        try
        {
            showName = URLEncoder.encode(showName, "UTF-8").replaceAll("\\+", "%20");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.warn("Encode ShowName Failed.", e);
        }
        
        return showName;
    }
    
    /**
     * POST方式上传文件
     * 
     * @param objectID
     * @param request
     * @param response
     */
    @MethodType(Method.POST_OBJECT)
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    CommonResponseEntiy postFileObject(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @RequestParam(value = RequestConstants.REQUEST_OBJECT_LENGTH, required = true) long objectLength,
        @PathVariable(value = RequestConstants.REQUEST_TOKEN) String token, HttpServletRequest request,
        HttpServletResponse response)
    {
        NoTempFileItemFactory factory = new NoTempFileItemFactory();
        NoTempFileUpload upload = new NoTempFileUpload(factory);
        List<FileItem> items = null;
        try
        {
            items = upload.parseRequest(request);
        }
        catch (FileUploadException e)
        {
            String message = "Parse Post Body Failed. [" + objectID + " ] ";
            logger.warn(message, e);
            throw new InnerException(message, e);
        }
        
        PostData postData = new PostData(items);
        
        FileItem fi = postData.getFile();
        
        InputStream inputStream = null;
        try
        {
            inputStream = fi.getInputStream();
            fileObjectManager.createFileObject(objectID, fi.getName(), objectLength, inputStream, getCallBackKey(request));
            setOrigin(request, response);
            response.setHeader("Connection", "close");
            return new CommonResponseEntiy(getRequestID(request));
        }
        catch (IOException e)
        {
            String message = "Get Post InputStream Failed. [" + objectID + " ] ";
            logger.warn(message, e);
            throw new InnerException(message, e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
        
    }
    
    /**
     * PUT文件上传
     * 
     * @param objectID
     * @param request
     * @param response
     */
    @MethodType(Method.PUT_OBJECT)
    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody
    CommonResponseEntiy uploadFileObject(@PathVariable(RequestConstants.OBJECT_ID) String objectID,
        @PathVariable(value = RequestConstants.REQUEST_TOKEN) String token,
        InputStream inputStream,
        HttpServletRequest request,
        HttpServletResponse response)
    {
        try
        {
            String objLengthStr = request.getHeader(RequestConstants.REQUEST_CONTENT_LENGTH);
            if (StringUtils.isEmpty(objLengthStr)) {
                throw new InvalidParameterException("empty Content-Length");
            }
            long objLength = 0;
            try {
                objLength = Long.parseLong(objLengthStr);
            } catch (NumberFormatException e) {
                throw new InvalidParameterException("empty Content-Length: " + objLengthStr, e);
            }

            // 执行上传文件操作
            fileObjectManager.createFileObject(objectID, null, objLength, inputStream, getCallBackKey(request));
            
            response.setHeader("Connection", "close");
            return new CommonResponseEntiy(getRequestID(request));
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
        
    }
    
    /**
     * 
     * @param objectID
     * @param request
     * @param response
     */
    @MethodType(Method.OPTION_OBJECT)
    @RequestMapping(method = RequestMethod.OPTIONS)
    public @ResponseBody
    ResponseEntity<?> optionFileObject(HttpServletRequest request,
        HttpServletResponse response)
    {
        response.setHeader("Connection", "close");
        setOrigin(request, response);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void setOrigin(HttpServletRequest request, HttpServletResponse response)
    {
        String origin = request.getHeader("Origin");
        
        // FORTIFY.Header_Manipulation
        origin = HttpSafeHeader.toSafeValue(origin);
        try
        {
            origin = StringUtils.isBlank(origin) ? "" : URLDecoder.decode(origin, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.warn("decode failed [ " + origin + " ]", e);
            
            origin = "";
        }
        
        if (origin.length() > MAX_ORIGIN_LENGTH)
        {
            origin = "";
        }
        
        String temp = StringUtils.lowerCase(origin);
        
        if (!temp.startsWith(START_ORIGIN_HTTP) && !temp.startsWith(START_ORIGIN_HTTPS))
        {
            origin = "";
        }
        
        if (StringUtils.isNotBlank(origin))
        {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        
        response.setHeader("Allow", BeanHolder.getMessage("access.allow.methods", "PUT"));
    }
    
    @SuppressWarnings("unchecked")
    private void buildDownLoadResponse(DownFileObjectContent downFileObjectContent, String showName,
        HttpServletRequest request, HttpServletResponse response, String contentType) throws UnsupportedEncodingException {
        response.setCharacterEncoding("utf-8");
        if (StringUtils.isBlank(contentType)) {
            response.setContentType("application/octet-stream; charset=utf-8");
        } else {
            response.setContentType(contentType);
        }
        String agent = request.getHeader("User-Agent");
        
        // FORTIFY.Header_Manipulation
        showName = HttpSafeHeader.toSafeValue(showName);
        String encodeName = getAttachName(showName, agent);
        String header = "attachment; filename=\"" + encodeName + "\"";
        response.setHeader("Content-Disposition", header);
        response.setHeader("Connection", "close");
        response.setHeader(RequestConstants.REQUEST_CONTENT_LENGTH, String.valueOf(downFileObjectContent.getContentLength()));
        
        Map<String, String> resultMap = (Map<String, String>) jsonMapper.fromJson(getCallBackKey(request), Map.class);
        
        // 如果回调结果不是json map, 或获取为空，则表示对接的老的ufm，不设置Last-Modified
        if (resultMap != null && StringUtils.isNotBlank(resultMap.get(RequestConstants.CALLBACK_LAST_MODIFIED))) {
            response.setHeader("Last-Modified", resultMap.get(RequestConstants.CALLBACK_LAST_MODIFIED));
        }
        
        //如果客户端用了分片上下载
        String range = request.getHeader(RequestConstants.REQUEST_OBJECT_RANGE);
        //ios分片下载
        if (range != null) {
        	if("bytes=0-".equals(range)){
        		response.setHeader("Content-Range", "bytes 0-1/" + downFileObjectContent.getFileObject().getObjectLength());
        		logger.debug("Content-Range", "bytes 0-1/" + downFileObjectContent.getFileObject().getObjectLength());
        	}else{
        		Range rangeObj = parseRange(range);
        		response.setHeader("Content-Range", "bytes " + rangeObj.getStart() + "-" + rangeObj.getEnd() + "/" + downFileObjectContent.getFileObject().getObjectLength());
        		logger.debug("Content-Range", "bytes " + rangeObj.getStart() + "-" + rangeObj.getEnd() + "/" + downFileObjectContent.getFileObject().getObjectLength());
        	}
        	response.setHeader("Accept-Ranges", "bytes");
        	response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
  		}
    }
    
    /**
     * 下载文件，写数据流
     * 
     * @param fileObject
     * @param showName
     * @param outputStream
     * @param response
     * @throws IOException
     */
    private void downLoad(DownFileObjectContent downFileObjectContent, String showName,
        HttpServletRequest request, HttpServletResponse response,
        String contentType) throws IOException
    {
        buildDownLoadResponse(downFileObjectContent, showName, request, response, contentType);
        OutputStream out = response.getOutputStream();
        InputStream is = null;
        int count = 0;
        try
        {
            if (0L == downFileObjectContent.getContentLength())
            {
            	response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
                return;
            }
            
            if (null != downFileObjectContent.getFileObject().getData()
                && downFileObjectContent.getFileObject().getData().length != 0)
            {
                out.write(downFileObjectContent.getFileObject().getData());
                out.flush();
            }
            else
            {
                is = downFileObjectContent.getFileObject().getInputStream();
                byte[] b = new byte[1024*50];
                int len = 0;
                while((len = is.read(b))!=-1){
                  out.write(b, 0, len);
                  count = count + len;
                }
                out.flush();
                out.close();
                is.close();
                response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            }
            logger.debug("=======  Write success " + count + " bytes.");
        }catch(IOException e){
        	logger.debug("=======  Write error " + count + " bytes.");
        }
        finally
        {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(is);
        }
    }

    private String getAttachName(String fileName, String agent) throws UnsupportedEncodingException {
        String attachName = URLEncoder.encode(fileName, "UTF-8");
        // 针对下载显示的文件名中不会被转码的特殊字符进行专门处理
        String lowerAgent = StringUtils.lowerCase(agent);
        if (StringUtils.isBlank(lowerAgent)) {
            return attachName;
        }

        if (lowerAgent.contains("firefox")) {
            attachName = new String(fileName.getBytes("utf-8"), "iso-8859-1");
        } else if (lowerAgent.contains("chrome")) {
            //chrome浏览器在windows和mac上, 编码后都能正常工作
            attachName = specialCharDecodeChrome(URLEncoder.encode(fileName, "UTF-8"));
        } else if (lowerAgent.contains("macintosh")) {
            //如果mac操作系统(不是chrome和firefox)
            return new String(fileName.getBytes("utf-8"), "iso-8859-1");
        } else {
            attachName = specialCharDecode(URLEncoder.encode(fileName, "UTF-8"));
        }

        return attachName;
    }
}
