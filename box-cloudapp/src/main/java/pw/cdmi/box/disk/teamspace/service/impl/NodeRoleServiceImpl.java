package pw.cdmi.box.disk.teamspace.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.client.api.NodeRoleClient;
import pw.cdmi.box.disk.teamspace.domain.RestNodeRoleInfo;
import pw.cdmi.box.disk.teamspace.service.NodeRoleService;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Component("nodeRoleService")
public class NodeRoleServiceImpl implements NodeRoleService
{
    @Resource
    private RestClient ufmClientService;
    
    private NodeRoleClient nodeRoleClient;
    
    @Override
    public List<RestNodeRoleInfo> getNodeRoles() throws RestException
    {
        return nodeRoleClient.getNodeRoles();
    }
    
    @PostConstruct
    void init()
    {
        this.nodeRoleClient = new NodeRoleClient(ufmClientService);
    }
}
