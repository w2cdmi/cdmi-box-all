
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.utils.SpringContextUtil;
import pw.cdmi.file.domain.FileObject;
import pw.cdmi.file.domain.FsEndpoint;
import pw.cdmi.file.model.S3FsEndpoint;
import pw.cdmi.file.model.S3FsObject;
import pw.cdmi.file.engine.CloudFilesClient;
import pw.cdmi.file.service.CloudEndpointService;
import pw.cdmi.file.service.CloudFilesService;
import pw.cdmi.file.service.FileObjectService;

import java.util.HashMap;
import java.util.Map;

/************************************************************
 * @Description:
 * <pre>腾讯云COS</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/

@Service
public class CloudFilesServiceImpl implements CloudFilesService {
    private static Logger LOGGER = LoggerFactory.getLogger(CloudFilesServiceImpl.class);

    private static final Map<String, CloudFilesClient> cloudClients = new HashMap<>();

    @Autowired
    private FileObjectService fileObjectService;

    @Autowired
    private CloudEndpointService cloudEndpointService;

    @Override
    public String getDownloadUrl(String objectId) {
        try {
            //
            FileObject fileObject = fileObjectService.get(objectId);
            S3FsObject s3FsObject = new S3FsObject(fileObject);

            CloudFilesClient client = getClient(s3FsObject.getCloud(), s3FsObject.getVersion());
            if(client == null) {
                LOGGER.warn("Can't found the suitable cloud client: objectId={}, cloud={}, version={}", objectId, s3FsObject.getCloud(), s3FsObject.getVersion());
                return null;
            }

            S3FsEndpoint s3FsEndpoint = cloudEndpointService.getFsEndpoint(s3FsObject.getEndpoint());
            if(s3FsEndpoint == null) {
                LOGGER.warn("Failed to get download url of object, can't get the endpoint, endpoint={}", s3FsObject.getEndpoint());
                return null;
            }
            return client.getDownloadUrl(s3FsEndpoint, s3FsObject);
        } catch (InvalidParamException e) {
            LOGGER.warn("Failed to get download url of object, the path is invalid: objectId={}", objectId);
        } catch (Exception e) {
            LOGGER.warn("Failed to get download url of object: objectId={}, error={}", objectId, e.getMessage());
            LOGGER.warn("Failed to get download url of object:", e);
        }

        return null;
    }

    protected CloudFilesClient getClient(String type, String version) {
        if(cloudClients.isEmpty()) {
            Map<String, CloudFilesClient> map = SpringContextUtil.getBeans(CloudFilesClient.class);

            for(CloudFilesClient c : map.values()) {
                cloudClients.put(c.getName(), c);
            }
        }

        return cloudClients.get(type);
    }
}
