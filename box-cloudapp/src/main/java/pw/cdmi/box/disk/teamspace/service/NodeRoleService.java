package pw.cdmi.box.disk.teamspace.service;

import java.util.List;

import pw.cdmi.box.disk.teamspace.domain.RestNodeRoleInfo;
import pw.cdmi.core.exception.RestException;

public interface NodeRoleService
{
    List<RestNodeRoleInfo> getNodeRoles() throws RestException;
}
