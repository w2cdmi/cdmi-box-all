package pw.cdmi.box.uam.statistics.service;

import java.io.IOException;
import java.util.List;

import pw.cdmi.box.uam.exception.RestException;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.httpclient.domain.RestUserHistoryStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.UserCurrentStatisticsInfo;
import pw.cdmi.box.uam.httpclient.domain.UserHistoryStatisticsInfo;
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
import pw.cdmi.uam.domain.AuthApp;

public interface StatisticsService
{
    ConcStatisticsResponse getConcStatisticsHistoryData(ConcStatisticsRequest concStatisticsRequest)
        throws RestException;
    
    List<UserCurrentStatisticsInfo> getCurUserAccount(String groupBy, String appId) throws RestException;
    
    ObjectCurrentStatisticsResponse getFileCurrentData(ObjectCurrentStatisticsRequest fileCurrentRequest)
        throws RestException;
    
    ObjectHistoryStatisticsResponse getFileHistoryData(ObjectHistoryStatisticsRequest statisticsRequest)
        throws RestException;
    
    List<UserHistoryStatisticsInfo> getHistUserAccount(RestUserHistoryStatisticsRequest request,
        AuthApp authApp) throws RestException;
    
    NodeCurrentStatisticsResponse getNodeCurrentData(NodeCurrentStatisticsRequest nodeCurrentRequest)
        throws RestException;
    
    NodeHistoryStatisticsResponse getNodeHistroyData(FileStoreHistoryRequest historyRequest)
        throws RestException;
    
    List<RestRegionInfo> getRegionInfo();
    
    StatisticsTempChart getTempChart(String id);
    
    void saveAndDeleteExpired(StatisticsTempChart statisticsTempChart);
    
    byte[] dataExport(Long beginTime, Long endTime, String type) throws IOException;
    
    UserClusterStatisticsList getUserStoreCurrenData(RestUserClusterStatisticsRequest interzoneRequest)
        throws RestException;
}
