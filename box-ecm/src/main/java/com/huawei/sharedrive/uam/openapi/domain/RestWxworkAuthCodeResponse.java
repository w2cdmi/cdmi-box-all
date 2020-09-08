package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.terminal.service.impl.TerminalServiceImpl;

public class RestWxworkAuthCodeResponse implements Serializable {
    private String preauthCode;

    private String registerCode;

    public String getPreauthCode() {
        return preauthCode;
    }

    public void setPreauthCode(String preauthCode) {
        this.preauthCode = preauthCode;
    }

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }
}
