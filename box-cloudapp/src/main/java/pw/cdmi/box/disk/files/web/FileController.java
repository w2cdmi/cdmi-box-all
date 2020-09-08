package pw.cdmi.box.disk.files.web;

import com.huawei.cs.json.JSONException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import pw.cdmi.box.disk.client.api.AccountConfigClient;
import pw.cdmi.box.disk.client.api.FileClient;
import pw.cdmi.box.disk.client.api.TeamSpaceClient;
import pw.cdmi.box.disk.client.api.domain.SecretStaff;
import pw.cdmi.box.disk.client.domain.node.*;
import pw.cdmi.box.disk.client.domain.user.RestAccountConfigList;
import pw.cdmi.box.disk.client.domain.user.RestUserConfig;
import pw.cdmi.box.disk.files.domain.FileSecretResponse;
import pw.cdmi.box.disk.httpclient.rest.response.FilePreUploadResponse;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.teamspace.domain.RestNodePermissionInfo;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(value = "/files")
public class FileController extends CommonController {
	private static final Logger         LOGGER                  = LoggerFactory.getLogger(FileController.class);
	
	private final static String         PAGE_PARENT_MYSPACE     = "myspace";
	
	private final static String         PAGE_PARENT_TEAMSPACE   = "teamspace";
	
	private final static String         PAGE_PARENT_SHARE       = "receiveShare";
	
	private static final String         PAGE_VERSION_LIST_ERROR = "files/versionListException";
	
	private final static String         SHOW_DELETE             = "showDel";
	
	private final static String         SHOW_DOWNLOAD           = "showDownload";
	
	private final static String         SHOW_LINK               = "showLink";
	
	private final static String         SHOW_SHARE              = "showShare";
	
	private final static String         LINK_CODE               = "linkCode";
	
	private static final int            VERSION_PAGE_SIZE       = 5;
	
	private FileClient                  fileClient;
	
	private TeamSpaceClient             teamSpaceHttpClient;
	
	@Resource
	private RestClient                  ufmClientService;
	
	@Resource
	private RestClient                  uamClientService;
	
	private AccountConfigClient         accountConfigClient;
	
	@Autowired
	@Qualifier("messageSource")
	private ResourceBundleMessageSource messageSource;
	
	@Autowired
	private UserService userService;
	
