package pw.cdmi.box.disk.client.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.node.CreateFolderRequest;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.files.domain.Shortcut;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.box.domain.Page;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class FolderClient
{
    
    private RestClient ufmClientService;
    
    private UserTokenManager userTokenManager;
    
    public FolderClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    @PostConstruct
    void init()
    {
        userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
    }
    
    public RestFolderInfo createFolder(String token, CreateFolderRequest request, long ownerId)
        throws RestException
    {
        String uri = BasicConstants.RESOURCE_FOLDER + '/' + ownerId;
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);
        
        if (response.getStatusCode() != HttpStatus.CREATED.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), RestFolderInfo.class);
    }
    
    public RestFolderInfo getFolderInfo(String token, long ownerId, long folderId) throws RestException
    {
        String uri = BasicConstants.RESOURCE_FOLDER + '/' + ownerId + '/' + folderId;
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        
        TextResponse response = ufmClientService.performGetText(uri, headerMap);
        String content = response.getResponseBody();
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            RestFolderInfo folderInfo = JsonUtils.stringToObject(content, RestFolderInfo.class);
            return folderInfo;
        }
        RestException exception = JsonUtils.stringToObject(content, RestException.class);
        throw exception;
    }
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param nodeID
     * @return
     * @throws RestException
     */
    public RestFolderInfo getNodeInfo(UserToken user, long ownerId, long nodeID) throws RestException
    {
        Map<String, String> headers = assembleToken();
        if (StringUtils.isNotBlank(user.getLinkCode()))
        {
            String accessCode = CommonTools.getAccessCode(user.getLinkCode());
            String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
            String authStr = "link," + user.getLinkCode() + ','
                + SignatureUtils.getSignature(accessCode, dateStr);
            headers.put("Authorization", authStr);
            headers.put("Date", dateStr);
        }
        
        String path = Constants.NODES_API_PATH + ownerId + '/' + nodeID;
        
        TextResponse response = ufmClientService.performGetText(path, headers);
        if (response.getStatusCode() == 200)
        {
            RestFolderInfo restFolderInfo = JsonUtils.stringToObject(response.getResponseBody(),
                RestFolderInfo.class);
            return restFolderInfo;
        }
        
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    public RestFolderLists listFolder(String token, BasicNodeListRequest request, Long ownerId, long parentId)
        throws RestException
    {
        String uri = BasicConstants.RESOURCE_FOLDER + '/' + ownerId + '/' + parentId + "/items";
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            return JsonUtils.stringToObject(content, RestFolderLists.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    
    
    /**
     * 外链页面,获取节点下的文件夹
     * @param token
     * @param request
     * @param ownerId
     * @param parentId
     * @param linkCode
     * @return
     * @throws RestException
     */
    public RestFolderLists listFolderForLink(String token, BasicNodeListRequest request, Long ownerId, long parentId,String linkCode)
            throws RestException
        {
            String uri = BasicConstants.RESOURCE_FOLDER + '/' + ownerId + '/' + parentId + "/items"+"/link";
            Map<String, String> headerMap = new HashMap<String, String>(1);
            
            String accessCode = CommonTools.getAccessCode(linkCode);
            
            String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
            String authStr = "link," + linkCode + ',' + SignatureUtils.getSignature(accessCode, dateStr);
            
            headerMap.put("Authorization", authStr);
            headerMap.put("Date", dateStr);
            
            TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);
            
            if (response.getStatusCode() == HttpStatus.OK.value())
            {
                String content = response.getResponseBody();
                return JsonUtils.stringToObject(content, RestFolderLists.class);
            }
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
            throw exception;
        }
    
    /**
     * 
     * @param listFolderRequest
     * @param user
     * @return
     * @throws BaseRunException
     * @throws RestException
     */
    public RestFolderLists listNodesbyFilter(BasicNodeListRequest listFolderRequest, INode node,
        UserToken user) throws BaseRunException, RestException
    {
        String uri = BasicConstants.RESOURCE_FOLDER + '/' + node.getOwnedBy() + '/' + node.getId() + "/items";
        Map<String, String> headers = new HashMap<String, String>(2);
        if (StringUtils.isNotBlank(user.getLinkCode()))
        {
            String acessCode = CommonTools.getAccessCode(user.getLinkCode());
            String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
            String authStr = "link," + user.getLinkCode() + ','
                + SignatureUtils.getSignature(acessCode, dateStr);
            headers.put("Authorization", authStr);
            headers.put("Date", dateStr);
        }
        else
        {
            headers = assembleToken();
        }
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headers, listFolderRequest);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            return JsonUtils.stringToObject(content, RestFolderLists.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", getUserTokenManager().getToken());
        return headers;
    }
    
    public UserTokenManager getUserTokenManager()
    {
        if (null == userTokenManager)
        {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }
    
    
    /**
     * 文件智能分类 web端
     * @param token
     * @param request
     * @param ownerId
     * @return
     * @throws RestException
     */
    public RestFolderLists listByDocType(String token, BasicNodeListRequest request, Long ownerId, int doctype)
              throws RestException
     {
          // {doctype}/doctype/web/list
          String uri = Constants.RESOURCE_FOLDER + '/' + ownerId + '/' + doctype + "/doctype/web/list";

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



	public RestFolderLists listFolderForRecent(String token, long ownerId,BasicNodeListRequest request) {
		// TODO Auto-generated method stub
		String uri = Constants.RESOURCE_FOLDER + '/' + ownerId + "/recent";
        Map <String , String> headerMap = new HashMap <String , String>(1);
        headerMap.put("Authorization" , token);

        TextResponse response = ufmClientService.performJsonPostTextResponse(uri , headerMap ,request);

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

	public List<Shortcut> listFolderForShortcut(String token, long ownerId) {
		// TODO Auto-generated method stub
		String uri = Constants.RESOURCE_FOLDER + '/' + ownerId + "/shortcut/list";
		Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri , headers,"");

        List<Shortcut> list = null;
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
             String content = response.getResponseBody();
             list=(List<Shortcut>) JsonUtils.stringToList(content,List.class, Shortcut.class);
             return list;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody() , RestException.class);
        throw exception;
	}

	public void createShortcut(Shortcut shortcut) {
		// TODO Auto-generated method stub
		String uri = Constants.RESOURCE_FOLDER + '/' + shortcut.getCreateBy() + "/shortcut/create";
		Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri , headers,shortcut);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
             return ;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody() , RestException.class);
        throw exception;
	}

	public void deleteShortcut(UserToken user, long id) {
		// TODO Auto-generated method stub
		String uri = Constants.RESOURCE_FOLDER + '/' + user.getId() + "/shortcut/"+id;
		Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performDelete(uri, headers);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
             return ;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody() , RestException.class);
        throw exception;
	}

	public void deleteRecent(UserToken user, long ownerId, long nodeId) {
		// TODO Auto-generated method stub
		String uri = Constants.RESOURCE_FOLDER + '/' + ownerId + "/recent/delete/"+nodeId;
		Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performDelete(uri, headers);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
             return ;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody() , RestException.class);
        throw exception;
	}

	public RestFolderLists listReciveFolder(String token,long createdBy, BasicNodeListRequest request, Long ownerId,
			Long parentId) {
        String uri = BasicConstants.RESOURCE_FOLDER + '/' + ownerId +'/'+createdBy+ '/' + parentId + "/items";
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            return JsonUtils.stringToObject(content, RestFolderLists.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }

    public RestFolderInfo getInboxFolder(String token, Long ownerId) {

        String uri = BasicConstants.RESOURCE_FOLDER + '/' + ownerId +'/' +"getInboxFolder";

        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);

        TextResponse response = ufmClientService.performGetText(uri, headerMap);

        if (response.getStatusCode() == HttpStatus.OK.value()||response.getStatusCode()==HttpStatus.CREATED.value())
        {
            String content = response.getResponseBody();
            return JsonUtils.stringToObject(content, RestFolderInfo.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
}
