package com.huawei.sharedrive.app.files.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.WaitingDeleteObjectDAO;
import com.huawei.sharedrive.app.files.domain.WaitingDeleteObject;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;

@Service("waitingDeleteObjectDAO")
@SuppressWarnings("deprecation")
public class WaitingDeleteObjectDAOImpl extends AbstractDAOImpl implements WaitingDeleteObjectDAO
{
    @Override
    public void create(WaitingDeleteObject waitingDeleteObject)
    {
        sqlMapClientTemplate.insert("WaitingDeleteObject.insert", waitingDeleteObject);
    }
    
    @Override
    public int delete(WaitingDeleteObject waitingDeleteObject)
    {
        return sqlMapClientTemplate.delete("WaitingDeleteObject.delete", waitingDeleteObject);
    }
    
    @Override
    public WaitingDeleteObject get(String objectId)
    {
        WaitingDeleteObject waitingDeleteObject = new WaitingDeleteObject();
        waitingDeleteObject.setObjectId(objectId);
        return (WaitingDeleteObject) sqlMapClientTemplate.queryForObject("WaitingDeleteObject.get",
            waitingDeleteObject);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<WaitingDeleteObject> listWaitingDeleteObject(Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("WaitingDeleteObject.getAll", map);
    }
    
}