	/**
	 * Enter the upload page
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "upload/{ownerId}/{parentId}", method = RequestMethod.GET)
	public String enterUpload(@PathVariable("ownerId") long ownerId, @PathVariable("parentId") Long parentId, Model model) {
		model.addAttribute("ownerId", ownerId);
		model.addAttribute("parentId", parentId);
		return "files/upload";
	}

	@RequestMapping(value = "onlineEdit/{ownerId}/{parentId}/{fileId}", method = RequestMethod.GET)
	public String onlineEdit(@PathVariable("ownerId") long ownerId, @PathVariable("parentId") long parentId, @PathVariable("fileId") long fileId, Model model) {
		model.addAttribute("ownerId", ownerId);
		model.addAttribute("parentId", parentId);
		model.addAttribute("fileId", fileId);
		return "files/onlineEdit";
	}


	/**
	 * Gets the file download address
	 */
	@RequestMapping(value = "getDownloadUrl/{ownerId}/{fileId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> getDownloadUrl(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId) {
		try {
            String url = HtmlUtils.htmlEscape(fileClient.getDownloadUrl(getToken(), ownerId, fileId).getDownloadUrl());
            return new ResponseEntity<>(url, HttpStatus.OK);
		} catch (RestException e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getCode(), HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Gets the file download address
	 */
	@RequestMapping(value = "/history/getDownloadUrl/{ownerId}/{fileId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> getDownloadUrlForHistory(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId) {
		try {
			String url = HtmlUtils.htmlEscape(fileClient.getDownloadUrlForHistory(getToken(), ownerId, fileId).getDownloadUrl());
			return new ResponseEntity<>(url, HttpStatus.OK);
		} catch (RestException e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getCode(), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	
	/**
	 * 获取下载地址和添加最近浏览记录
	 */
	@RequestMapping(value = "getUrlAndBrowse/{ownerId}/{fileId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> getUrlAndBrowse(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId) {
		try {
			String url = HtmlUtils.htmlEscape(fileClient.getUrlAndBrowse(getToken(), ownerId, fileId).getDownloadUrl());
			return new ResponseEntity<>(url, HttpStatus.OK);
		} catch (RestException e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getCode(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(value = "getPreviewMeta/{ownerId}/{fileId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<PreviewMetaResponse> getPreviewMeta(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId) {
		FileController.LOGGER.info("Enter PreviewMeta/" + ownerId + "/" + fileId);
		
		PreviewMetaResponse response = fileClient.getPreviewMeta(getToken(), ownerId, fileId);
		FileController.LOGGER.info("PreviewMetaResponse = {}", response);
		
		return new ResponseEntity<PreviewMetaResponse>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "getPreviewUrl/{ownerId}/{fileId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<PreviewUrlResponse> getPreviewUrl(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId) {
		FileController.LOGGER.info("Enter PreviewUrl/" + ownerId + "/" + fileId);
		
		PreviewUrlResponse response = fileClient.getPreviewUrl(getToken(), ownerId, fileId);
		FileController.LOGGER.info("PreviewUrlResponse = {}", response);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Get file thumbnails
	 *
	 * @param ownerId
	 * @param fileId
	 */
	@RequestMapping(value = "getThumbnailUrlSmall/{ownerId}/{fileId}", method = RequestMethod.GET)
	public ResponseEntity<String> getThumbnailUrl(@PathVariable("ownerId") Long ownerId, @PathVariable("fileId") Long fileId) {
		String url = fileClient.getFileThumbUrls(ownerId, fileId);
		return new ResponseEntity<>(HtmlUtils.htmlEscape(url), HttpStatus.OK);
	}

	@RequestMapping(value = "gotoPreview/{ownerId}/{nodeId}", method = RequestMethod.GET)
	public String gotoPreview(@PathVariable("ownerId") long ownerId, @PathVariable("nodeId") long fileId, Model model, HttpServletRequest request) {
		model.addAttribute("ownerId", ownerId);
		model.addAttribute("nodeId", fileId);

		try {
			RestFileInfo file = fileClient.getFileInfo(getCurrentUser(), ownerId, fileId);
			if(file != null) {
				model.addAttribute("name", file.getName());
			}
		} catch (RestException e) {
			LOGGER.warn("Failed to query the file info: ownerId={}, nodeId={}", ownerId, fileId);
			LOGGER.warn("Failed to query the file info:", e);
		}

		return "files/previewReader";
	}

	private void processCode(RestException e, Model model, Locale locale) {
		String code = e.getCode();
		switch (code) {
			case "FileConverting": {
				model.addAttribute("style", "preview-converting");
				model.addAttribute("errMsg", messageSource.getMessage("preview.status.creating", null, locale));
				break;
			}
			case "FileConvertFailed": {
				model.addAttribute("style", "preview-failed");
				model.addAttribute("errMsg", messageSource.getMessage("preview.status.failed", null, locale));
				break;
			}
			case "FileConvertNotSupport": {
				model.addAttribute("style", "preview-failed");
				model.addAttribute("errMsg", messageSource.getMessage("preview.status.unsupport", null, locale));
				break;
			}
			case "FileScanning": {
				model.addAttribute("style", "preview-converting");
				model.addAttribute("errMsg", messageSource.getMessage("file.status.scanning", null, locale));
				break;
			}
			case "ScannedForbidden": {
				model.addAttribute("style", "preview-failed");
				model.addAttribute("errMsg", messageSource.getMessage("preview.status.forbidden", null, locale));
				break;
			}
			case "SecurityMatrixForbidden":
			case "Forbidden":
			case "Unauthorized": {
				model.addAttribute("style", "preview-failed");
				model.addAttribute("errMsg", messageSource.getMessage("link.view.Forbidden", null, locale));
				break;
			}
			case "LinkExpired":
			case "NoSuchLink": {
				model.addAttribute("style", "preview-failed");
				model.addAttribute("errMsg", messageSource.getMessage("link.view.NoSuchItems", null, locale));
				break;
			}
			default: {
				LOGGER.warn("get preview file url, return code is " + code + ", message is " + e.getMessage());
				model.addAttribute("style", "preview-failed");
				model.addAttribute("errMsg", messageSource.getMessage("preview.status.unsupport", null, locale));
				break;
			}
		}
	}

	/**
	 * Enter the file version of the list page
	 *
	 * @param model
	 */
	@RequestMapping(value = "listVersion/{ownerId}/{nodeId}", method = RequestMethod.GET)
	public String gotoVersionList(@PathVariable("ownerId") Long ownerId, @PathVariable("nodeId") Long fileId, Model model, String parentPageType) {
		try {
			UserToken user = getCurrentUser();
			RestFileInfo fileInfo = fileClient.getFileInfo(user, ownerId, fileId);
			if (null == fileInfo) {
				return PAGE_VERSION_LIST_ERROR;
			}
			showVersionOp(fileInfo, getCurrentUser(), model, parentPageType);
			model.addAttribute("ownerId", ownerId);
			model.addAttribute("nodeId", fileInfo.getId());
			return "files/versionList";
		} catch (RestException e) {
			return PAGE_VERSION_LIST_ERROR;
		}

	}

	/**
	 * File version
	 *
	 * @param nodeId
	 * @param pageNumber
	 * @param orderField
	 * @param desc
	 */
	@RequestMapping(value = "listVersion", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Page<RestFileVersionInfo>> listVersion(Long ownerId, Long nodeId, int pageNumber, String orderField, boolean desc, HttpServletRequest request) {
		super.checkToken(request);
		PageRequest pageRequest = new PageRequest(pageNumber, VERSION_PAGE_SIZE);
		pageRequest.setOrder(new Order(orderField, desc));

		long offset = 0L;
		if (pageNumber > 0) {
			offset = (long) (pageNumber - 1) * VERSION_PAGE_SIZE;
		}

		RestVersionLists versionList = fileClient.listVersion(getToken(), ownerId, nodeId, offset, VERSION_PAGE_SIZE);
		List<RestFileVersionInfo> content = transToNodeList(versionList);
		Page<RestFileVersionInfo> page = new PageImpl<>(content, pageRequest, (int)versionList.getTotalCount());
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	/**
	 * @param ownerId
	 * @param nodeId
	 */
	@RequestMapping(value = "restoreVersion", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> restoreVersion(Long ownerId, Long nodeId, HttpServletRequest request) {
		super.checkToken(request);
		RestFileInfo fileInfo = fileClient.restoreVersion(getToken(), ownerId, nodeId);
		if (null != fileInfo) {
			fileInfo.setDescription(HtmlUtils.htmlEscape(fileInfo.getDescription()));
			fileInfo.setMender(HtmlUtils.htmlEscape(fileInfo.getMender()));
			fileInfo.setMenderName(HtmlUtils.htmlEscape(fileInfo.getMenderName()));
			fileInfo.setName(HtmlUtils.htmlEscape(fileInfo.getName()));
		}
		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	/**
	 * @param parentId
	 * @param name
	 */
	@RequestMapping(value = "preUpload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> preUpload(long ownerId, long parentId, String name,String path, long size, HttpServletRequest httpServletRequest) {
		super.checkToken(httpServletRequest);
		int i = 1;
		String uri = BasicConstants.RESOURCE_FILE + '/' + ownerId;
		Map<String, String> headerMap = new HashMap<>(1);
		headerMap.put("Authorization", getToken());
		FilePreUploadRequest request = new FilePreUploadRequest(name, parentId, size);
		request.setPath(path);
		TextResponse response;
		int status;
		while (true) {
			response = ufmClientService.performJsonPutTextResponse(uri, headerMap, request);
			
			status = response.getStatusCode();
			if (status == HttpStatus.OK.value()) {
				String content = response.getResponseBody();
				return new ResponseEntity<>(JsonUtils.stringToObject(content, FilePreUploadResponse.class).getUploadUrl(), HttpStatus.OK);
			} else if (status == HttpStatus.CONFLICT.value()) {
				String newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, name, i);
				request.setName(newName);
				i++;
			} else if (status == HttpStatus.BAD_REQUEST.value()) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} else if (status == HttpStatus.FORBIDDEN.value()) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			} else if (status == HttpStatus.PRECONDITION_FAILED.value()) {
				return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
			} else {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostConstruct
	void init() {
		this.fileClient = new FileClient(ufmClientService);
		this.teamSpaceHttpClient = new TeamSpaceClient(ufmClientService);
		this.accountConfigClient = new AccountConfigClient(uamClientService);
	}

	private void showVersionOp(RestFileInfo fileInfo, UserToken user, Model model, String parentPageType) {
		if (StringUtils.equals(PAGE_PARENT_MYSPACE, parentPageType)) {
			model.addAttribute(SHOW_DELETE, 1);
			model.addAttribute(SHOW_DOWNLOAD, 1);
			return;
		}

		if (StringUtils.equals(PAGE_PARENT_TEAMSPACE, parentPageType) || StringUtils.equals(PAGE_PARENT_SHARE, parentPageType)) {
			RestNodePermissionInfo pInfo = teamSpaceHttpClient.getNodePermission(fileInfo.getOwnedBy(), fileInfo.getId(), user.getCloudUserId());

			model.addAttribute(SHOW_DELETE, 0);
			model.addAttribute(SHOW_DOWNLOAD, 0);

			if (pInfo != null) {
				if ((pInfo.getPermissions().getEdit() == 1 || pInfo.getPermissions().getDelete() == 1)) {
					model.addAttribute(SHOW_DELETE, 1);
				}
				if (pInfo.getPermissions().getDownload() == 1) {
					model.addAttribute(SHOW_DOWNLOAD, 1);
				}
			}
			return;
		}
		model.addAttribute(SHOW_DELETE, 0);
		model.addAttribute(SHOW_DOWNLOAD, 0);
	}

	private void showLinkAndShare(Model model, String parentPageType, String linkCode) {
		if (StringUtils.isBlank(linkCode)) {
			model.addAttribute(LINK_CODE, "");
		} else {
			model.addAttribute(LINK_CODE, linkCode);
		}
		if (StringUtils.equals(PAGE_PARENT_MYSPACE, parentPageType)) {
			model.addAttribute(SHOW_LINK, true);
			model.addAttribute(SHOW_SHARE, true);
			return;
		}
		if (StringUtils.equals(PAGE_PARENT_TEAMSPACE, parentPageType)) {
			model.addAttribute(SHOW_LINK, true);
			model.addAttribute(SHOW_SHARE, false);
			return;
		}
		model.addAttribute(SHOW_LINK, false);
		model.addAttribute(SHOW_SHARE, false);
	}

	private List<RestFileVersionInfo> transToNodeList(RestVersionLists list) {
		List<RestFileVersionInfo> content = new ArrayList<>(list.getTotalCount());
		for (RestFileVersionInfo versionInfo : list.getVersions()) {
			versionInfo.setMender(HtmlUtils.htmlEscape(versionInfo.getMender()));
			versionInfo.setMenderName(HtmlUtils.htmlEscape(versionInfo.getMenderName()));
			versionInfo.setName(HtmlUtils.htmlEscape(versionInfo.getName()));
			versionInfo.setVersion(HtmlUtils.htmlEscape(versionInfo.getVersion()));
			if(versionInfo.getCreatedBy()!=0){
				User createUser = userService.getUserByCloudUserId(versionInfo.getCreatedBy());
		        if(createUser != null){
		        	versionInfo.setCreatedByName(HtmlUtils.htmlEscape(createUser.getName()));
		        }
			}
			content.add(versionInfo);
		}
		return content;
	}
    /**
     * 得到文件詳情信息
     * @param ownerId
     * @param fileId
     * @return
     */
    @RequestMapping(value = "getFileDetail/{ownerId}/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<RestFileInfo> getFileDetail(@PathVariable("ownerId") Long ownerId,
        @PathVariable("fileId") Long fileId)
    {
        UserToken user = getCurrentUser();
        RestFileInfo fileInfo = fileClient.getFileInfo(user, ownerId, fileId);
        
        if(fileInfo.getCreatedBy()!=0){
			User createUser = userService.getUserByCloudUserId(fileInfo.getCreatedBy());
	        if(createUser != null){
	        	fileInfo.setCreatedByName(createUser.getName());
	        }
		}
        
        User modifiedUser = userService.getUserByCloudUserId(fileInfo.getModifiedBy());
        if(modifiedUser != null){
        	fileInfo.setModifiedByName(modifiedUser.getName());
        }
        return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
    }
    
    /**
     * 得到文件操作权限
     * @param ownerId
     * @param fileId
     * @return
     * @throws JSONException 
     */
    @RequestMapping(value = "getFilePermisson/{ownerId}/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<?> getFilePermisson(@PathVariable("ownerId") Long ownerId,
        @PathVariable("fileId") Long fileId,@RequestParam("secretLevel") byte secretLevel) throws JSONException
    {
        UserToken user = getCurrentUser();
        FileSecretResponse  result=new FileSecretResponse();
        RestAccountConfigList restAccountConfigList=accountConfigClient.getAccountConfig(user.getAccountId(), user.getToken());
        Map<String, Object>  configs=new HashMap<>();
        for(RestUserConfig restUserConfig:restAccountConfigList.getConfigs()){
        	configs.put(restUserConfig.getName(), restUserConfig.getValue());
        }
        if(configs.get("customer.storbox.doc.secretlevel.enable")!=null&&configs.get("customer.storbox.doc.secretlevel.enable").toString().equals("true")){
          
           @SuppressWarnings("unchecked")
		List<SecretStaff>  list=	(List<SecretStaff>) accountConfigClient.getStaffSecret(user.getAccountId(),user.getToken());
           for(int i=0;i<list.size();i++){
        	   SecretStaff secretStaff=list.get(i);
        	   if(secretLevel==secretStaff.getSecretLevel()){
        		   if(user.getStaffLevel()<secretStaff.getStaffLevel()){
        			   result.setVisite(false);
        		   }
        	   }
           }
        }
        
        if(configs.get("customer.storbox.doc.secretdoc.staff.creator.enable")!=null&&configs.get("customer.storbox.doc.secretdoc.staff.creator.enable").toString().equals("true")){
        	byte creatorSecretlevel=Byte.parseByte(configs.get("customer.storbox.doc.secretdoc.staff.creator.secretlevel").toString());
            if(user.getStaffLevel()<=creatorSecretlevel){
            	result.setSecretSet(true);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    
    /**
     * 得到文件操作权限
     * @param ownerId
     * @param fileId
     * @return
     * @throws JSONException 
     */
    @RequestMapping(value = "updateSecretLevel/{ownerId}/{fileId}", method = RequestMethod.POST)
    public ResponseEntity<?> updateFileSecretLevel(@PathVariable("ownerId") Long ownerId,
        @PathVariable("fileId") Long fileId,HttpServletRequest request) throws JSONException
    {
        UserToken user = getCurrentUser();
        String secretLevel=request.getParameter("secretLevel");
        fileClient.updateFileSecretLevel(ownerId,fileId,secretLevel,user.getToken());
        return new ResponseEntity<>( HttpStatus.OK);
    }
    
	@RequestMapping(value = "gotoFileInfo/{ownerId}/{nodeId}", method = RequestMethod.GET)
	public String gotoFileInfo(@PathVariable("ownerId") long ownerId, @PathVariable("nodeId") long fileId,String name, Model model) {
		model.addAttribute("ownerId", ownerId);
		model.addAttribute("fileId", fileId);
		model.addAttribute("name", name);
		return "files/fileInfo";
	}

	@RequestMapping(value = "search", method = RequestMethod.GET)
	public String search(@RequestParam String type, Model model) {
        model.addAttribute("type", type);

		return "files/search";
	}
}
