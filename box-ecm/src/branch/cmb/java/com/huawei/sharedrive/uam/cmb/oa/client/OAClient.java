package com.huawei.sharedrive.uam.cmb.oa.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.datacontract.schemas._2004._07.cmbchina_yst.ArrayOfOrgInfo;
import org.datacontract.schemas._2004._07.cmbchina_yst.ArrayOfOrgUserRelationInfo;
import org.datacontract.schemas._2004._07.cmbchina_yst.ArrayOfSapUser;
import org.datacontract.schemas._2004._07.cmbchina_yst.OrgInfo;
import org.datacontract.schemas._2004._07.cmbchina_yst.OrgUserRelationInfo;
import org.datacontract.schemas._2004._07.cmbchina_yst.SapUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tempuri.ISyncService;
import org.tempuri.ISyncServiceGetOrgInfosFaultMessageFaultFaultMessage;
import org.tempuri.ISyncServiceGetOrgUserRelationInfosFaultMessageFaultFaultMessage;
import org.tempuri.ISyncServiceGetUserInfoFaultMessageFaultFaultMessage;
import org.tempuri.SyncService;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;
import com.huawei.sharedrive.uam.cmb.oa.exception.CMBOAException;
import com.huawei.sharedrive.uam.cmb.oa.service.ConvertFromWsdl;

@Service
public class OAClient
{
    public static final int PAGE_SIZE = 1000;
    
    public static final int RETRY_TIME = 5;
    
    public static final int SLEEP_TIME = 2000;
    
    private static Logger LOGGER = LoggerFactory.getLogger(OAClient.class);
    
    @Autowired
    private ConvertFromWsdl convertFromWsdl;
    
    @SuppressWarnings("static-access")
    public List<CMBOrgInfo> getAllCMBOrgInfo(String url, String appCode, String pToken, int retryTime)
    {
        List<CMBOrgInfo> cmbOrgInfoList = null;
        try
        {
            LOGGER.info("get all orgInfo begin url:" + url + " appCode:" + appCode + " retryTime:"
                + retryTime);
            if (retryTime >= RETRY_TIME)
            {
                return null;
            }
            URL sUrl = new URL(url);
            ISyncService client = new SyncService(sUrl).getBasicHttpBindingISyncService();
            int pageIndex = 0;
            List<OrgInfo> orgInfoList = new ArrayList<OrgInfo>();
            while (true)
            {
                List<OrgInfo> tempOrgInfo = getCMBOrgInfo(appCode, pToken, pageIndex, client);
                if (null == tempOrgInfo || tempOrgInfo.size() < 1)
                {
                    LOGGER.info("tempOrgInfo is null,break orgInfoList");
                    break;
                }
                pageIndex = pageIndex + 1;
                LOGGER.info("pageIndex is " + pageIndex + " tempOrgInfo size is " + tempOrgInfo.size());
                orgInfoList.addAll(tempOrgInfo);
                if (tempOrgInfo.size() < PAGE_SIZE)
                {
                    LOGGER.info("tempOrgInfo is less than pageSize,break orgInfoList size is "
                        + tempOrgInfo.size());
                    break;
                }
                final Object lockObj = new Object();
                synchronized (lockObj)
                {
                    Thread.currentThread().sleep(SLEEP_TIME);
                }
            }
            cmbOrgInfoList = convertFromWsdl.convertToCMBOrgInfo(orgInfoList);
        }
        catch (MalformedURLException e)
        {
            LOGGER.error("url exception" + e.toString());
            return null;
        }
        catch (CMBOAException e)
        {
            LOGGER.error(e.toString());
            retryTime = retryTime + 1;
            cmbOrgInfoList = getAllCMBOrgInfo(url, appCode, pToken, retryTime);
        }
        catch (ISyncServiceGetOrgInfosFaultMessageFaultFaultMessage e)
        {
            LOGGER.error(e.toString());
            retryTime = retryTime + 1;
            cmbOrgInfoList = getAllCMBOrgInfo(url, appCode, pToken, retryTime);
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
            return null;
        }
        int cmbOrgInfoListSize = 0;
        if (null != cmbOrgInfoList)
        {
            cmbOrgInfoListSize = cmbOrgInfoList.size();
            
        }
        LOGGER.info("get all orgInfo end retryTime:" + retryTime + " cmbOrgInfoList size is "
            + cmbOrgInfoListSize);
        return cmbOrgInfoList;
    }
    
