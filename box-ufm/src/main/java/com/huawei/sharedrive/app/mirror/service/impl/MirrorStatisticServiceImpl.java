package com.huawei.sharedrive.app.mirror.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.mirror.dao.ObjectMirrorShipDAO;
import com.huawei.sharedrive.app.mirror.domain.ObjectMirrorShip;
import com.huawei.sharedrive.app.mirror.service.MirrorStatisticService;

import pw.cdmi.box.domain.Limit;

@Component
public class MirrorStatisticServiceImpl implements MirrorStatisticService
{
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private ObjectMirrorShipDAO objectMirrorShipDAO;
    
    @Override
    public List<ObjectMirrorShip> listObjectMirrorShip(String parentObjectId)
    {
        return objectMirrorShipDAO.listObjectMirrorShip(parentObjectId);
    }
    
    /**
     * 暂时使用主数据库统计，下一个迭代版本再修改============================================= c00287749
     */
    @Override
    public List<INode> lstFileAndVersionNode(long userId, Limit limit)
    {
        
        return iNodeDAO.lstFileAndVersionNode(userId, limit);
    }
    
}
