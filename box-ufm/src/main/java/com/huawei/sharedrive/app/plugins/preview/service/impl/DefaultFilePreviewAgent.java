
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.app.plugins.preview.service.impl;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dataserver.thrift.client.PreviewConvertThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceClusterDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.thrift.plugins.preview.convert.TConvertTask;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pw.cdmi.common.preview.domain.ConvertTaskParser;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.lang.IllegalStateException;

/************************************************************
 * @Description:
 * <pre>文件预览代理类</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/3/10
 ************************************************************/

@Service
public class DefaultFilePreviewAgent implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(DefaultFilePreviewAgent.class);

    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;

    @Autowired
    private PluginServiceClusterDAO pluginServiceClusterDAO;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private DssDomainService dssDomainService;

    @Value("${activemq.broker.url}")
    private String jmsUrl;

    @Value("${activemq.preview.convert.job.queue}")
    private String jobQueue;

    @Value("${preview.convert.auto}")
    private boolean isAutoConvert;

    private long convertTimeout;

    @PostConstruct
    public void init() throws JMSException {
        //处理 activemq.preview.convert.job.queue 队列下的消息(预览消息)
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(jobQueue);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof BytesMessage) {
            logger.debug("receive a convert task message");
            sendTaskToAgent((BytesMessage) message);
        } else {
            logger.warn("receive a unknown message, type is " + message.getClass().getName());
        }
    }

    private void sendTaskToAgent(BytesMessage bytesMessage) {
        PreviewConvertThriftServiceClient client = null;
        try {
            byte[] data = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(data);
            TConvertTask task = ConvertTaskParser.bytesToTConvertTask(data, 0, data.length);
            String sourceObjectId = task.getSourceObjectId();
            String sourceFileSuffix = task.getSourceFileSuffix();
            long accountId = task.getAccountId();
            String storageObjectId = task.getStorageObjectId();
            int priority = task.getPriority();
            logger.debug("sourceObjectId: " + sourceObjectId + ", sourceFileSuffix: " + sourceFileSuffix + ", accountId: " + accountId + ", storageObjectId: " + storageObjectId + ", priority: " + priority);
            int groupId = getDssIdByObjectId(sourceObjectId);
            ResourceGroup group = resourceGroupService.getResourceGroup(groupId);
            String domain = dssDomainService.getDomainByDssId(group);
            client = new PreviewConvertThriftServiceClient(domain, group.getManagePort());
            client.addTask(task);
        } catch (RuntimeException e) {
            logger.error("error occur when exec convert task", e);
        } catch (Exception e) {
            logger.error("error occur when exec convert task", e);
        } finally {
            if (null != client) {
                client.close();
            }
        }
    }

    protected int getDssIdByObjectId(String objectId) {
        ObjectReference objectReference = objectReferenceDAO.get(objectId);
        if (objectReference == null) {
            throw new IllegalStateException("can not found source object " + objectId);
        }
        PluginServiceCluster cluster = pluginServiceClusterDAO.getByAppIdAndRouteInfo(objectReference.getResourceGroupId(), FilePreviewManager.PREVIEW_CONVERT_APP_ID);
        if (cluster == null) {
            throw new IllegalStateException("object " + objectId + " is storage in group " + objectReference.getResourceGroupId() + ", but can not found preview convert cluster dss to it");
        }

        return cluster.getDssId();
    }

}
