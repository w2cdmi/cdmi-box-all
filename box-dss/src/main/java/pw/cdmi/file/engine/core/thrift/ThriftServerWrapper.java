/**
 * 
 */
package pw.cdmi.file.engine.core.thrift;

import pw.cdmi.common.thrift.ThriftServer;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;

/**
 * @author q90003805
 *         
 */
public class ThriftServerWrapper extends ThriftServer
{
    @Override
    public int getPort()
    {
        return SystemConfigContainer.getInteger(SystemConfigKeys.THRIFT_DATASERVER_PUBLISH_PORT, 12346);
    }
    
}
