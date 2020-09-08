package pw.cdmi.file.engine.filesystem.support.dao.impl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.core.util.JuuidUtils;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.support.dao.FSEndpointDao;

@Repository("fsEndpointDao")
public class FSEndpointDaoImpl extends IbatisSupportDAO<FSEndpoint>
  implements FSEndpointDao
{
  private static final Logger LOGGER = LoggerFactory.getLogger(FSEndpointDaoImpl.class);

  public List<FSEndpoint> queryAllForDevice(String device)
  {
    return this.sqlMapClientTemplate.queryForList(warpSqlstatement(FSEndpoint.class, "selectAllForCurrentDevice"), device);
  }

  protected void doInsert(FSEndpoint endpoint)
  {
    endpoint.setId(JuuidUtils.generateUUID(endpoint.getEndpoint() + endpoint.getFsType()));
    this.sqlMapClientTemplate.insert(warpSqlstatement(FSEndpoint.class, "insertBaseInfo"), endpoint);

    addAccessPath(endpoint.getId(), endpoint.getAccessPaths());
  }

  protected void doDelete(FSEndpoint obj)
  {
    this.sqlMapClientTemplate.delete(warpSqlstatement(FSEndpoint.class, "deleteBaseInfo"), obj);
    this.sqlMapClientTemplate.delete(warpSqlstatement(FSEndpoint.class, "deleteAccessPathBaseInfo"), obj);
    this.sqlMapClientTemplate.delete(warpSqlstatement(FSEndpoint.class, "deleteDeviceInfo"), obj);
    this.sqlMapClientTemplate.delete(warpSqlstatement(FSEndpoint.class, "deleteAccessPathDeviceInfo"), obj);
  }

  protected FSEndpoint doSelect(FSEndpoint obj)
  {
    return (FSEndpoint)this.sqlMapClientTemplate.queryForObject(warpSqlstatement(FSEndpoint.class, "select"), obj);
  }

  protected int doUpdate(FSEndpoint obj)
  {
    throw new UnsupportedOperationException();
  }

  public void updateBaseInfo(FSEndpoint endpoint)
  {
    this.sqlMapClientTemplate.update(warpSqlstatement(FSEndpoint.class, "updateBaseInfo"), endpoint);
  }

  public void updateDeviceInfo(String device, FSEndpoint endpoint)
  {
    SqlMapClient sqlMapClient = this.sqlMapClientTemplate.getSqlMapClient();
    try
    {
      endpoint.setDevice(device);
      sqlMapClient.update(warpSqlstatement(FSEndpoint.class, "updateDeviceInfo"), endpoint);

      String updateAccessPathDeviceInfo = warpSqlstatement(FSEndpoint.class, "updateAccessPathDeviceInfo");

      sqlMapClient.startBatch();

      Set beenUpdate = new HashSet(endpoint.getAccessPaths().size());
      String pathKey = null;
      for (FSAccessPath path : endpoint.getAccessPaths())
      {
        path.setDevice(device);

        pathKey = path.getId() + '_' + path.getDevice();
        if (!beenUpdate.contains(pathKey))
        {
          sqlMapClient.update(updateAccessPathDeviceInfo, path);
          beenUpdate.add(pathKey);
        }
      }
      sqlMapClient.executeBatch();
    }
    catch (SQLException e)
    {
      LOGGER.warn("update for device failed");
      throw new InnerException(e.getMessage(), e);
    }
  }

  public void addAccessPath(final String endpointID, final List<FSAccessPath> paths)
  {
    this.sqlMapClientTemplate.execute(new SqlMapClientCallback()
    {
      public Integer doInSqlMapClient(SqlMapExecutor executor)
        throws SQLException
      {
        executor.startBatch();
        String sql = FSEndpointDaoImpl.this.warpSqlstatement(FSEndpoint.class, "insertAccessPathBaseInfo");
        for (FSAccessPath accessPath : paths)
        {
          accessPath.setEndpointID(endpointID);
          accessPath.setId(JuuidUtils.generateUUID(accessPath.getPath()));

          executor.insert(sql, accessPath);
        }
        executor.executeBatch();

        return Integer.valueOf(paths.size());
      }
    });
  }

  public void updateStatus(FSEndpoint endpoint)
  {
    this.sqlMapClientTemplate.update(warpSqlstatement(FSEndpoint.class, "updateStatus"), endpoint);
  }

  public void updateExtAttribute(FSEndpoint endpoint)
  {
    this.sqlMapClientTemplate.update(warpSqlstatement(FSEndpoint.class, "updateExtAttribute"), endpoint);
  }

}