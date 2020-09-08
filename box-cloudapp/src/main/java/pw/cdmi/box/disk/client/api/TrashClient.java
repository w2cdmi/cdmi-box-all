package pw.cdmi.box.disk.client.api;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.RestoreTrashRequest;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class TrashClient extends BaseClient
{
    private static final String RESOURCE_TRASH = "/api/v2/trash";
    
    public TrashClient(RestClient clientService)
    {
        super(clientService);
    }
    
    /**
     * 
     * @param token
     * @param ownerId
     * @param limit
     * @param offset
     * @param thumbnails
     * @param orders
     * @return
     */
    public RestFolderLists listTrashItems(String token, long ownerId, BasicNodeListRequest listFolderRequest) throws RestException
    {
        String uri = RESOURCE_TRASH + '/' + ownerId;
        
        TextResponse response = this.getClientService().performJsonPostTextResponse(uri,
            createHeader(token),
            listFolderRequest);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return JsonUtils.stringToObject(response.getResponseBody(), RestFolderLists.class);
        }
        
        RestException restEx = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw restEx;
    }
    
    /**
     * Delete the specified recycle bin contents
     * 
     * @param token
     * @param ownerId
     * @param nodeId
     * @throws RestException
     */
    public void deleteTrashItem(String token, long ownerId, long nodeId) throws RestException
    {
        String uri = RESOURCE_TRASH + '/' + ownerId + '/' + nodeId;
        TextResponse response = this.getClientService().performDelete(uri, createHeader(token));
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException restEx = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
            throw restEx;
        }
    }
    
    /**
     * Reduction of designated recycling station content
     * 
     * @param token
     * @param ownerId
     * @param nodeId
     * @param destFolderId
     * @param autoRename
     * @throws RestException
     */
    public void restoreTrashItem(String token, long ownerId, long nodeId, Long destFolderId,
        boolean autoRename) throws RestException
    {
        RestoreTrashRequest request = new RestoreTrashRequest(destFolderId, autoRename);
        String uri = RESOURCE_TRASH + '/' + ownerId + '/' + nodeId;
        TextResponse response = this.getClientService().performJsonPutTextResponse(uri,
            createHeader(token),
            request);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException restEx = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
            throw restEx;
        }
    }
}
