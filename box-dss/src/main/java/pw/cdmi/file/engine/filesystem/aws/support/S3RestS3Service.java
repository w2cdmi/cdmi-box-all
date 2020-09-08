package pw.cdmi.file.engine.filesystem.aws.support;

import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.impl.rest.httpclient.RestStorageService;
import org.jets3t.service.security.ProviderCredentials;

import pw.cdmi.file.engine.core.config.SystemConfigContainer;

public class S3RestS3Service extends RestS3Service
{
  public S3RestS3Service(ProviderCredentials credentials, S3Jets3tProperties jets3tProperties)
    throws S3ServiceException
  {
    super(credentials, null, null, jets3tProperties);
  }

  protected HttpUriRequest setupConnection(RestStorageService.HTTP_METHOD method, String bucketName, String objectKey, Map<String, String> requestParameters)
    throws S3ServiceException
  {
    HttpUriRequest request = super.setupConnection(method, bucketName, objectKey, requestParameters);
    request.setHeader("Connection", "Close");
    if (RestStorageService.HTTP_METHOD.PUT == method)
    {
      request.setHeader("Expect", "100-continue");

      request.getParams().setIntParameter("http.protocol.wait-for-continue", 2000);
    }
    return request;
  }

  public boolean isHttpsOnly()
  {
    return SystemConfigContainer.getBoolean("support.filesystem.uds.https.only", Boolean.valueOf(false)).booleanValue();
  }
}