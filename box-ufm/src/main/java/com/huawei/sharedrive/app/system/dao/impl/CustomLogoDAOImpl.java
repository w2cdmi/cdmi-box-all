/**
 * 
 */
package com.huawei.sharedrive.app.system.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.system.dao.CustomizeLogoDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.CustomizeLogo;

/**
 * @author d00199602
 * 
 */
@Service("customLogoDAO")
@SuppressWarnings("deprecation")
public class CustomLogoDAOImpl extends AbstractDAOImpl implements CustomizeLogoDAO
{
    
    @Override
    public CustomizeLogo get(int id)
    {
        // TODO Auto-generated method stub
        return (CustomizeLogo) sqlMapClientTemplate.queryForObject("CustomizeLogo.get", id);
    }
    
}