    @SuppressWarnings("static-access")
    public List<CMBSapUser> getAllCMBSapUser(String url, String appCode, String pToken, int retryTime)
    {
        List<CMBSapUser> cmbSapUserList = null;
        try
        {
            LOGGER.info("get all sapUser begin url:" + url + " appCode:" + appCode + " retryTime:"
                + retryTime);
            if (retryTime >= RETRY_TIME)
            {
                return null;
            }
            URL sUrl = new URL(url);
            ISyncService client = new SyncService(sUrl).getBasicHttpBindingISyncService();
            int pageIndex = 0;
            List<SapUser> sapUserList = new ArrayList<SapUser>();
            while (true)
            {
                List<SapUser> sapUsers = getCMBOSapUser(appCode, pToken, pageIndex, client);
                if (null == sapUsers || sapUsers.size() < 1)
                {
                    LOGGER.info("sapUsers is null, break sapUsers");
                    break;
                }
                pageIndex = pageIndex + 1;
                LOGGER.info("pageIndex is " + pageIndex + " sapUsers size is " + sapUsers.size());
                sapUserList.addAll(sapUsers);
                if (sapUsers.size() < PAGE_SIZE)
                {
                    LOGGER.info("sapUsers is less than pageSize,break sapUsers size is " + sapUsers.size());
                    break;
                }
                final Object lockObj = new Object();
                synchronized (lockObj)
                {
                    Thread.currentThread().sleep(SLEEP_TIME);
                }
            }
            cmbSapUserList = convertFromWsdl.convertToCMBSapUser(sapUserList);
        }
        catch (MalformedURLException e)
        {
            LOGGER.error("url exception" + e.toString());
            return null;
        }
        catch (CMBOAException e)
        {
            LOGGER.error(e.toString());
            retryTime = retryTime + 1;
            cmbSapUserList = getAllCMBSapUser(url, appCode, pToken, retryTime);
        }
        catch (ISyncServiceGetUserInfoFaultMessageFaultFaultMessage e)
        {
            LOGGER.error(e.toString());
            retryTime = retryTime + 1;
            cmbSapUserList = getAllCMBSapUser(url, appCode, pToken, retryTime);
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
            return null;
        }
        int cmbSapUserListSize = 0;
        if (null != cmbSapUserList)
        {
            cmbSapUserListSize = cmbSapUserList.size();
            
        }
        LOGGER.info("get all sapUser end retryTime:" + retryTime + " cmbSapUserList size is "
            + cmbSapUserListSize);
        return cmbSapUserList;
    }
    
    @SuppressWarnings("static-access")
    public List<CMBUserRelationInfo> getAllCMBUserRelationInfo(String url, String appCode, String pToken,
        int retryTime)
    {
        List<CMBUserRelationInfo> cmbUserRelationInfoList = null;
        try
        {
            LOGGER.info("get all cmbUserRelationInfoList begin url:" + url + " appCode:" + appCode
                + " retryTime:" + retryTime);
            if (retryTime >= RETRY_TIME)
            {
                return null;
            }
            URL sUrl = new URL(url);
            ISyncService client = new SyncService(sUrl).getBasicHttpBindingISyncService();
            int pageIndex = 0;
            List<OrgUserRelationInfo> orgUserRelationInfoList = new ArrayList<OrgUserRelationInfo>();
            while (true)
            {
                List<OrgUserRelationInfo> tempOrgUserRelationInfo = getCMBUserRelationInfo(appCode,
                    pToken,
                    pageIndex,
                    client);
                if (null == tempOrgUserRelationInfo || tempOrgUserRelationInfo.size() < 1)
                {
                    LOGGER.info("tempOrgUserRelationInfo is null,break tempOrgUserRelationInfo");
                    break;
                }
                pageIndex = pageIndex + 1;
                LOGGER.info("pageIndex is " + pageIndex + " tempOrgUserRelationInfo size is "
                    + tempOrgUserRelationInfo.size());
                orgUserRelationInfoList.addAll(tempOrgUserRelationInfo);
                if (tempOrgUserRelationInfo.size() < PAGE_SIZE)
                {
                    LOGGER.info("tempOrgUserRelationInfo is less than pageSize,break tempOrgUserRelationInfo size is "
                        + tempOrgUserRelationInfo.size());
                    break;
                }
                final Object lockObj = new Object();
                synchronized (lockObj)
                {
                    Thread.currentThread().sleep(SLEEP_TIME);
                }
            }
            cmbUserRelationInfoList = convertFromWsdl.convertToCMBUserRelationInfo(orgUserRelationInfoList);
        }
        catch (MalformedURLException e)
        {
            LOGGER.error("url exception" + e.toString());
            return null;
        }
        catch (CMBOAException e)
        {
            LOGGER.error(e.toString());
            retryTime = retryTime + 1;
            cmbUserRelationInfoList = getAllCMBUserRelationInfo(url, appCode, pToken, retryTime);
        }
        catch (ISyncServiceGetOrgUserRelationInfosFaultMessageFaultFaultMessage e)
        {
            LOGGER.error(e.toString());
            retryTime = retryTime + 1;
            cmbUserRelationInfoList = getAllCMBUserRelationInfo(url, appCode, pToken, retryTime);
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
            return null;
        }
        int cmbUserRelationInfoListSize = 0;
        if (null != cmbUserRelationInfoList)
        {
            cmbUserRelationInfoListSize = cmbUserRelationInfoList.size();
            
        }
        LOGGER.info("get all userRelationInfo end retryTime:" + retryTime + " userRelationInfoList size is "
            + cmbUserRelationInfoListSize);
        return cmbUserRelationInfoList;
    }
    
