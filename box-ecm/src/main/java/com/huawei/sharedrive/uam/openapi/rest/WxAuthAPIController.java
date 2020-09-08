package com.huawei.sharedrive.uam.openapi.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.uam.openapi.domain.RestWxworkAuthCodeResponse;
import com.huawei.sharedrive.uam.weixin.service.WxProviderService;

/* 用于获取企业微信的预授权码和注册码*/
@Controller
@RequestMapping(value = "/api/v2/wxwork/authCode")
public class WxAuthAPIController {
    @Autowired
    private WxProviderService wxProviderService;

    private String templateId = "tpl3f3220e53299307e";

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<RestWxworkAuthCodeResponse> enterpriseRegisterByWxwork() throws IOException {
        RestWxworkAuthCodeResponse res = new RestWxworkAuthCodeResponse();
        //应用授权
        String preauthCode = wxProviderService.getPreAuthCode();
        res.setPreauthCode(preauthCode);

        //注册微信
        String registerCode = wxProviderService.getRegisterCode(templateId);
        res.setRegisterCode(registerCode);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
