package pw.cdmi.box.uam.statistics.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.exception.RestException;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.httpclient.domain.RestUserCurrentStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.RestUserHistoryStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.UserCurrentStatisticsInfo;
import pw.cdmi.box.uam.httpclient.domain.UserCurrentStatisticsList;
import pw.cdmi.box.uam.httpclient.domain.UserHistoryStatisticsInfo;
import pw.cdmi.box.uam.httpclient.domain.UserHistoryStatisticsList;
import pw.cdmi.box.uam.httpclient.rest.StatisticsHttpClient;
import pw.cdmi.box.uam.statistics.dao.StatisticsChartDao;
import pw.cdmi.box.uam.statistics.domain.ConcStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ConcStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.FileStoreHistoryRequest;
import pw.cdmi.box.uam.statistics.domain.NodeCurrentStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.NodeCurrentStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.NodeHistoryStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.ObjectCurrentStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ObjectCurrentStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.ObjectHistoryStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ObjectHistoryStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.RestUserClusterStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.StatisticsTempChart;
import pw.cdmi.box.uam.statistics.domain.UserClusterStatisticsList;
import pw.cdmi.box.uam.statistics.service.StatisticsAccesskeyService;
import pw.cdmi.box.uam.statistics.service.StatisticsService;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.uam.domain.AuthApp;

@Service
public class StatisticsServiceImpl implements StatisticsService
{
    @Autowired
    private StatisticsChartDao statisticsDao;
    
    private StatisticsHttpClient statisticsHttpClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private StatisticsAccesskeyService statisticsAccesskeyService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @PostConstruct
    public void init()
    {
        this.statisticsHttpClient = new StatisticsHttpClient(ufmClientService, statisticsAccesskeyService);
    }
    
    private static final long TIME_EXPIRE = 24 * 60 * 50 * 1000;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAndDeleteExpired(StatisticsTempChart statisticsTempChart)
    {
        Date date = new Date(System.currentTimeMillis() - TIME_EXPIRE);
        statisticsDao.deleteEndToCreatedAt(date);
        statisticsTempChart.setCreatedAt(new Date());
        statisticsDao.insert(statisticsTempChart);
    }
    
    @Override
    public StatisticsTempChart getTempChart(String id)
    {
        return statisticsDao.get(id);
    }
    
    @Override
    public List<UserCurrentStatisticsInfo> getCurUserAccount(String groupBy, String appId)
        throws RestException
    {
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        RestUserCurrentStatisticsRequest request = new RestUserCurrentStatisticsRequest();
        request.setGroupBy(groupBy);
        request.setAppId(null);
        request.setRegionId(null);
        UserCurrentStatisticsList response = statisticsHttpClient.getCurrUserAccount(request, authApp);
        return response.getData();
    }
    
    @Override
    public List<UserHistoryStatisticsInfo> getHistUserAccount(RestUserHistoryStatisticsRequest request,
        AuthApp authApp) throws RestException
    {
        UserHistoryStatisticsList response = statisticsHttpClient.getHistUserAccount(request, authApp);
        return response.getData();
    }
    
    @Override
    public ObjectCurrentStatisticsResponse getFileCurrentData(
        ObjectCurrentStatisticsRequest fileCurrentRequest) throws RestException
    {
        ObjectCurrentStatisticsResponse statisticsResponse = statisticsHttpClient.getCurrentObjectData(fileCurrentRequest);
        return statisticsResponse;
    }
    
    @Override
    public ObjectHistoryStatisticsResponse getFileHistoryData(ObjectHistoryStatisticsRequest statisticsRequest)
        throws RestException
    {
        ObjectHistoryStatisticsResponse statisticsResponse = statisticsHttpClient.getHistoryObjectData(statisticsRequest);
        return statisticsResponse;
    }
    
    @Override
    public NodeHistoryStatisticsResponse getNodeHistroyData(FileStoreHistoryRequest historyRequest)
        throws RestException
    {
        NodeHistoryStatisticsResponse statisticsResponse = statisticsHttpClient.getNodeHistoryObjectData(historyRequest);
        return statisticsResponse;
    }
    
    @Override
    public NodeCurrentStatisticsResponse getNodeCurrentData(NodeCurrentStatisticsRequest nodeCurrentRequest)
        throws RestException
    {
        NodeCurrentStatisticsResponse statisticsResponse = statisticsHttpClient.getCurrentNodeData(nodeCurrentRequest);
        return statisticsResponse;
    }
    
    @Override
    public List<RestRegionInfo> getRegionInfo()
    {
        return statisticsHttpClient.getRegionInfo();
    }
    
    @Override
    public ConcStatisticsResponse getConcStatisticsHistoryData(ConcStatisticsRequest concStatisticsRequest)
        throws RestException
    {
        return statisticsHttpClient.getConcurrenceHistoryData(concStatisticsRequest);
    }
    
    @Override
    public byte[] dataExport(Long beginTime, Long endTime, String type) throws IOException
    {
        
        return statisticsHttpClient.historyDataStatisticsExport(beginTime, endTime, type);
    }
    
    @Override
    public UserClusterStatisticsList getUserStoreCurrenData(RestUserClusterStatisticsRequest interzoneRequest)
        throws RestException
    {
        return statisticsHttpClient.getUserStoreCurrenData(interzoneRequest);
    }
    
}
