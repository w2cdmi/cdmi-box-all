package com.huawei.sharedrive.uam.cmb.retrieve.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBOrgInfoService;
import com.huawei.sharedrive.uam.cmb.openapi.domain.ResponseOrgTreeNode;
import com.huawei.sharedrive.uam.cmb.retrieve.domain.OrgTreeNode;
import com.huawei.sharedrive.uam.cmb.retrieve.manager.OrgRetrieveManager;
import com.huawei.sharedrive.uam.core.domain.Limit;

@Component
public class OrgRetrieveManagerImpl implements OrgRetrieveManager
{
    @Autowired
    private CMBOrgInfoService cMBOrgInfoService;
    
    @Override
    public ResponseOrgTreeNode getAllOrgInfo(String fatherGroupId, Limit limit)
    {
        int count = cMBOrgInfoService.getByFatherGroupIdCount(fatherGroupId);
        List<CMBOrgInfo> list = cMBOrgInfoService.getByFatherGroupId(fatherGroupId, limit);
        List<OrgTreeNode> treeNodeList = new ArrayList<OrgTreeNode>();
        if (null != list && list.size() > 0)
        {
            for (CMBOrgInfo cmbOrgInfo : list)
            {
                OrgTreeNode orgTreeNode = new OrgTreeNode();
                orgTreeNode.setFatherGroupId(cmbOrgInfo.getFatherGroupId());
                orgTreeNode.setGroupId(cmbOrgInfo.getGroupId());
                orgTreeNode.setName(cmbOrgInfo.getGroupName());
                orgTreeNode.setOrgId(cmbOrgInfo.getOrgId());
                orgTreeNode.setIsParent("true");
                treeNodeList.add(orgTreeNode);
            }
        }
        ResponseOrgTreeNode responseOrgTreeNode = new ResponseOrgTreeNode();
        responseOrgTreeNode.setLimit(limit.getLength());
        responseOrgTreeNode.setOffset(limit.getOffset());
        responseOrgTreeNode.setTotalCount((long) count);
        responseOrgTreeNode.setOrgs(treeNodeList);
        return responseOrgTreeNode;
    }
}
