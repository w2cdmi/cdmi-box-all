package pw.cdmi.file.engine.filesystem.support.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSEndpointStatus;
import pw.cdmi.file.engine.filesystem.support.dao.FSEndpointDao;
import pw.cdmi.file.engine.filesystem.support.service.FSEndpointService;

@Service("fsEndpointService")
public class FSEndpointServiceImpl
  implements FSEndpointService
{

  @Autowired
  private FSEndpointDao fsEndpointDao;

  @MethodLogAble(Level.INFO)
  @Transactional(propagation=Propagation.REQUIRED)
  public FSEndpoint addFSEndpoint(FSEndpoint endpoint)
  {
    this.fsEndpointDao.create(endpoint);

    return endpoint;
  }

  @MethodLogAble(Level.INFO)
  @Transactional(propagation=Propagation.REQUIRED)
  public void updateFSEndpointsForCurrentDevice(FSEndpoint endpoint)
  {
    if (endpoint == null)
    {
      return;
    }
    updateBaseInfo(endpoint);
    this.fsEndpointDao.updateDeviceInfo(EnvironmentUtils.getHostName(), endpoint);
  }

  @MethodLogAble(Level.INFO)
  @Transactional(propagation=Propagation.REQUIRED)
  public void deleteFSEndpoint(FSEndpoint endpoint)
  {
    endpoint.setDevice(EnvironmentUtils.getHostName());
    this.fsEndpointDao.delete(endpoint);
  }

  public FSEndpoint getFSEndpoint(String endpointID)
  {
    return (FSEndpoint)this.fsEndpointDao.get(new FSEndpoint(endpointID, EnvironmentUtils.getHostName()));
  }

  public List<FSEndpoint> getAllFSEndpointsForCurrentDevice()
  {
    List<FSEndpoint> temp = this.fsEndpointDao.queryAllForDevice(EnvironmentUtils.getHostName());

    List<FSEndpoint> endpoints = new ArrayList<FSEndpoint>(1);

    if (CollectionUtils.isEmpty(temp))
    {
      return endpoints;
    }

    for (FSEndpoint endpoint : temp)
    {
      endpoints.add(endpoint.createRealFSEndpoint());
    }

    return endpoints;
  }

  @MethodLogAble(Level.INFO)
  @Transactional(propagation=Propagation.REQUIRED)
  public void updateBaseInfo(FSEndpoint endpoint)
  {
    this.fsEndpointDao.updateBaseInfo(endpoint);
  }

  @MethodLogAble(Level.INFO)
  @Transactional(propagation=Propagation.REQUIRED)
  public void updateFSEndpoint(FSEndpoint endpoint)
  {
    updateBaseInfo(endpoint);
    if (endpoint.getAccessPaths() != null)
    {
      this.fsEndpointDao.addAccessPath(endpoint.getId(), endpoint.getAccessPaths());
    }
  }

  @MethodLogAble(Level.INFO)
  @Transactional(propagation=Propagation.REQUIRED)
  public void updateFSEndpointExtAttribute(FSEndpoint endpoint)
  {
    this.fsEndpointDao.updateExtAttribute(endpoint);
  }

  @MethodLogAble(Level.INFO)
  @Transactional(propagation=Propagation.REQUIRED)
  public void updateFSEndpointStatus(FSEndpoint endpoint, FSEndpointStatus status)
  {
    endpoint.setStatus(status);
    this.fsEndpointDao.updateStatus(endpoint);
  }
}