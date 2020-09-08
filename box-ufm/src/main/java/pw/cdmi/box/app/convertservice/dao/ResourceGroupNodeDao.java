/**
 * 
 */
package pw.cdmi.box.app.convertservice.dao;

import java.util.List;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;

/**
 * 管理资源组节点
 * 
 * @author luanxibao
 * 
 */
public interface ResourceGroupNodeDao
{
    /**
     * 获取DSS所有的节点信息
     * @return Dss所有节点信息
     */
    List<ResourceGroupNode> getResourceGroupNodes();
    
}