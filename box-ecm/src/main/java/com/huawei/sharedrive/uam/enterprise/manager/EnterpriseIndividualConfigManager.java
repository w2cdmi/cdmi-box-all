package com.huawei.sharedrive.uam.enterprise.manager;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;

import pw.cdmi.common.domain.CustomizeLogo;

public interface EnterpriseIndividualConfigManager
{
    void create(WebIconPcLogo webIconPcLogo);
    
    void validPictrueFormat(String fileName, String[] picTypes);
    
    String getFormatName(InputStream inputStream, String originalFileName);
    
    boolean fileToByte(MultipartHttpServletRequest request, WebIconPcLogo webIconPcLogo);
    
    String[] getDescription(MultipartHttpServletRequest request, WebIconPcLogo webIconPcLogo);
    
    WebIconPcLogo get(WebIconPcLogo webIconPcLogo);
    
    int getAccountId(WebIconPcLogo webIconPcLogo);
    
    WebIconPcLogo getWebIconPcLogo(WebIconPcLogo webIconPcLogo);
    
    void getAccountTitle(CustomizeLogo customize, WebIconPcLogo webIconPcLogo);

	void updateCorpright(WebIconPcLogo webIconPcLogo);
    
}
