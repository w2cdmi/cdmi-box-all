/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.permission;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.JsonMapper;
import pw.cdmi.core.interceptor.ExcludeAbleInterceptor;
import pw.cdmi.file.engine.core.web.ExceptionResponseEntity;
import pw.cdmi.file.engine.manage.datacenter.statistics.ConcDownloadStatistician;
import pw.cdmi.file.engine.manage.datacenter.statistics.ConcUploadStatistician;
import pw.cdmi.file.engine.object.rest.support.Method;
import pw.cdmi.file.engine.object.rest.support.MethodType;
import pw.cdmi.file.engine.object.rest.support.RequestConstants;

/**
 * 权限拦截器，主要用来校验token信息
 * 
 * @author s90006125
 * 
 */
public class PermissionInterceptor extends ExcludeAbleInterceptor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionInterceptor.class);
    
    /** 方法类型cache */
    private static final Map<String, Method> METHOD_TYPE_CACHE = new ConcurrentHashMap<String, Method>(1);
    
    private static final String KEY_CONCURRENT = "KEY_CONCURRENT";
    
    private JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
    
    @Override
    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        try
        {
            validatePermission(request, handler);
            
            try
            {
                // 并发计算
                Method method = getMethod(handler);
                concurrentOperBeforeAccess(request, method);
            }
            catch (Exception e)
            {
                LOGGER.error("concurrentOperBeforeAccess failed", e);
            }
        }
        catch (ForbiddenException e)
        {
            request.setAttribute(HttpStatus.class.toString(), HttpStatus.FORBIDDEN);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            ServletOutputStream out = null;
            try
            {
                out = response.getOutputStream();
                String body = jsonMapper.toJson(new ExceptionResponseEntity(LoggerUtil.getCurrentLogID(), e));
                out.print(body);
            }
            catch (IOException ex)
            {
                LOGGER.warn("IOException", ex);
            }
            finally
            {
                IOUtils.closeQuietly(out);
            }
            return false;
        }
        catch (Exception e)
        {
            LOGGER.warn("", e);
            request.setAttribute(HttpStatus.class.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }
        
        return true;
    }
    
    @Override
    public void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex)
    {
        try
        {
            Method method = getMethod(handler);
            concurrentOperAfterAccess(request, method);
        }
        catch (Exception e)
        {
            LOGGER.error("concurrentOperAfterAccess failed", e);
        }
    }
    
    private void validatePermission(HttpServletRequest request, Object handler)
    {
        baseValidate(request, handler);
        
        String token = getToken(request);
        if (StringUtils.isBlank(token))
        {
            LOGGER.warn("Required parameter 'token' is not present");
            throw new ForbiddenException();
        }
        
        String objectID = getObjectID(request);
        
        if (StringUtils.isBlank(objectID))
        {
            LOGGER.warn("Required parameter 'objectID' is not present");
            throw new ForbiddenException();
        }
        
        Method method = getMethod(handler);
        
        String callBackKey = PermissionUtils.validate(method.name(), objectID, token);
        
        if (StringUtils.isBlank(callBackKey))
        {
            LOGGER.warn("Token Check failed. [ objectID: " + objectID + ", method: "
                + method + " ]");
            throw new ForbiddenException();
        }
        
        request.setAttribute(RequestConstants.CALLBACK_KEY, callBackKey);
        
        String requestID = getRequestID();
        
        request.setAttribute(RequestConstants.REQUEST_ID, requestID);
        
        LOGGER.info("Change LogID To [ " + requestID + " ]");
        LoggerUtil.regiestThreadLocalLog(requestID);
    }
    
    private void baseValidate(HttpServletRequest request, Object handler)
    {
        if (handler instanceof DefaultServletHttpRequestHandler)
        {
            String uri = request.getRequestURI().replace(request.getContextPath(), "");
            
            LOGGER.warn("NoSupport Request For URI [ " + uri + " ]");
            throw new ForbiddenException();
        }
    }
    
    @SuppressWarnings("rawtypes")
    private String getToken(HttpServletRequest request)
    {
        Object vars = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (null == vars)
        {
            return null;
        }
        
        if (!(vars instanceof LinkedHashMap))
        {
            return null;
        }
        
        Object objectID = ((LinkedHashMap) vars).get(RequestConstants.REQUEST_TOKEN);
        
        if (null == objectID)
        {
            return null;
        }
        
        return (String) objectID;
    }
    
    @SuppressWarnings("rawtypes")
    private String getObjectID(HttpServletRequest request)
    {
        Object vars = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (null == vars)
        {
            return null;
        }
        
        if (!(vars instanceof LinkedHashMap))
        {
            return null;
        }
        
        Object objectID = ((LinkedHashMap) vars).get(RequestConstants.OBJECT_ID);
        
        if (null == objectID)
        {
            return null;
        }
        
        return (String) objectID;
    }
    
    private Method getMethod(Object handler)
    {
        if (!(handler instanceof HandlerMethod))
        {
            LOGGER.warn("No Such Method.");
            throw new ForbiddenException();
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String methodKey = handlerMethod.getMethod().toString();
        // 如果cache中存在，则直接返回cache中的值
        if (METHOD_TYPE_CACHE.containsKey(methodKey))
        {
            return METHOD_TYPE_CACHE.get(methodKey);
        }
        
        String type = null;
        MethodType methodType = handlerMethod.getMethodAnnotation(MethodType.class);
        if (null != methodType)
        {
            type = methodType.value().toString();
        }
        
        Method method = Method.parse(type);
        if (null == method)
        {
            LOGGER.warn("NoSupport Method Exception [ {} ].", type);
            throw new ForbiddenException();
        }
        
        METHOD_TYPE_CACHE.put(methodKey, method);
        
        return method;
    }
    
    private String getRequestID()
    {
        return LoggerUtil.getCurrentLogID();
    }
    
    private void concurrentOperBeforeAccess(HttpServletRequest request, Method method)
    {
        switch (method)
        {
            case GET_OBJECT:
            case GET_THUMBNAIL:
            case GET_PREVIEW:
                ConcDownloadStatistician.startDownload();
                break;
            case PUT_OBJECT:
            case PUT_PART:
            case POST_OBJECT:
            case POST_PART:
                ConcUploadStatistician.startUpload();
                break;
            default:
                break;
        }
        
        request.setAttribute(KEY_CONCURRENT, new Object());
    }
    
    private void concurrentOperAfterAccess(HttpServletRequest request, Method method)
    {
        Object obj = request.getAttribute(KEY_CONCURRENT);
        if (null == obj)
        {
            // 只有访问进来时，计算成功的，才做后面的操作
            LOGGER.warn("don't neet cut concurrent.");
            return;
        }
        switch (method)
        {
            case GET_OBJECT:
            case GET_THUMBNAIL:
            case GET_PREVIEW:
                ConcDownloadStatistician.endDownload();
                break;
            case PUT_OBJECT:
            case PUT_PART:
            case POST_OBJECT:
            case POST_PART:
                ConcUploadStatistician.endUpload();
                break;
            default:
                break;
        }
    }
}