    private List<OrgInfo> getCMBOrgInfo(String appCode, String pToken, int pageIndex, ISyncService client)
        throws CMBOAException, ISyncServiceGetOrgInfosFaultMessageFaultFaultMessage
    {
        Holder<ArrayOfOrgInfo> getOrgInfosResult = new Holder<ArrayOfOrgInfo>();
        Holder<Integer> count = new Holder<Integer>();
        client.getOrgInfos(appCode, pToken, pageIndex, PAGE_SIZE, getOrgInfosResult, count);
        List<OrgInfo> tempOrgInfo = getOrgInfosResult.value.getOrgInfo();
        if (null == tempOrgInfo)
        {
            if (count.value != null && count.value != 0)
            {
                throw new CMBOAException("tempOrgInfo is null and count is not null count size:"
                    + count.value);
            }
            return null;
        }
        if (tempOrgInfo.size() != count.value)
        {
            throw new CMBOAException("select count is error tempOrgInfo size:" + tempOrgInfo.size()
                + " count size:" + count.value);
        }
        return tempOrgInfo;
    }
    
    private List<SapUser> getCMBOSapUser(String appCode, String pToken, int pageIndex, ISyncService client)
        throws CMBOAException, ISyncServiceGetUserInfoFaultMessageFaultFaultMessage
    {
        Holder<ArrayOfSapUser> getUserInfoResult = new Holder<ArrayOfSapUser>();
        Holder<Integer> count = new Holder<Integer>();
        client.getUserInfo(appCode, pToken, pageIndex, PAGE_SIZE, getUserInfoResult, count);
        List<SapUser> tempSaps = getUserInfoResult.value.getSapUser();
        if (null == tempSaps)
        {
            if (count.value != null && count.value != 0)
            {
                throw new CMBOAException("tempOrgInfo is null and count is not null count size:"
                    + count.value);
            }
            return null;
        }
        if (tempSaps.size() != count.value)
        {
            throw new CMBOAException("select count is error tempOrgInfo size:" + tempSaps.size()
                + " count size:" + count.value);
        }
        return tempSaps;
    }
    
    private List<OrgUserRelationInfo> getCMBUserRelationInfo(String appCode, String pToken, int pageIndex,
        ISyncService client) throws CMBOAException,
        ISyncServiceGetOrgUserRelationInfosFaultMessageFaultFaultMessage
    {
        Holder<ArrayOfOrgUserRelationInfo> getOrgUserRelationInfosResult = new Holder<ArrayOfOrgUserRelationInfo>();
        Holder<Integer> count = new Holder<Integer>();
        client.getOrgUserRelationInfos(appCode,
            pToken,
            pageIndex,
            PAGE_SIZE,
            getOrgUserRelationInfosResult,
            count);
        List<OrgUserRelationInfo> orgUserRelationInfos = getOrgUserRelationInfosResult.value.getOrgUserRelationInfo();
        if (null == orgUserRelationInfos)
        {
            if (count.value != null && count.value != 0)
            {
                throw new CMBOAException("orgUserRelationInfos is null and count is not null count size:"
                    + count.value);
            }
            return null;
        }
        if (orgUserRelationInfos.size() != count.value)
        {
            throw new CMBOAException("select count is error orgUserRelationInfos size:"
                + orgUserRelationInfos.size() + " count size:" + count.value);
        }
        return orgUserRelationInfos;
    }
}
