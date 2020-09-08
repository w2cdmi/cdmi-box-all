/**
 * 
 */
package com.huawei.sharedrive.app.system.dao;

import java.util.List;

import pw.cdmi.box.dao.BaseDAO;
import pw.cdmi.common.domain.AccessNetwork;

/**
 * @author d00199602
 * 
 */
public interface AccessNetworkDAO extends BaseDAO<AccessNetwork, String>
{
    List<AccessNetwork> getAll();
}