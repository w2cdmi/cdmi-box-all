package pw.cdmi.file.engine.filesystem.manage.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.thrift.filesystem.StorageInfo;
import com.huawei.sharedrive.thrift.filesystem.StorageResouceThriftServiceOnDss;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.thrift.exception.ThriftException;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.manage.FSEndpointManager;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class StorageResouceThriftServiceImpl
  implements StorageResouceThriftServiceOnDss.Iface
{
  private static final Logger LOGGER = LoggerFactory.getLogger(StorageResouceThriftServiceImpl.class);
  private static final int UDS_ENDPOINT_SIX = 6;

  @Autowired
  private FSEndpointManager fsEndpointManager;

  @MethodLogAble(value=Level.INFO, newLogId=true, isPrintResult=false)
  public List<StorageInfo> getAllStorageResource()
    throws TException
  {
    try
    {
      List<FSEndpoint> endpoints = this.fsEndpointManager.getAllFSEndpoint();
      List<StorageInfo> storageList = new ArrayList<StorageInfo>(10);
      if (!endpoints.isEmpty())
      {
        StorageInfo storageInfo = null;
        for (FSEndpoint endpoint : endpoints)
        {
          storageInfo = createStorageInfo(endpoint);
          storageList.add(storageInfo);
        }
      }
      return storageList;
    }
    catch (Exception e)
    {
      LOGGER.error("get storageresource failed.", e);
      throw new ThriftException(e);
    }
  }

  @MethodLogAble(value=Level.INFO, newLogId=true, isPrintArgs=false)
  public String addStorageResource(StorageInfo storageInfo)
    throws TException
  {
    try
    {
      FSEndpoint endpoint = createEndpoint(storageInfo);

      this.fsEndpointManager.addFSEndpoint(endpoint);

      Thread.sleep(1000L);

      return endpoint.getId();
    }
    catch (Exception e)
    {
      LOGGER.error("add storageresource failed .", e);
      throw new ThriftException(e);
    }
  }

  @MethodLogAble(value=Level.INFO, newLogId=true)
  public void disableStorageResource(String id)
    throws TException
  {
    this.fsEndpointManager.disableFSEndpoint(id);
  }

  @MethodLogAble(value=Level.INFO, newLogId=true)
  public void enableStorageResource(String id)
    throws TException
  {
    this.fsEndpointManager.enableStorageResource(id);
  }

  @MethodLogAble(value=Level.INFO, newLogId=true, isPrintArgs=false)
  public void changeStorageResource(StorageInfo storageInfo)
    throws TException
  {
    try
    {
      LOGGER.info("Change Endpoint : " + storageInfo.getId());

      FSEndpoint endpoint = createEndpoint(storageInfo);
      this.fsEndpointManager.updateFSEndpoint(endpoint);
    }
    catch (Exception e)
    {
      LOGGER.error("change storageresource failed [ " + storageInfo.getId() + " ]", e);
      throw new ThriftException(e);
    }
  }

  @MethodLogAble(value=Level.INFO, newLogId=true)
  public void deleteStorageResource(String id)
    throws TException
  {
    try
    {
      this.fsEndpointManager.removeFSEndpoint(id);
    }
    catch (FSException e)
    {
      LOGGER.error("delete storageresource failed [ " + id + " ]", e);
      throw new ThriftException(e);
    }
  }

  @MethodLogAble(value=Level.INFO, newLogId=true, isPrintResult=false)
  public StorageInfo getStorageResource(String id)
    throws TException
  {
    try
    {
      FSEndpoint endpoint = this.fsEndpointManager.getFSEndpoint(id);
      if (endpoint != null)
      {
        StorageInfo storageInfo = createStorageInfo(endpoint);
        String endpointStr = storageInfo.getEndpoint();
        String[] infos = endpointStr.split(":");
        if (infos.length == UDS_ENDPOINT_SIX)
        {
          String ip = StringUtils.trimToEmpty(infos[0]);
          String httpPort = StringUtils.trimToEmpty(infos[1]);
          String httpsPort = StringUtils.trimToEmpty(infos[2]);
          String accessKey = StringUtils.trimToEmpty(infos[3]);
          String encryptedSecretKey = StringUtils.trimToEmpty(infos[4]);
          String encryptKey = StringUtils.trimToEmpty(infos[5]);
          String secretKey = EDToolsEnhance.decode(encryptedSecretKey, encryptKey);
          StringBuilder newEndpoint = new StringBuilder(ip).append(":")
            .append(httpPort)
            .append(":")
            .append(httpsPort)
            .append(":")
            .append(accessKey)
            .append(":")
            .append(secretKey);
          storageInfo.setEndpoint(newEndpoint.toString());
        }
        return storageInfo;
      }
    }
    catch (Exception e)
    {
      LOGGER.error("get storageresource failed.", e);
      throw new ThriftException(e);
    }
    return null;
  }

  private StorageInfo createStorageInfo(FSEndpoint endpoint)
  {
    StorageInfo res = new StorageInfo();
    res.setId(endpoint.getId());
    res.setEndpoint(endpoint.getEndpoint());
    res.setFsType(endpoint.getFsType());
    res.setWriteAlbe(endpoint.isWriteAble());
    res.setAvailAble(endpoint.isAvailable());
    res.setNoSpace(endpoint.isNoSpace());
    res.setStatus(endpoint.getStatus().getCode());
    res.setMaxUtilization(endpoint.getMaxUtilization().floatValue());
    res.setRetrieval(endpoint.getRetrieval().floatValue());
    res.setSpaceSize(endpoint.getSpaceSize());
    res.setUsedSize(endpoint.getUsedSize());
    return res;
  }

  private FSEndpoint createEndpoint(StorageInfo storageInfo)
  {
    FSEndpoint endpoint = new FSEndpoint();
    endpoint.setId(storageInfo.getId());
    endpoint.setEndpoint(storageInfo.getEndpoint());
    endpoint.setFsType(storageInfo.getFsType());
    endpoint.setWriteAble(storageInfo.isWriteAlbe());
    endpoint.setAvailable(storageInfo.isAvailAble());
    endpoint.setNoSpace(storageInfo.isNoSpace());
    endpoint.setMaxUtilization(new Float(storageInfo.getMaxUtilization()));
    endpoint.setRetrieval(new Float(storageInfo.getRetrieval()));
    endpoint.setSpaceSize(storageInfo.getSpaceSize());
    endpoint.setUsedSize(storageInfo.getUsedSize());
    return endpoint;
  }
}