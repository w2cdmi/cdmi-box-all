package pw.cdmi.box.disk.files.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.FolderClient;
import pw.cdmi.box.disk.client.domain.node.CreateFolderRequest;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFileInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.files.domain.Path;
import pw.cdmi.box.disk.files.domain.Shortcut;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/folders")
public class FolderController extends CommonController {
	private static final int MAX_TREE_SIZE = 1000;

	private FolderClient folderClient;

	@Resource
	private RestClient ufmClientService;

	@Autowired
	private UserService userService;

	/**
	 * Create a new folder
	 * 
	 * @param parentId
	 * @param folderName
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> createForder(Locale locale, long ownerId, long parentId, String name,
			HttpServletRequest httpServletRequest) throws Exception {
		try {
			super.checkToken(httpServletRequest);
			FilesCommonUtils.checkNodeNameVaild(name);
			CreateFolderRequest request = new CreateFolderRequest(name, parentId);
			RestFolderInfo result = folderClient.createFolder(getToken(), request, ownerId);
			return new ResponseEntity<RestFolderInfo>(result, HttpStatus.OK);

		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);

		}

		//
	}

	/**
	 * Opens the create folder page
	 */
	@RequestMapping(value = "create/{ownerId}/{parentId}", method = RequestMethod.GET)
	public String createForm(@PathVariable("ownerId") long ownerId, @PathVariable("parentId") long parentId,
			Model model) {
		model.addAttribute("ownerId", ownerId);
		model.addAttribute("parentId", parentId);
		return "files/createFolder";
	}

	/**
	 * Opens the create folder page
	 */
	@RequestMapping(value = "createReciver/{ownerId}/{nodeId}", method = RequestMethod.GET)
	public String createReciver(@PathVariable("ownerId") long ownerId, @PathVariable("nodeId") long nodeId,
			Model model) {
		model.addAttribute("ownerId", ownerId);
		model.addAttribute("nodeId", nodeId);
		return "folder/reciveFolder";
	}

	/**
	 * Opens the create folder page
	 */
	@RequestMapping(value = "listReciver/{ownerId}", method = RequestMethod.GET)
	public String listReciver(@PathVariable("ownerId") long ownerId, Model model) {
		model.addAttribute("ownerId", ownerId);
		return "folder/listReciver";
	}

	/**
	 * Information navigation path to get a list of the current ID
	 * 
	 * @param ownerId
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "getPaths/{ownerId}/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPaths(@PathVariable("ownerId") long ownerId, @PathVariable("id") long id) {
		if (id == INode.FILES_ROOT) {
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		List<Path> paths = new ArrayList<Path>(BusinessConstants.INITIAL_CAPACITIES);
		buildNodePath(ownerId, paths, id);
		return new ResponseEntity<List<Path>>(paths, HttpStatus.OK);
	}

	@PostConstruct
	public void init() {
		folderClient = new FolderClient(ufmClientService);
	}

	/**
	 * Folder list
	 * 
	 * @param parentId
	 * @param pageNumber
	 * @param orderField
	 * @param desc
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "list", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Page<INode>> list(Long ownerId,
			@RequestParam(value = "parentId", defaultValue = "0") Long parentId,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "40", required = false) Integer pageSize,
			@RequestParam(value = "orderField", defaultValue = "modifiedAt") String orderField,
			@RequestParam(value = "desc", defaultValue = "true") boolean desc, HttpServletRequest request) {
		UserToken user = getCurrentUser();
		if (ownerId == null) {
			ownerId = user.getCloudUserId();
		}
		PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
		pageRequest.setOrder(new Order(orderField, desc));

		super.checkToken(request);

		long offset = 0L;
		if (pageNumber > 0) {
			offset = (long) (pageNumber - 1) * pageSize;
		}

		BasicNodeListRequest listFolderRequest = generalRequest(orderField, desc, offset, pageSize);
		RestFolderLists list = folderClient.listFolder(getToken(), listFolderRequest, ownerId, parentId);

		List<INode> nodeList = transToNodeList(list);

		Page<INode> page = new PageImpl<INode>(nodeList, pageRequest, list.getTotalCount());
		return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "listReciveFolder", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Page<INode>> listReciveFolder(Long ownerId,
			@RequestParam(value = "parentId", defaultValue = "0") Long parentId,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "40", required = false) Integer pageSize,
			@RequestParam(value = "orderField", defaultValue = "modifiedAt") String orderField,
			@RequestParam(value = "desc", defaultValue = "true") boolean desc, HttpServletRequest request) {
		UserToken user = getCurrentUser();
		PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
		pageRequest.setOrder(new Order(orderField, desc));

		super.checkToken(request);

		long offset = 0L;
		if (pageNumber > 0) {
			offset = (long) (pageNumber - 1) * pageSize;
		}
		BasicNodeListRequest listFolderRequest = generalRequest(orderField, desc, offset, pageSize);

		RestFolderLists list = folderClient.listReciveFolder(getToken(), user.getCloudUserId(), listFolderRequest,
				ownerId, parentId);

		List<INode> nodeList = transToNodeList(list);

		Page<INode> page = new PageImpl<INode>(nodeList, pageRequest, list.getTotalCount());
		return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "listTreeNode/{ownerId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<List<TreeNode>> listTreeNode(@PathVariable("ownerId") long ownerId,
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "orderField", defaultValue = "modifiedAt") String orderField,
			@RequestParam(value = "desc", defaultValue = "true") boolean desc, HttpServletRequest request) {
		super.checkToken(request);

		Long parentId = id != null ? id : INode.FILES_ROOT;
		BasicNodeListRequest listFolderRequest = new BasicNodeListRequest(MAX_TREE_SIZE, 0L);
		Order orderByField = new Order(orderField, desc ? "DESC" : "ASC");
		listFolderRequest.addOrder(orderByField);

		RestFolderLists list = folderClient.listFolder(getToken(), listFolderRequest, ownerId, parentId);
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>(list.getFolders().size());

		for (RestFolderInfo folderInfo : list.getFolders()) {
			treeNodeList.add(new TreeNode(folderInfo));
		}
		return new ResponseEntity<List<TreeNode>>(treeNodeList, HttpStatus.OK);
	}

	/**
	 * Recursive obtaining node information
	 * 
	 * @param ownerId
	 * @param paths
	 * @param folderId
	 * @throws RestException
	 */
	private void buildNodePath(long ownerId, List<Path> paths, long folderId) throws RestException {
		RestFolderInfo folderInfo = folderClient.getFolderInfo(getToken(), ownerId, folderId);
		INode node = new INode(folderInfo);
		node.setName(HtmlUtils.htmlEscape(node.getName()));
		if (node.getParentId() == INode.FILES_ROOT) {
			paths.add(new Path(node));
			return;
		}
		buildNodePath(ownerId, paths, node.getParentId());
		paths.add(new Path(node));
	}

