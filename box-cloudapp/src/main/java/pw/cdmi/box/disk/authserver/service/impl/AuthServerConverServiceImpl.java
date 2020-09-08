package pw.cdmi.box.disk.authserver.service.impl;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.authserver.service.AuthServerConverService;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapFiledMapping;
import pw.cdmi.common.domain.enterprise.ldap.LdapNodeFilterConfig;

@Service
public class AuthServerConverServiceImpl implements AuthServerConverService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerConverServiceImpl.class);
    
    public static final String TRANS_BASIC = "basic";
    
    public static final String TRANS_SEARCH = "search";
    
    public static final String TRANS_NODE = "node";
    
    @Override
    public LdapDomainConfig convertedToObject(AuthServer authConfig)
    {
        String ldapBasicXml = authConfig.getLdapBasicXml();
        String filedMappingXml = authConfig.getFiledMappingXml();
        String ldapNodesFilterXml = authConfig.getLdapNodesFilterXml();
        LdapDomainConfig ldapDomainConfig = new LdapDomainConfig();
        
        try
        {
            if (StringUtils.isNotBlank(ldapBasicXml))
            {
                Document document = DocumentHelper.parseText(ldapBasicXml);
                Element root = document.getRootElement();
                ldapDomainConfig.setLdapBasicConfig(LdapBasicConfig.bulidLdapBasicConfig(root));
            }
            if (StringUtils.isNotBlank(filedMappingXml))
            {
                Document document = DocumentHelper.parseText(filedMappingXml);
                Element root = document.getRootElement();
                ldapDomainConfig.setLdapFiledMapping(LdapFiledMapping.bulidLdapFiledMapping(root));
            }
            if (StringUtils.isNotBlank(ldapNodesFilterXml))
            {
                Document document = DocumentHelper.parseText(ldapNodesFilterXml);
                Element root = document.getRootElement();
                ldapDomainConfig.setLdapNodeFilterConfig(LdapNodeFilterConfig.bulidLdapNodeFilterConfig(root));
            }
        }
        catch (org.dom4j.DocumentException e)
        {
            LOGGER.error("parse config xml failed", e);
        }
        return ldapDomainConfig;
    }
    
    @Override
    public String convertedToAuthConfig(LdapDomainConfig ldapDomainConfig)
    {
        String configXml = "";
        if (null != ldapDomainConfig.getLdapBasicConfig())
        {
            return LdapBasicConfig.bulidLdapBasicXml(ldapDomainConfig.getLdapBasicConfig());
        }
        if (null != ldapDomainConfig.getLdapFiledMapping())
        {
            return LdapFiledMapping.bulidLdapBasicXml(ldapDomainConfig.getLdapFiledMapping());
        }
        if (null != ldapDomainConfig.getLdapNodeFilterConfig())
        {
            return LdapNodeFilterConfig.bulidLdapBasicXml(ldapDomainConfig.getLdapNodeFilterConfig());
        }
        return configXml;
    }
    
    @Override
    public LdapBasicConfig transLdapObject(LdapBasicConfig ldapBasicConfig,
        LdapDomainConfig ldapDomainConfig, String type)
    {
        
        LdapBasicConfig selLdapBasicConfig = ldapDomainConfig.getLdapBasicConfig();
        if (null == selLdapBasicConfig)
        {
            return ldapBasicConfig;
        }
        if (null != ldapBasicConfig)
        {
            if (type.equals(TRANS_BASIC))
            {
                selLdapBasicConfig.setDomainControlServer(ldapBasicConfig.getDomainControlServer());
                selLdapBasicConfig.setLdapPort(ldapBasicConfig.getLdapPort());
                selLdapBasicConfig.setLdapBaseDN(ldapBasicConfig.getLdapBaseDN());
                selLdapBasicConfig.setLdapBindAccount(ldapBasicConfig.getLdapBindAccount());
                selLdapBasicConfig.setLdapBindAccountPassword(ldapBasicConfig.getLdapBindAccountPassword());
                
                selLdapBasicConfig.setIsNtlm(ldapBasicConfig.getIsNtlm());
                selLdapBasicConfig.setNtlmPcAccount(ldapBasicConfig.getNtlmPcAccount());
                selLdapBasicConfig.setNtlmPcAccountPasswd(ldapBasicConfig.getNtlmPcAccountPasswd());
                selLdapBasicConfig.setNetBiosDomainName(ldapBasicConfig.getNetBiosDomainName());
                selLdapBasicConfig.setLdapDns(ldapBasicConfig.getLdapDns());
            }
            if (type.equals(TRANS_NODE))
            {
                selLdapBasicConfig.setLdapNodeBaseDN(ldapBasicConfig.getLdapNodeBaseDN());
                selLdapBasicConfig.setSearchNodeFilter(ldapBasicConfig.getSearchNodeFilter());
            }
            if (type.equals(TRANS_SEARCH))
            {
                selLdapBasicConfig.setSearchFilter(ldapBasicConfig.getSearchFilter());
                selLdapBasicConfig.setSyncFilter(ldapBasicConfig.getSyncFilter());
                selLdapBasicConfig.setPageCount(ldapBasicConfig.getPageCount());
            }
        }
        return selLdapBasicConfig;
    }
    
}
