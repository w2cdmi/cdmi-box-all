package pw.cdmi.box.disk.client.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.node.RestFileLabelRequest;
import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.doctype.domain.RestDocTypeList;
import pw.cdmi.box.disk.filelabel.dto.BaseFilelabelResponseDto;
import pw.cdmi.box.disk.filelabel.dto.EntityFilelabelResponseDto;
import pw.cdmi.box.disk.filelabel.dto.FileLabelRequestDto;
import pw.cdmi.box.disk.filelabel.dto.ListFilelabelResponseDto;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class FilelabelClient {
    private RestClient ufmClientService;
    
    private UserTokenManager userTokenManager;
    
    @PostConstruct
    void init() {
        userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
    }
    
    public FilelabelClient(RestClient ufmClientService) {
        this.ufmClientService = ufmClientService;
    }
    
    /**
     * 获取默认的文件类型信息
     * 
     * @param user
     * @param ownerId
     * @param fileId
     * @return
     * @throws RestException
     */
    public RestDocTypeList getDefaultDoctype(UserToken user, long ownerId, long userId) throws RestException {
        StringBuilder sb = new StringBuilder(BasicConstants.UFM_RESOURCE_BIND_FILELABEL);
        sb.append("/").append(ownerId).append("/doctype");
        
        Map<String, String> headers;
        if (StringUtils.isNotBlank(user.getLinkCode())) {
            headers = assembleLink(user.getLinkCode());
        } else {
            headers = assembleToken();
        }
        
        TextResponse response = ufmClientService.performGetText(sb.toString(), headers);
        
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            String content = response.getResponseBody();
            if (StringUtils.isNotEmpty(content)) {
                RestDocTypeList docTypeDto = JsonUtils.stringToObject(content, RestDocTypeList.class);
                return docTypeDto;
            }
        }
        
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * 绑定文件标签
     * 
     * @param token
     * @param request
     * @param ownerId
     * @return
     * @throws RestException
     */
    public EntityFilelabelResponseDto bindFilelabel(String token, FileLabelRequestDto request, long ownerId)
        throws RestException {
        String uri = BasicConstants.UFM_RESOURCE_BIND_FILELABEL + "/" + ownerId + "/bindFilelabel";

        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, assembleToken(), request);
        
        String respContent = response.getResponseBody();
        if (StringUtils.isNotEmpty(respContent)) {
            return JsonUtils.stringToObject(response.getResponseBody(), EntityFilelabelResponseDto.class);
        } 
        
        return new EntityFilelabelResponseDto(HttpStatus.EXPECTATION_FAILED);
    }
    
    /**
     * 解除標簽的綁定
     * 
     * @param token
     * @param request
     * @param ownerId
     * @return
     * @throws RestException
     */
    public BaseFilelabelResponseDto unbindFilelabel(String token, RestFileLabelRequest request, long ownerId)
        throws RestException {
        String uri = BasicConstants.UFM_RESOURCE_BIND_FILELABEL + "/" + ownerId + "/unbindFilelabel";
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, assembleToken(), request);
        
        String respContent = response.getResponseBody();
        if (StringUtils.isNotEmpty(respContent)) {
            return JsonUtils.stringToObject(response.getResponseBody(), BaseFilelabelResponseDto.class);
        }
        
        return new ListFilelabelResponseDto(HttpStatus.EXPECTATION_FAILED);
    }
    
    /**
     * 列举企业标签
     * 
     * @param token
     * @param request
     * @param ownerId
     * @param parentId
     * @return
     * @throws RestException
     */
    public ListFilelabelResponseDto queryEnterpriseFilelabels(long ownerId, int labelType, int reqPage,
        int pageSize, String filelabelName) throws RestException {
        String uri = BasicConstants.UFM_RESOURCE_BIND_FILELABEL + "/" + ownerId + "/listFilelabels/"
            + labelType + "/" + reqPage + "/" + pageSize + "?filelabelName=" + filelabelName;
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, assembleToken(), null);
        
        String respContent = response.getResponseBody();
        if (StringUtils.isNotEmpty(respContent)) {
            return JsonUtils.stringToObject(response.getResponseBody(), ListFilelabelResponseDto.class);
        }
        
        return new ListFilelabelResponseDto(HttpStatus.EXPECTATION_FAILED);
    }
    
    /**
     * 列举用户最近使用标签信息
     * 
     * @param token
     * @param request
     * @param ownerId
     * @param parentId
     * @return
     * @throws RestException
     */
    public ListFilelabelResponseDto queryUserLatestViewedLabels(long ownerId) throws RestException {
        String uri = BasicConstants.UFM_RESOURCE_BIND_FILELABEL + "/" + ownerId + "/userLatestViewedLabels";
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, assembleToken(), null);
        
        String respContent = response.getResponseBody();
        if (StringUtils.isNotEmpty(respContent)) {
            return JsonUtils.stringToObject(response.getResponseBody(), ListFilelabelResponseDto.class);
        }
        
        return new ListFilelabelResponseDto(HttpStatus.EXPECTATION_FAILED);
    }
    
    protected Map<String, String> assembleLink(String linkCode) {
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        String accessCode = CommonTools.getAccessCode(linkCode);
        
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        String authStr = "link," + linkCode + ',' + SignatureUtils.getSignature(accessCode, dateStr);
        headers.put(RestConstants.HEADER_AUTHORIZATION, authStr);
        headers.put(RestConstants.HEADER_DATE, dateStr);
        return headers;
    }
    
    public UserTokenManager getUserTokenManager() {
        if (null == userTokenManager) {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }
    
    private Map<String, String> assembleToken() {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put(RestConstants.HEADER_AUTHORIZATION, getUserTokenManager().getToken());
        return headers;
    }
}
