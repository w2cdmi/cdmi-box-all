package pw.cdmi.box.disk.uploadFolder.web;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import pw.cdmi.box.disk.client.api.FolderClient;
import pw.cdmi.box.disk.client.domain.node.CreateFolderRequest;
import pw.cdmi.box.disk.client.domain.node.FilePreUploadRequest;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.files.web.TreeNode;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.response.FilePreUploadResponse;
import pw.cdmi.box.disk.uploadFolder.domain.ActiveXFileInfo;
import pw.cdmi.box.disk.uploadFolder.domain.ActiveXFileInfoResp;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

/**
 * 
 * 文件夹上传处理类
 * 
 * Project Name:cloudapp_cmb_v1
 * 
 * File Name:UploadFolderController.java
 * 
 * 
 * 修改时间：2016年7月21日 下午2:24:53
 */
@Controller
@RequestMapping(value = "/uploadFolder")
public class UploadFolderController extends CommonController {

	/**
	 * 单线程信息管理
	 */
	public static ThreadLocal<String> FLY_DRAGON = new ThreadLocal<>();

	/**
	 * 日志记录
	 */
	private static Log logger = LogFactory.getLog(UploadFolderController.class);

	/**
	 * 查询文件夹最大数量
	 */
	private static final int MAX_TREE_SIZE = 1000;

	/**
	 * 反斜杠，用于解析activeX插件获取的文件路径
	 */
	private static final String BACK_SLASH = "\\";

	/**
	 * 反斜杠，用于解析activeX插件获取的文件路径
	 */
	private static final String DOUBLE_BACK_SLASH = "\\\\";

	/**
	 * 斜杠，用于解析谷歌浏览器获取的文件路径
	 */
	private static final String SLASH = "/";

	/**
	 * 文件夹处理客户端
	 */
	private FolderClient folderClient;

	@Resource
	private RestClient ufmClientService;

	@PostConstruct
	public void init() {
		folderClient = new FolderClient(ufmClientService);
	}
	
	/**
	 * 
	 * 跳转到上传页面
	 * 
	 * 
	 */
	@RequestMapping(value = "/getUploadFilePage", method = RequestMethod.GET)
	public String getUploadFilePage(){
		return "upload/uploadFileList";
	}

	/**
	 * 
	 * 文件夹上传预上传,IE版本
	 * 
	 * @param request
	 * @param ownerId
	 * @param parentId
	 * @return <参数描述>
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "folderPreupload/{ownerId}/{parentId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> dirPreUploadService(HttpServletRequest request, @PathVariable long ownerId,
			@PathVariable long parentId) {

		String strBody = UploadFolderController.FLY_DRAGON.get();

		try {
			strBody = strBody.substring(strBody.indexOf("["), strBody.indexOf("]") + 1);

		} catch (Exception e) {
			logger.error("fail to get requestBody!", e);
		}

		List<ActiveXFileInfo> fileList = new ArrayList<ActiveXFileInfo>();

		fileList = (List<ActiveXFileInfo>) JsonUtils.stringToList(strBody, ArrayList.class, ActiveXFileInfo.class);

		String uploadUrl = "";

		List<ActiveXFileInfoResp> fileInfoResp = new ArrayList<ActiveXFileInfoResp>();

		for (ActiveXFileInfo file : fileList) {
			ActiveXFileInfoResp resp = new ActiveXFileInfoResp();
			resp.setFileId(file.getFileId());

			if (file.getSize() == 0) {
				resp.setUploadUrl("");
			} else {
				uploadUrl = getUploadUrl(file.getFileName(), ownerId, parentId, file.getSize());
				resp.setUploadUrl(uploadUrl + "?objectLength=" + file.getSize());
			}

			// resp.setUploadUrl("");
			fileInfoResp.add(resp);
		}

		String response = this.toJson(fileInfoResp);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String viewUploadMainPage(){
		return "main";
	}

	/**
	 * 
	 * 获取文件上传地址
	 * 
	 * @param name
	 * @param ownerId
	 * @param parentId
	 * @param fileSize
	 * @return <参数描述>
	 * 
	 */
	private String getUploadUrl(String name, long ownerId, long parentId, long fileSize) {

		// 兼容插件和h5获取的文件路径信息
		if (name.contains(UploadFolderController.BACK_SLASH)) {
			name = name.replaceAll(UploadFolderController.DOUBLE_BACK_SLASH, UploadFolderController.SLASH);
		}

		name = name.startsWith(UploadFolderController.SLASH) ? name.substring(1, name.length()) : name;

		String[] folderName = name.split(UploadFolderController.SLASH);

		int start = name.lastIndexOf(UploadFolderController.SLASH);

		String fileName = name.substring(start + 1);

		boolean dirExist = false;

		for (int i = 0; i < folderName.length - 1; i++) {
			dirExist = false;
			// 查询所有文件夹
			List<TreeNode> treeNodeList = findFolder(ownerId, parentId, getToken());

			for (TreeNode node : treeNodeList) {
				if (folderName[i].equals(node.getName())) {
					dirExist = true;
					parentId = node.getId();
					break;
				}
			}
			if (!dirExist) {
				parentId = createMkir(folderName[i], parentId, getToken(), ownerId);
			}
		}

		String uploadUrl = dirPreUpload(fileName, parentId, ownerId, fileSize);

		return uploadUrl;
	}

