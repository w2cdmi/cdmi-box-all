/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.object.rest.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import pw.cdmi.file.engine.core.exception.errorcode.ErrorCode;
import pw.cdmi.file.engine.object.exception.BadRequestException;
import pw.cdmi.file.engine.object.manager.FileObjectAttachmentManager;
import pw.cdmi.file.engine.object.manager.FileObjectManager;
import pw.cdmi.file.engine.object.manager.MultipartFileObjectManager;
import pw.cdmi.file.engine.object.manager.Range;

/**
 * 
 * @author s90006125
 * 
 */

@Controller
@RequestMapping("/api/{" + RequestConstants.REQUEST_TOKEN + "}/{" + RequestConstants.OBJECT_ID + '}')
public abstract class BaseFileObjectRestInterface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFileObjectRestInterface.class);
    
    private static final String RANGE_PATTERN = "^bytes=([0-9]*)-([0-9]*)$";
    
    @Autowired
    protected FileObjectManager fileObjectManager;
    
    @Autowired
    protected MultipartFileObjectManager multipartFileObjectManager;
    
    @Autowired
    protected FileObjectAttachmentManager fileObjectAttachmentManager;
    
    /**
     * 获取回调的KEY
     * 
     * @param request
     * @return
     */
    protected String getCallBackKey(HttpServletRequest request)
    {
        return (String) request.getAttribute(RequestConstants.CALLBACK_KEY);
    }
    
    /**
     * 获取请求ID
     * 
     * @return
     */
    protected String getRequestID(HttpServletRequest request)
    {
        return (String) request.getAttribute(RequestConstants.REQUEST_ID);
    }
    
    /**
     * 
     * @param range
     * @return 这个数据区间是个闭合区间，起始值是 0，所以“Range: bytes=0-1”这样一个请求实际上是在请求开头的 2 个字节。<br>
     *         表示头500个字节：bytes=0-499<br>
     *         表示第二个500字节：bytes=500-999<br>
     *         表示最后500个字节：bytes=-500<br>
     *         表示500字节以后的范围：bytes=500-<br>
     *         第一个和最后一个字节：bytes=0-0,-1<br>
     *         同时指定几个范围：bytes=500-600,601-999
     */
    protected Range parseRange(String range)
    {
        LOGGER.info("Request Range Is [ " + range + " ] ");
        
        if (StringUtils.isBlank(range))
        {
            return null;
        }
        
        range = Normalizer.normalize(StringUtils.lowerCase(StringUtils.trimToEmpty(range)), Form.NFKC);
        
        if (range.contains("%"))
        {
            try
            {
                range = URLDecoder.decode(range, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                String message = "Required parameter 'Range' is incorrect.";
                LOGGER.warn(message);
                throw new BadRequestException(ErrorCode.BadRequestException.getCode(), message, e);
            }
        }
        
        Pattern rangePattern = Pattern.compile(RANGE_PATTERN);
        Matcher match = rangePattern.matcher(range);
        if (!match.matches())
        {
            String message = "Required parameter 'Range' is incorrect.";
            LOGGER.warn(message);
            throw new BadRequestException(ErrorCode.BadRequestException.getCode(), message);
        }
        
        Long start = StringUtils.isBlank(match.group(1)) ? null : Long.valueOf(match.group(1));
        Long end = StringUtils.isBlank(match.group(2)) ? null : Long.valueOf(match.group(2));
        
        if (!validateRange(start, end))
        {
            String message = "Required parameter 'Range' is incorrect.";
            throw new BadRequestException(ErrorCode.BadRequestException.getCode(), message);
        }
        
        return new Range(start, end);
    }
    
    private boolean validateRange(Long start, Long end)
    {
        if (null != start && null != end && end < start)
        {
            return false;
        }
        
        if (null == start && null == end)
        {
            return false;
        }
        
        if ((null != start && start < 0) || (null != end && end < 0))
        {
            return false;
        }
        
        return true;
    }
}
