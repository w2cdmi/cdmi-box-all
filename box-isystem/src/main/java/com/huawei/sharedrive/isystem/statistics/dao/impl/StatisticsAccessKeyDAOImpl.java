package com.huawei.sharedrive.isystem.statistics.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.statistics.dao.StatisticsAccessKeyDAO;
import com.huawei.sharedrive.isystem.statistics.domain.StatisticsAccessKey;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.EDToolsEnhance;

@Service
@SuppressWarnings(
{"unchecked", "deprecation"})
public class StatisticsAccessKeyDAOImpl extends AbstractDAOImpl implements StatisticsAccessKeyDAO
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsAccessKeyDAOImpl.class);
    
    @Override
    public void create(StatisticsAccessKey statisticsAccessKey)
    {
        Map<String, String> encodedKeys = EDToolsEnhance.encode(statisticsAccessKey.getSecretKey());
        statisticsAccessKey.setSecretKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
        statisticsAccessKey.setSecretKeyEncodeKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
        statisticsAccessKey.setCreatedAt(new Date());
        LOGGER.info("set crypt in isystem.StatisticsAccessKey");
        sqlMapClientTemplate.insert("StatisticsAccessKey.insert", statisticsAccessKey);
    }
    
    @Override
    public List<StatisticsAccessKey> queryList()
    {
        List<StatisticsAccessKey> statisticsAccessKeies = sqlMapClientTemplate.queryForList("StatisticsAccessKey.getListAll");
        for (StatisticsAccessKey ak : statisticsAccessKeies)
        {
            ak.setSecretKey(EDToolsEnhance.decode(ak.getSecretKey(), ak.getSecretKeyEncodeKey()));
        }
        return statisticsAccessKeies;
    }
    
    @Override
    public void delete(String accessKey)
    {
        sqlMapClientTemplate.delete("StatisticsAccessKey.deleteById", accessKey);
    }
    
    @Override
    public StatisticsAccessKey get(String id)
    {
        StatisticsAccessKey statisticsAccessKey = (StatisticsAccessKey) sqlMapClientTemplate.queryForObject("StatisticsAccessKey.getById",
            id);
        if (statisticsAccessKey != null)
        {
            statisticsAccessKey.setSecretKey(EDToolsEnhance.decode(statisticsAccessKey.getSecretKey(),
                statisticsAccessKey.getSecretKeyEncodeKey()));
        }
        return statisticsAccessKey;
    }
    
}
