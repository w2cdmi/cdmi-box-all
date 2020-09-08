package pw.cdmi.file.engine.filesystem.uds.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.HttpResponseAndByteCount;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.impl.rest.httpclient.RestStorageService;
import org.jets3t.service.model.CreateBucketConfiguration;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.StorageBucket;
import org.jets3t.service.model.StorageObject;
import org.jets3t.service.security.ProviderCredentials;
import org.jets3t.service.utils.Mimetypes;
import org.jets3t.service.utils.ServiceUtils;

import pw.cdmi.file.engine.core.config.SystemConfigContainer;

public class UDSRestS3Service extends RestS3Service {
    private static final Log log = LogFactory.getLog(UDSRestS3Service.class);

    public UDSRestS3Service(ProviderCredentials credentials, UDSJets3tProperties jets3tProperties)
        throws S3ServiceException {
        super(credentials, null, null, jets3tProperties);
    }

    protected HttpUriRequest setupConnection(RestStorageService.HTTP_METHOD method, String bucketName, String objectKey,
        Map<String, String> requestParameters) throws S3ServiceException {
        HttpUriRequest request = super.setupConnection(method, bucketName, objectKey, requestParameters);
        request.setHeader("Connection", "Close");
        if (RestStorageService.HTTP_METHOD.PUT == method) {
            request.setHeader("Expect", "100-continue");

            request.getParams().setIntParameter("http.protocol.wait-for-continue", 2000);
        }
        return request;
    }

    public boolean isHttpsOnly() {
        return SystemConfigContainer.getBoolean("support.filesystem.uds.https.only", Boolean.valueOf(false))
            .booleanValue();
    }

    @Override
    public S3Bucket createBucket(String bucketName) throws S3ServiceException {
        try {
            return this.createBucket(bucketName,
                getJetS3tProperties().getStringProperty("s3service.default-bucket-location", "cn-north-1"), null);
        } catch (ServiceException se) {
            throw new S3ServiceException(se);
        }
    }

    @Override
    public S3Bucket createBucket(String bucketName, String location, AccessControlList acl) throws S3ServiceException {
        try {
            assertAuthenticatedConnection("createBucket");
            return (S3Bucket) createBucketImpl(bucketName, location, acl);
        } catch (ServiceException se) {
            throw new S3ServiceException(se);
        }
    }

    protected StorageBucket createBucketImpl(String bucketName, String location, AccessControlList acl)
        throws ServiceException {
        return this.createBucketImpl(bucketName, location, acl, Collections.<String, Object> emptyMap());
    }

    @Override
    protected StorageBucket createBucketImpl(String bucketName, String location, AccessControlList acl,
        Map<String, Object> headers) throws ServiceException {
        if (log.isDebugEnabled()) {
            log.debug("Creating bucket with name: " + bucketName);
        }

        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.putAll(headers);
        HttpEntity requestEntity = null;

        if (location != null && !"US".equalsIgnoreCase(location)) {
            metadata.put("Content-Type", "text/xml");
            try {
                CreateBucketConfiguration config = new CreateBucketConfiguration(location);
                String configXml = config.toXml();
                metadata.put("Content-Length", String.valueOf(configXml.length()));
                requestEntity = new StringEntity(configXml,
                    ContentType.create("text/plain", Constants.DEFAULT_ENCODING));
            } catch (ParserConfigurationException e) {
                throw new ServiceException("Unable to encode CreateBucketConfiguration XML document", e);
            } catch (TransformerException e) {
                throw new ServiceException("Unable to encode CreateBucketConfiguration XML document", e);
            }
        }

        Map<String, Object> map = createObjectImpl(bucketName, null, null, requestEntity, metadata, null, acl, null,
            null);

        StorageBucket bucket = newBucket();
        bucket.setName(bucketName);
        bucket.setLocation(location);
        bucket.setAcl(acl);
        bucket.replaceAllMetadata(map);
        return bucket;
    }