	private BasicNodeListRequest generalRequest(String orderField, boolean desc, long offset, int limit) {
		BasicNodeListRequest listFolderRequest = new BasicNodeListRequest(limit, offset);
		Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH, Thumbnail.DEFAULT_SMALL_HEIGHT);
		Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH, Thumbnail.DEFAULT_BIG_HEIGHT);
		listFolderRequest.addThumbnail(smallThumb);
		listFolderRequest.addThumbnail(bigThumb);
		Order orderByType = new Order("TYPE", "ASC");
		Order orderByField = new Order(orderField, desc ? "DESC" : "ASC");
		orderByField.setDesc(desc);
		listFolderRequest.addOrder(orderByType);
		listFolderRequest.addOrder(orderByField);
		return listFolderRequest;
	}

	private List<INode> transToNodeList(RestFolderLists list) {
		List<INode> content = new ArrayList<INode>(list.getTotalCount());
		INode iNode = null;
		for (RestFolderInfo folderInfo : list.getFolders()) {
			iNode = new INode(folderInfo);
			content.add(iNode);
		}
		for (RestFileInfo fileInfo : list.getFiles()) {
			iNode = new INode(fileInfo);
			content.add(iNode);
		}

		for (INode inode : content) {
			inode.setName(HtmlUtils.htmlEscape(inode.getName()));
			inode.setDescription(HtmlUtils.htmlEscape(inode.getDescription()));
			inode.setLinkCode(HtmlUtils.htmlEscape(inode.getLinkCode()));
			inode.setModifiedByName(HtmlUtils.htmlEscape(inode.getModifiedByName()));
			inode.setPath(HtmlUtils.htmlEscape(inode.getPath()));
			if (inode.getCreatedBy() != 0) {
				try {
					User createUser = userService.getUserByCloudUserId(inode.getCreatedBy());
					if (createUser != null) {
						inode.setCreatedByName(createUser.getName());
					}
				}catch(Exception e){
				}

			}

		}

		return content;
	}

	/**
	 * 
	 * 文件智能分类
	 *
	 * <参数类型> @param ownerId 拥有都 <参数类型> @param docType 分类类型【可在ufm工程.DocType类中查看】
	 * <参数类型> @param pageNumber <参数类型> @param pageSize <参数类型> @param orderField
	 * <参数类型> @param desc <参数类型> @param request <参数类型> @return
	 *
	 * @return ResponseEntity<Page<INode>>
	 */
	@RequestMapping(value = "listByDocType", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Page<INode>> listByDocType(Long ownerId, @RequestParam(value = "docType") Integer docType,
			@RequestParam(value = "name", defaultValue = "", required = false) String name,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "40", required = false) Integer pageSize,
			@RequestParam(value = "orderField", defaultValue = "modifiedAt") String orderField,
			@RequestParam(value = "desc", defaultValue = "true") boolean desc, HttpServletRequest request,
			Model model) {
		Long parentId = INode.FILES_ROOT; // 根节点
		UserToken user = getCurrentUser();
		if (ownerId == null) {
			ownerId = user.getCloudUserId();
		}
		PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
		pageRequest.setOrder(new Order(orderField, desc));
		INode node = new INode();
		node.setId(parentId);
		node.setOwnedBy(ownerId);
		node.setDocType(docType);
		super.checkToken(request);

		long offset = 0L;
		if (pageNumber > 0) {
			offset = (long) (pageNumber - 1) * pageSize;
		}

		BasicNodeListRequest condition = generalRequest(orderField, desc, offset, pageSize);
		RestFolderLists list = folderClient.listByDocType(getToken(), condition, ownerId, docType);

		List<INode> nodeList = transToNodeList(list);

		Page<INode> page = new PageImpl<INode>(nodeList, pageRequest, list.getTotalCount());
		return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
	}

	/**
	 * 最近浏览页面
	 */
	@RequestMapping(value = "/recentFileList", method = RequestMethod.GET)
	public String recentFileList() {
		return "folder/recentFileList";
	}

	/**
	 * 获取最近浏览
	 */
	@RequestMapping(value = "/{ownerId}/recent", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listFolderForRecent(@PathVariable("ownerId") long ownerId) {
		try {
			BasicNodeListRequest condition = generalRequest("", true, 0L, 1000);
			RestFolderLists restFolderLists = folderClient.listFolderForRecent(getToken(), ownerId, condition);
			return new ResponseEntity<RestFolderLists>(restFolderLists, HttpStatus.OK);
		} catch (RestException e) {
			// TODO: handle exception
			return new ResponseEntity<String>(e.getCode(), HttpStatus.BAD_REQUEST);
			/* throw e; */
		}
	}

	/**
	 * 删除最近浏览
	 */
	@RequestMapping(value = "/{ownerId}/recent/delete/{nodeId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteRecent(@PathVariable("ownerId") long ownerId, @PathVariable("nodeId") long nodeId) {
		UserToken user = getCurrentUser();
		BasicNodeListRequest request = new BasicNodeListRequest();
		folderClient.deleteRecent(user, ownerId, nodeId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 快捷目录界面
	 */
	@RequestMapping(value = "/shortcutList", method = RequestMethod.GET)
	public String shortcutList() {
		return "folder/shortcutList";
	}

	/**
	 * 获取快捷目录
	 *
	 */
	@RequestMapping(value = "/{ownerId}/shortcut", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> listFolderForShortcut(@PathVariable("ownerId") long ownerId) {
		UserToken user = getCurrentUser();
		List<Shortcut> restLists = folderClient.listFolderForShortcut(user.getToken(), ownerId);
		return new ResponseEntity<List<Shortcut>>(restLists, HttpStatus.OK);
	}

	/**
	 * 创建快捷目录
	 *
	 */
	@RequestMapping(value = "/{ownerId}/shortcut/create", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> createShortcut(@PathVariable("ownerId") long ownerId, long nodeId, byte type) {
		try {
			UserToken user = getCurrentUser();
			Shortcut shortcut = new Shortcut();
			shortcut.setOwnerId(ownerId);
			shortcut.setNodeId(nodeId);
			shortcut.setCreateBy(user.getCloudUserId());
			shortcut.setType(type);
			folderClient.createShortcut(shortcut);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RestException e) {
			// TODO: handle exception
			return new ResponseEntity<String>(e.getCode(), HttpStatus.CONFLICT);
			/* throw e; */
		}

	}

	/**
	 * 删除快捷目录
	 *
	 */
	@RequestMapping(value = "/{ownerId}/shortcut/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteShortcut(@PathVariable("ownerId") long ownerId, @PathVariable("id") long id) {
		UserToken user = getCurrentUser();
		folderClient.deleteShortcut(user, id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 获取收件箱信息
	 * 
	 * @param ownerId
	 * @return
	 */
	@RequestMapping(value = "getInboxFolder/{ownerId}", method = RequestMethod.GET)
	public ResponseEntity<RestFolderInfo> getInboxFolder(@PathVariable("ownerId") Long ownerId) {
		RestFolderInfo restFolderInfo = folderClient.getInboxFolder(getToken(),ownerId);
		return new ResponseEntity<RestFolderInfo>(restFolderInfo, HttpStatus.OK);
	}
}
