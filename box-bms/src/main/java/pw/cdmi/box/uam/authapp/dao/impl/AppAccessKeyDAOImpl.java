package pw.cdmi.box.uam.authapp.dao.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.uam.authapp.dao.AppAccessKeyDAO;
import pw.cdmi.box.uam.authapp.domain.AppAccessKeyCryptUtil;
import pw.cdmi.common.domain.AppAccessKey;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class AppAccessKeyDAOImpl extends AbstractDAOImpl implements AppAccessKeyDAO
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppAccessKeyDAOImpl.class);
    
    @Override
    public AppAccessKey getById(String id)
    {
        AppAccessKey appAccessKey = (AppAccessKey) sqlMapClientTemplate.queryForObject("AppAccessKey.getById",
            id);
        if (appAccessKey != null)
        {
            appAccessKey = AppAccessKeyCryptUtil.decode(appAccessKey);
        }
        return appAccessKey;
    }
    
    @Override
    public List<AppAccessKey> getByAppId(String appId)
    {
        List<AppAccessKey> accessKeyLists = sqlMapClientTemplate.queryForList("AppAccessKey.getByAppId",
            appId);
        for (AppAccessKey app : accessKeyLists)
        {
            AppAccessKeyCryptUtil.decode(app);
        }
        return accessKeyLists;
    }
    
    @Override
    public int delete(String id)
    {
        return sqlMapClientTemplate.delete("AppAccessKey.delete", id);
    }
    
    @Override
    public void create(AppAccessKey appAccessKey)
    {
        appAccessKey.setCreatedAt(new Date());
        appAccessKey = AppAccessKeyCryptUtil.encode(appAccessKey);
        LOGGER.info("change crypt in bms.AppAccessKey");
        sqlMapClientTemplate.insert("AppAccessKey.insert", appAccessKey);
    }
    
    @Override
    public int deleteByAppId(String appId)
    {
        return sqlMapClientTemplate.delete("AppAccessKey.deleteByAppId", appId);
    }
    
}
