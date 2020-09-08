package com.huawei.sharedrive.uam.user.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.user.dao.UserCustomDAO;
import com.huawei.sharedrive.uam.user.domain.UserCustom;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service
@SuppressWarnings("deprecation")
public class UserCustomDAOImpl extends AbstractDAOImpl implements UserCustomDAO
{
    
    @Override
    public UserCustom get(Long id)
    {
        UserCustom custom = new UserCustom();
        custom.setId(id);
        custom = (UserCustom) sqlMapClientTemplate.queryForObject("UserCustom.get", custom);
        if (custom == null)
        {
            custom = new UserCustom();
            custom.setId(id);
            custom.setInit(true);
            custom.setLanguage("zh_CN");
            custom.setDatePattern("yyyy-MM-dd");
            custom.setTimePattern("HH:mm:ss");
            custom.setTimeZone("Asia/Shanghai");
        }
        return custom;
    }
    
    @Override
    public void create(UserCustom t)
    {
        sqlMapClientTemplate.insert("UserCustom.insert", t);
    }
    
    @Override
    public void update(UserCustom t)
    {
        sqlMapClientTemplate.update("UserCustom.update", t);
    }
    
    @Override
    public void delete(Long id)
    {
        UserCustom custom = new UserCustom();
        custom.setId(id);
        sqlMapClientTemplate.delete("UserCustom.delete", custom);
    }
    
}