    protected Map<String, Object> createObjectImpl(String bucketName, String objectKey, String contentType,
        HttpEntity requestEntity, Map<String, Object> metadata, Map<String, String> requestParams,
        AccessControlList acl, String storageClass, String serverSideEncryptionAlgorithm) throws ServiceException {
        if (metadata == null) {
            metadata = new HashMap<String, Object>();
        } else {
            // Use a new map object in case the one we were provided is immutable.
            metadata = new HashMap<String, Object>(metadata);
        }
        if (contentType != null) {
            metadata.put("Content-Type", contentType);
        } else {
            metadata.put("Content-Type", Mimetypes.MIMETYPE_OCTET_STREAM);
        }

        prepareStorageClass(metadata, storageClass, true, objectKey);

        // Apply per-object or default options when uploading object
        if (objectKey != null) {
            // do not set server-side encryption flag for part-objects
            if (requestParams == null || !requestParams.containsKey("partNumber")) {
                prepareServerSideEncryption(metadata, serverSideEncryptionAlgorithm, objectKey);
            }
        }

        boolean isExtraAclPutRequired = !prepareRESTHeaderAcl(metadata, acl);

        if (log.isDebugEnabled()) {
            log.debug("Creating object bucketName=" + bucketName + ", objectKey=" + objectKey + ", storageClass="
                    + storageClass + "." + " Content-Type=" + metadata.get("Content-Type") + " Including data? "
                    + (requestEntity != null) + " Metadata: " + metadata + " ACL: " + acl);
        }

        HttpResponseAndByteCount methodAndByteCount = performRestPut(bucketName, objectKey, metadata, requestParams,
            requestEntity, true);

        // Consume response content.
        HttpResponse httpResponse = methodAndByteCount.getHttpResponse();

        Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(metadata); // Keep existing metadata.
        map.putAll(convertHeadersToMap(httpResponse.getAllHeaders()));
        map.put(StorageObject.METADATA_HEADER_CONTENT_LENGTH, String.valueOf(methodAndByteCount.getByteCount()));
        map = ServiceUtils.cleanRestMetadataMap(map, this.getRestHeaderPrefix(), this.getRestMetadataPrefix());

        if (isExtraAclPutRequired) {
            if (log.isDebugEnabled()) {
                log.debug("Creating object with a non-canned ACL using REST, so an extra ACL Put is required");
            }
            putAclImpl(bucketName, objectKey, acl, null);
        }

        return map;
    }

    protected void prepareStorageClass(Map<String, Object> metadata, String storageClass,
        boolean useDefaultStorageClass, String objectKey) {
        if (metadata == null) {
            throw new IllegalArgumentException("Null metadata not allowed.");
        }
        if (getEnableStorageClasses()) {
            if (storageClass == null && useDefaultStorageClass && this.defaultStorageClass != null) {
                // Apply default storage class
                storageClass = this.defaultStorageClass;
                log.debug("Applied default storage class '" + storageClass + "' to object '" + objectKey + "'");
            }
            if (storageClass != null && !storageClass.equals(""))  // Hack to avoid applying empty storage class (Issue
                                                                   // #121)
            {
                metadata.put("x-default-storage-class", storageClass);
            }
        }
    }

    protected HttpResponseAndByteCount performRestPut(String bucketName, String objectKey, Map<String, Object> metadata,
        Map<String, String> requestParameters, HttpEntity requestEntity, boolean autoRelease) throws ServiceException {
        // Add any request parameters.
        HttpUriRequest httpMethod = setupConnection(HTTP_METHOD.PUT, bucketName, objectKey, requestParameters);

        addMetadataToHeaders(httpMethod, metadata);

        long contentLength = 0;

        if (log.isTraceEnabled()) {
            log.trace("Put request with entity: " + requestEntity);
        }
        if (requestEntity != null) {
            ((HttpPut) httpMethod).setEntity(requestEntity);

            /* Explicitly apply any latent Content-Type header from the request entity to the
            * httpMethod to ensure it is included in the request signature, since it will be
            * included in the wire request by HttpClient. But only apply the latent mimetype
            * if an explicit Content-Type is not already set. See issue #109
            */
            if (requestEntity.getContentType() != null && httpMethod.getFirstHeader("Content-Type") == null) {
                httpMethod.setHeader(requestEntity.getContentType());
            }
        }

        HttpResponse result = performRequest(httpMethod, new int[] { 200, 204 });

        if (requestEntity != null) {
            // Respond with the actual guaranteed content length of the uploaded data.
            contentLength = ((HttpPut) httpMethod).getEntity().getContentLength();
        }

        if (autoRelease) {
            releaseConnection(result);
        }

        return new HttpResponseAndByteCount(result, contentLength);
    }

    
    private void releaseConnection(HttpResponse pResponse) {
        if(pResponse == null) {
            return;
        }
        try {
            EntityUtils.consume(pResponse.getEntity());
        }
        catch(Exception e) {
            log.warn("Unable to consume response entity " + pResponse, e);
        }
    }
    
    private Map<String, Object> convertHeadersToMap(Header[] headers) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; headers != null && i < headers.length; i++) {
            map.put(headers[i].getName(), headers[i].getValue());
        }
        return map;
    }
}