	/**
	 * 
	 * 预上传
	 * 
	 * @param fileName
	 * @param parentId
	 * @param ownerId
	 * @param size
	 * @return <参数描述>
	 * 
	 */
	private String dirPreUpload(String fileName, long parentId, long ownerId, long size) {
		INode node = new INode();
		node.setParentId(parentId);
		node.setName(fileName);
		node.setSize(size);
		node.setType(INode.TYPE_FILE);
		node.setOwnedBy(ownerId);
		int i = 1;
		String uri = Constants.RESOURCE_FILE + '/' + ownerId;

		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", getToken());
		FilePreUploadRequest request = new FilePreUploadRequest(fileName, parentId, size);
		TextResponse response = null;
		int status = -1;
		while (true) {
			response = ufmClientService.performJsonPutTextResponse(uri, headerMap, request);
			status = response.getStatusCode();
			if (status == HttpStatus.OK.value()) {
				String content = response.getResponseBody();
				FilePreUploadResponse preUploadRsp = JsonUtils.stringToObject(content, FilePreUploadResponse.class);

				return preUploadRsp.getUploadUrl();
			} else if (status == HttpStatus.CONFLICT.value()) {
				String newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, fileName, i);
				request.setName(newName);
				i++;
				continue;
			} else if (status == HttpStatus.BAD_REQUEST.value()) {
				return "";
			} else if (status == HttpStatus.FORBIDDEN.value()) {
				return "";
			} else if (status == HttpStatus.PRECONDITION_FAILED.value()) {
				return "";
			}
			return "";
		}
	}

	/**
	 * 
	 * 文件夹文件上传预上传
	 * 
	 * @param ownerId
	 * @param parentId
	 * @param name
	 * @param size
	 * @return <参数描述>
	 * 
	 */
	@RequestMapping(value = "dirPreupload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> dirPreUpload(long ownerId, long parentId, String name, long size, String linkCode) {
		String uploadUrl = "";
		if (StringUtils.isNotBlank(linkCode)) {
			uploadUrl = getLinkUploadUrl(name, ownerId, parentId, size, linkCode);
		} else {
			uploadUrl = getUploadUrl(name, ownerId, parentId, size);
		}
		return new ResponseEntity<String>(uploadUrl, HttpStatus.OK);
	}

	/**
	 * 
	 * @param ownerId
	 * @param parentId
	 * @param token
	 * @return
	 */
	public List<TreeNode> findFolder(long ownerId, long parentId, String token) {
		BasicNodeListRequest listFolderRequest = new BasicNodeListRequest(MAX_TREE_SIZE, 0L);
		Order orderByField = new Order("modifiedAt", "DESC");
		listFolderRequest.addOrder(orderByField);

		RestFolderLists list = folderClient.listFolder(token, listFolderRequest, ownerId, parentId);
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>(list.getFolders().size());

		for (RestFolderInfo folderInfo : list.getFolders()) {
			treeNodeList.add(new TreeNode(folderInfo));
		}

		return treeNodeList;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param name
	 * @param ownerId
	 * @param parentId
	 * @param token
	 * @return
	 */
	public long createMkir(String name, long parentId, String token, long ownerId) {
		CreateFolderRequest request = new CreateFolderRequest(name, parentId);
		String uri = Constants.RESOURCE_FOLDER + '/' + ownerId;
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", token);

		TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);

		if (response.getStatusCode() != HttpStatus.CREATED.value()) {
			RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
			throw exception;
		}

		RestFolderInfo result = JsonUtils.stringToObject(response.getResponseBody(), RestFolderInfo.class);

		return result.getId();
	}

	/**
	 * 为外链上传文件夹时(上传节点中不存在)创建文件夹
	 * 
	 * @param name
	 * @param ownerId
	 * @param parentId
	 * @param token
	 * @param linkCode
	 * @return
	 */
	// For creating folder for link
	public long createMkirForLink(String name, long parentId, String token, long ownerId, String linkCode) {
		CreateFolderRequest request = new CreateFolderRequest(name, parentId);
		String uri = Constants.RESOURCE_FOLDER + '/' + ownerId + "/link";
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", token);
		if (linkCode != null && !"".equals(linkCode)) {
			headerMap.put("linkCode", linkCode);
		}
		if (token == null) {
			headerMap.put("linkType", "anonymous");
		}else{
			headerMap.put("linkType", "logined");
		}

		TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);
		if (response.getStatusCode() != HttpStatus.CREATED.value()) {
			RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
			throw exception;
		}

		RestFolderInfo result = JsonUtils.stringToObject(response.getResponseBody(), RestFolderInfo.class);
		return result.getId();
	}

	// For link upload
	public ResponseEntity<String> linkPreUpload(long ownerId, long parentId, String name, long size, String linkCode) {
		if (StringUtils.isBlank(linkCode)) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		int i = 1;
		String uri = Constants.RESOURCE_FILE + '/' + ownerId;

		Map<String, String> headerMap = assembleLink(linkCode);
		FilePreUploadRequest request = new FilePreUploadRequest(name, parentId, size);
		TextResponse response;
		int status = -1;
		while (true) {
			response = ufmClientService.performJsonPutTextResponse(uri, headerMap, request);
			status = response.getStatusCode();
			if (status == HttpStatus.OK.value()) {
				String content = response.getResponseBody();
				FilePreUploadResponse preUploadRsp = JsonUtils.stringToObject(content, FilePreUploadResponse.class);
				return new ResponseEntity<String>(preUploadRsp.getUploadUrl(), HttpStatus.OK);
			} else if (status == HttpStatus.CONFLICT.value()) {
				String newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, name, i);
				request.setName(newName);
				i++;
				continue;
			} else if (status == HttpStatus.BAD_REQUEST.value()) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			} else if (status == HttpStatus.FORBIDDEN.value()) {
				return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
			} else if (status == HttpStatus.PRECONDITION_FAILED.value()) {
				return new ResponseEntity<String>(HttpStatus.PRECONDITION_FAILED);
			} else {
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/**
	 * 
	 * 外链上传文件夹时获取文件上传地址
	 * 
	 * @param name
	 * @param ownerId
	 * @param parentId
	 * @param fileSize
	 * @return <参数描述>
	 * 
	 */
	private String getLinkUploadUrl(String name, long ownerId, long parentId, long fileSize, String linkCode) {
		// 兼容插件和h5获取的文件路径信息
		if (name.contains(UploadFolderController.BACK_SLASH)) {
			name = name.replaceAll(UploadFolderController.DOUBLE_BACK_SLASH, UploadFolderController.SLASH);
		}

		name = name.startsWith(UploadFolderController.SLASH) ? name.substring(1, name.length()) : name;

		String[] folderName = name.split(UploadFolderController.SLASH);

		int start = name.lastIndexOf(UploadFolderController.SLASH);

		String fileName = name.substring(start + 1);

		boolean dirExist = false;
		String token = getToken() != null ? getToken() : "";
		
		for (int i = 0; i < folderName.length - 1; i++) {
			dirExist = false;
			// 查询所有文件夹
			List<TreeNode> treeNodeList = findFolderForLink(ownerId, parentId, token, linkCode);
			for (TreeNode node : treeNodeList) {
				if (folderName[i].equals(node.getName())) {
					dirExist = true;
					parentId = node.getId();
					break;
				}
			}
			if (!dirExist) {
				parentId = createMkirForLink(folderName[i], parentId, token, ownerId, linkCode);
				// 上传外链文件
			}
		}
		String uploadUrl = dirLinkPreUpload(fileName, parentId, ownerId, fileSize, linkCode);
		return uploadUrl;
	}

	// For link

	/**
	 * 
	 * 预上传
	 * 
	 * @param fileName
	 * @param parentId
	 * @param ownerId
	 * @param size
	 * @return <参数描述>
	 * 
	 */
	private String dirLinkPreUpload(String fileName, long parentId, long ownerId, long size, String linkCode) {
		INode node = new INode();
		node.setParentId(parentId);
		node.setName(fileName);
		node.setSize(size);
		node.setType(INode.TYPE_FILE);
		node.setOwnedBy(ownerId);
		int i = 1;
		String uri = Constants.RESOURCE_FILE + '/' + ownerId;

		Map<String, String> headerMap = assembleLink(linkCode);
		FilePreUploadRequest request = new FilePreUploadRequest(fileName, parentId, size);

		TextResponse response = null;
		int status = -1;
		while (true) {
			response = ufmClientService.performJsonPutTextResponse(uri, headerMap, request);
			status = response.getStatusCode();
			if (status == HttpStatus.OK.value()) {
				String content = response.getResponseBody();
				FilePreUploadResponse preUploadRsp = JsonUtils.stringToObject(content, FilePreUploadResponse.class);

				return preUploadRsp.getUploadUrl();
			} else if (status == HttpStatus.CONFLICT.value()) {
				String newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, fileName, i);
				request.setName(newName);
				i++;
				continue;
			} else if (status == HttpStatus.BAD_REQUEST.value()) {
				return "";
			} else if (status == HttpStatus.FORBIDDEN.value()) {
				return "";
			} else if (status == HttpStatus.PRECONDITION_FAILED.value()) {
				return "";
			}
			return "";
		}
	}

	/**
	 * 
	 * @param ownerId
	 * @param parentId
	 * @param token
	 * @return
	 */
	public List<TreeNode> findFolderForLink(long ownerId, long parentId, String token, String linkCode) {
		BasicNodeListRequest listFolderRequest = new BasicNodeListRequest(MAX_TREE_SIZE, 0L);
		Order orderByField = new Order("modifiedAt", "DESC");
		listFolderRequest.addOrder(orderByField);

		RestFolderLists list = folderClient.listFolderForLink(token, listFolderRequest, ownerId, parentId, linkCode);
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>(list.getFolders().size());

		for (RestFolderInfo folderInfo : list.getFolders()) {
			treeNodeList.add(new TreeNode(folderInfo));
		}

		return treeNodeList;
	}

	/**
	 * 
	 * 外链文件夹上传预上传,IE版本
	 * 
	 * @param request
	 * @param ownerId
	 * @param parentId
	 * @return <参数描述>
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "linkDirPreUpload/{ownerId}/{parentId}/{linkCode}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> linkDirPreUploadSrv(HttpServletRequest request, @PathVariable long ownerId,
			@PathVariable long parentId, @PathVariable String linkCode) {

		String strBody = UploadFolderController.FLY_DRAGON.get();
		try {
			strBody = strBody.substring(strBody.indexOf("["), strBody.indexOf("]") + 1);
		} catch (Exception e) {
			logger.error("fail to get requestBody!", e);
		}

		List<ActiveXFileInfo> fileList = new ArrayList<ActiveXFileInfo>();
		fileList = (List<ActiveXFileInfo>) JsonUtils.stringToList(strBody, ArrayList.class, ActiveXFileInfo.class);
		String uploadUrl = "";
		List<ActiveXFileInfoResp> fileInfoResp = new ArrayList<ActiveXFileInfoResp>();

		for (ActiveXFileInfo file : fileList) {
			ActiveXFileInfoResp resp = new ActiveXFileInfoResp();
			resp.setFileId(file.getFileId());

			if (file.getSize() == 0) {
				resp.setUploadUrl("");
			} else {
				if (StringUtils.isNotBlank(linkCode)) {
					uploadUrl = getLinkUploadUrl(file.getFileName(), ownerId, parentId, file.getSize(), linkCode);
				} else {

				}
				resp.setUploadUrl(uploadUrl + "?objectLength=" + file.getSize());
			}
			// resp.setUploadUrl("");
			fileInfoResp.add(resp);
		}

		String response = this.toJson(fileInfoResp);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	public <T> String toJson(T obj) {
		ObjectMapper mapper = new ObjectMapper();
		// mapper.setSerializationInclusion(Include.NON_EMPTY);
		StringWriter sw = new StringWriter();

		try {
			mapper.writeValue(sw, obj);
		} catch (Exception e) {
			logger.error("parse json string error:", e);
		}
		String result = sw.toString();
		return result;
	}

	/***************************************************************************** 新增文件夹预上传 *********************************************************************************/

}
