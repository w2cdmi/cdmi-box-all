package pw.cdmi.box.disk.client.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.common.RestBaseObject;
import pw.cdmi.box.disk.client.domain.node.GetNodeByNameRequest;
import pw.cdmi.box.disk.client.domain.node.RenameAndSetSyncRequest;
import pw.cdmi.box.disk.client.domain.node.RestFileInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.RestNodeList;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class NodeClient
{
    
    private RestClient ufmClientService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeClient.class);
    
    public NodeClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public RestBaseObject getNodeInfo(String token, long ownerId, long nodeId) throws RestException
    {
        String uri = Constants.RESOURCE_NODE + '/' + ownerId + '/' + nodeId;
        
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", token);
        
        TextResponse response = ufmClientService.performGetText(uri, headerMap);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            return castTo(content);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * 
     * @param token
     * @param ownerId
     * @param nodeId
     * @return
     * @throws RestException
     */
    @SuppressWarnings("unchecked")
    public List<RestFolderInfo> getNodePath(String token, long ownerId, long nodeId) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_NODE);
        uri.append('/');
        uri.append(ownerId);
        uri.append('/');
        uri.append(nodeId);
        uri.append("/path");
        
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", token);
        
        TextResponse response = ufmClientService.performGetText(uri.toString(), headerMap);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            List<?> list = (ArrayList<?>) JsonUtils.stringToList(content,
                List.class,
                RestFolderInfo.class);
            return (List<RestFolderInfo>) list;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    public boolean isNodeExsit(String token, long ownerId, long parentId, GetNodeByNameRequest request)
        throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(BasicConstants.RESOURCE_FOLDER);
        uri.append('/');
        uri.append(ownerId);
        uri.append('/');
        uri.append(parentId);
        uri.append("/children");
        
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", token);
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(),
            headerMap,
            request);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            RestNodeList nodeList = JsonUtils.stringToObject(response.getResponseBody(), RestNodeList.class);
            List<RestFolderInfo> folderList = nodeList.getFolders();
            List<RestFileInfo> fileList = nodeList.getFiles();
            return CollectionUtils.isNotEmpty(folderList) || CollectionUtils.isNotEmpty(fileList);
        }
        return false;
    }
    
    public boolean isNodeExsitNoSelf(String token, long ownerId, long parentId, long nodeId,
        GetNodeByNameRequest request) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(BasicConstants.RESOURCE_FOLDER);
        uri.append('/');
        uri.append(ownerId);
        uri.append('/');
        uri.append(parentId);
        uri.append("/children");
        
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", token);
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(),
            headerMap,
            request);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            RestNodeList nodeList = JsonUtils.stringToObject(response.getResponseBody(), RestNodeList.class);
            List<RestFolderInfo> folderList = nodeList.getFolders();
            for (RestFolderInfo item : folderList)
            {
                if (item.getId() != nodeId)
                {
                    return true;
                }
            }
            List<RestFileInfo> fileList = nodeList.getFiles();
            for (RestFileInfo item : fileList)
            {
                if (item.getId() != nodeId)
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public void renameAndSetSync(String token, long ownerId, long nodeId, RenameAndSetSyncRequest request)
        throws RestException
    {
        String uri = Constants.RESOURCE_NODE + '/' + ownerId + '/' + nodeId;
        
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", token);
        TextResponse response = ufmClientService.performJsonPutTextResponse(uri, headerMap, request);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
    }
    
    public RestFolderLists search(String token, long ownerId, BasicNodeListRequest request) throws RestException
    {
        String uri = Constants.RESOURCE_NODE + '/' + ownerId + "/search";
        
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", token);
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestFolderLists list = JsonUtils.stringToObject(content, RestFolderLists.class);
            return list;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    private RestBaseObject castTo(String content)
    {
        try
        {
            RestFileInfo fileInfo = JsonUtils.stringToObject(content, RestFileInfo.class);
            return fileInfo;
        }
        catch (Exception e)
        {
            try
            {
                RestFolderInfo folderInfo = JsonUtils.stringToObject(content, RestFolderInfo.class);
                return folderInfo;
            }
            catch (Exception ex)
            {
                LOGGER.error("failed in change content to RestFolderInfo", ex);
                throw new InternalServerErrorException("failed in change content to RestFolderInfo",ex);
            }
        }
    }
    
    /**
     * 文件智能分类 web端
     * @param token
     * @param request
     * @param ownerId
     * @param parentId
     * @return
     * @throws RestException
     */
    public RestFolderLists listSearchByDocType(String token, BasicNodeListRequest request, Long ownerId, int doctype)
              throws RestException
     {
          // {doctype}/doctype/web/list
          String uri = Constants.RESOURCE_FOLDER + '/' + ownerId + '/' + doctype + "/doctype/web/search";

          Map <String , String> headerMap = new HashMap <String , String>(1);
          headerMap.put("Authorization" , token);

          TextResponse response = ufmClientService.performJsonPostTextResponse(uri , headerMap , request);

          RestFolderLists list = null;
          if (response.getStatusCode() == HttpStatus.OK.value())
          {
               String content = response.getResponseBody();
               list = JsonUtils.stringToObject(content , RestFolderLists.class);
               return list;
          }
          RestException exception = JsonUtils.stringToObject(response.getResponseBody() , RestException.class);
          throw exception;
     }
    
}
