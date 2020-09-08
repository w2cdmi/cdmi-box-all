package pw.cdmi.box.app.convertservice.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;

import pw.cdmi.box.app.convertservice.dao.ResourceGroupNodeDao;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("cSResourceGroupNodeDao")
@SuppressWarnings("deprecation")
public class ResourceGroupNodeDaoImpl extends AbstractDAOImpl implements ResourceGroupNodeDao
{
       
    @Override
    public List<ResourceGroupNode> getResourceGroupNodes()
    {        
        return  (List<ResourceGroupNode>) sqlMapClientTemplate.queryForList("ResourceGroupNode.getNodes");
    }
}
