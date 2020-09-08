package pw.cdmi.box.disk.system.dao.impl;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.system.dao.CustomizeLogoDAO;
import pw.cdmi.common.domain.CustomizeLogo;

@Service("CustomLogoDAO")
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
