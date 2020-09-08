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
package com.huawei.sharedrive.app.openapi.rest;

import com.huawei.sharedrive.app.core.web.JsonMapper;
import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ExceptionResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pw.cdmi.common.log.LoggerUtil;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * @author s90006125
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private JsonMapper jsonMapper;

    public HttpStatus getHttpStatus(HttpServletRequest request, Exception exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (exception instanceof BaseRunException) {
            status = ((BaseRunException) exception).getHttpcode();
        } else if (exception instanceof MissingServletRequestParameterException) {
            status = HttpStatus.BAD_REQUEST;
        }
        request.setAttribute(HttpStatus.class.toString(), status);

        return status;
    }

    /**
     * 业务异常
     *
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(BaseRunException.class)
    public ResponseEntity<Object> handleBusinessException(BaseRunException exception, HttpServletRequest request) {
        LOGGER.warn("BaseRunException", exception);

        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }

    /**
     * 参数缺失异常
     *
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleBusinessException(MissingServletRequestParameterException exception, HttpServletRequest request) {
        LOGGER.warn("MissingServletRequestParameterException", exception);

        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }

    /**
     * 默认的异常
     *
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefaultException(Exception exception, HttpServletRequest request) {
        LOGGER.warn("Exception Occurred, url={}", request.getRequestURL());
        LOGGER.warn("Exception", exception);

        if (exception instanceof IllegalArgumentException) {
            BadRequestException bad = new BadRequestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        } else {
            ExceptionResponseEntity entry = new ExceptionResponseEntity(getRequestID(request));

            entry.setMessage("Sorry, Server Exception!");
            String body = jsonMapper.toJson(entry);
            return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
        }
    }

    @PostConstruct
    public void init() {
        jsonMapper = JsonMapper.nonEmptyMapper();
    }

    /**
     * 获取请求ID
     *
     * @return
     */
    protected String getRequestID(HttpServletRequest request) {
        return LoggerUtil.getCurrentLogID();
    }
}
