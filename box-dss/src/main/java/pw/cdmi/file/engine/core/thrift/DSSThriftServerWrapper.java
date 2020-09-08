/**
 * 
 */
package pw.cdmi.file.engine.core.thrift;

import pw.cdmi.file.engine.core.config.SystemConfigContainer;

/**
 * @author zwx371120
 *         
 */
public class DSSThriftServerWrapper extends DSSLocalThriftServer
{
    @Override
    public int getPort()
    {
        return SystemConfigContainer.getInteger("", 10785);
    }
    
}
