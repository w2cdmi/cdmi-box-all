
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.file.dao.CloudEndpointDao;
import pw.cdmi.file.domain.FsEndpoint;
import pw.cdmi.file.model.S3FsEndpoint;
import pw.cdmi.file.service.CloudEndpointService;

/************************************************************
 * @Description:
 * <pre> endpoint </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/

@Service
public class CloudEndpointServiceImpl implements CloudEndpointService {
    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private CloudEndpointDao cloudEndpointDao;

    @Override
    public S3FsEndpoint getFsEndpoint(String id) {
        S3FsEndpoint endpoint = null;
        String key = "FsEndpoint.x121432343" + id;
        if(cacheClient != null) {
            endpoint = (S3FsEndpoint)cacheClient.getCache(key);
        }

        //缓存未命中
        if(endpoint == null) {
            FsEndpoint fsEndpoint = cloudEndpointDao.get(id);

            if(fsEndpoint != null && cacheClient != null) {
                //放入缓存
                long t = System.currentTimeMillis();
                S3FsEndpoint s3FsEndpoint = new S3FsEndpoint(fsEndpoint);
                System.err.println("Time cost: " + (System.currentTimeMillis() - t));
                cacheClient.setCacheNoExpire(key, s3FsEndpoint);
            }
        }

        return endpoint;
    }
}
