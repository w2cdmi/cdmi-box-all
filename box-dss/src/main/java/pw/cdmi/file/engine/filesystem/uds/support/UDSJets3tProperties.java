package pw.cdmi.file.engine.filesystem.uds.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jets3t.service.Constants;
import org.jets3t.service.Jets3tProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.file.engine.filesystem.model.cloud.S3EndPointInfo;

public final class UDSJets3tProperties extends Jets3tProperties
{
  private static final long serialVersionUID = 1L;
  private static final Map<String, UDSJets3tProperties> PROPERTIES = new ConcurrentHashMap<String, UDSJets3tProperties>(
    1);

  private static final Logger LOGGER = LoggerFactory.getLogger(UDSJets3tProperties.class);

  public static UDSJets3tProperties getInstance(S3EndPointInfo endpoint)
  {
    UDSJets3tProperties jets3tProperties = null;

    String propertiesKey = endpoint.getIP() + '_' + endpoint.getHttpPort() + '_' + 
      endpoint.getHttpsPort();
    if (PROPERTIES.containsKey(propertiesKey))
    {
      jets3tProperties = (UDSJets3tProperties)PROPERTIES.get(propertiesKey);
      return jets3tProperties;
    }

    jets3tProperties = new UDSJets3tProperties();
    PROPERTIES.put(propertiesKey, jets3tProperties);

    InputStream cpIS = null;
    try
    {
      if (LOGGER.isDebugEnabled())
      {
        LOGGER.debug("Loading properties from resource in the classpath: " + 
          Constants.JETS3T_PROPERTIES_FILENAME);
      }
      cpIS = jets3tProperties.getClass()
        .getResourceAsStream('/' + Constants.JETS3T_PROPERTIES_FILENAME);
      jets3tProperties.loadAndReplaceProperties(cpIS, "Resource '" + 
        Constants.JETS3T_PROPERTIES_FILENAME + "' in classpath");
    }
    catch (IOException e)
    {
      if (LOGGER.isErrorEnabled())
      {
        LOGGER.error("Failed to load properties from resource in classpath: " + 
          Constants.JETS3T_PROPERTIES_FILENAME, e);
      }

      try
      {
        cpIS.close(); } catch (Exception ignored) { if (!LOGGER.isWarnEnabled()); } } finally { try { cpIS.close(); }
      catch (Exception ignored)
      {
        if (LOGGER.isWarnEnabled())
        {
          LOGGER.warn("Failed to close cpIS.");
        }
      }

    }

    jets3tProperties.setProperty("s3service.s3-endpoint", endpoint.getIP());
    jets3tProperties.setProperty("s3service.s3-endpoint-http-port", 
      String.valueOf(endpoint.getHttpPort()));
    jets3tProperties.setProperty("s3service.s3-endpoint-https-port", 
      String.valueOf(endpoint.getHttpsPort()));
    return jets3tProperties;
  }
}