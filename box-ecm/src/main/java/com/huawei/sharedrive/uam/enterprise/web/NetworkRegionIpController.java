package com.huawei.sharedrive.uam.enterprise.web;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.UnknownSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.huawei.sharedrive.uam.authapp.service.impl.ImportNetRegionIpThread;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.ImportNetRegionIp;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;
import com.huawei.sharedrive.uam.enterprise.domain.NetWorkRegionIpExcelImport;
import com.huawei.sharedrive.uam.enterprise.domain.NetworkIpExcelEnum;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprise.manager.NetRegionManager;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionService;
import com.huawei.sharedrive.uam.enterprise.service.NetworkRegionExcelService;
import com.huawei.sharedrive.uam.enterprise.service.NetworkRegionIpExcelImportService;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.NoSuchNetworkRegionIpException;
import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.ImageCheckUtil;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/security")
public class NetworkRegionIpController extends AbstractCommonController {
	private static Logger logger = LoggerFactory.getLogger(NetworkRegionIpController.class);

	public static final String NETWORK_MAX_SIZE_KEY = "networkRegionIp.excelFile.max.size";

	public static final String EXCEL_2007 = "xlsx";

	private long excelFileSize = 1024 * 1024 * 20L;

	@Autowired
	private NetRegionManager manager;

	@Autowired
	private NetworkRegionExcelService excelService;

	@Autowired
	private NetRegionService netRegionService;

	@Autowired
	private NetworkRegionIpExcelImportService networkRegionExcelImportService;

	@Autowired
	private SystemConfigDAO systemConfigDAO;

	@Autowired
	private AdminLogManager adminlogManager;

	@Autowired
	private AdminLogManager enterpriseAdminLogManager;

	@Autowired
	private EnterpriseManager enterpriseManager;

	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;

	@Resource(name = "cacheClient")
	private CacheClient cacheClient;

	@PostConstruct
	public void init() {
		SystemConfig config = systemConfigDAO.getByPriKey("-1", NETWORK_MAX_SIZE_KEY);
		if (config != null) {
			try {
				excelFileSize = Long.parseLong(config.getValue());
			} catch (NumberFormatException e) {
				logger.info("init excelFileSize final  systemConfig table the key" + NETWORK_MAX_SIZE_KEY + "value"
						+ config.getValue() + "con't pase to int", e);
			}
		}
	}

	@RequestMapping(value = "listNetRegionIp/{appId}/", method = { RequestMethod.GET })
	public String enterNetRegionConfigPage(@PathVariable(value = "appId") String appId, Model model, long id) {
		model.addAttribute("appId", appId);
		model.addAttribute("id", id);
		return "enterprise/security/modifyNetRegion";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "listNetRegionIp/{appId}/", method = { RequestMethod.POST })
	public ResponseEntity list(@PathVariable(value = "appId") String appId,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber, Integer id, String token) {
		super.checkToken(token);
		PageRequest request = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);
		NetRegionIp netRegionIp = new NetRegionIp();
		netRegionIp.setNetRegionId(id);
		netRegionIp.setAccountId(getAccoutId(appId));
		Page<NetRegionIp> securityRolePage = manager.getFilterd(netRegionIp, request);

		return new ResponseEntity(securityRolePage, HttpStatus.OK);
	}

	@RequestMapping(value = "listCreateNetRegionIp/{appId}/", method = { RequestMethod.GET })
	public String enterCreateNetRegionConfigPage(@PathVariable(value = "appId") String appId, Model model, long id) {
		model.addAttribute("appId", appId);
		model.addAttribute("id", id);
		return "enterprise/security/createNetRegion";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "listCreateNetRegionIp/{appId}/", method = { RequestMethod.POST })
	public ResponseEntity listCreate(@PathVariable(value = "appId") String appId,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber, Integer id, Model model,
			String token) {
		super.checkToken(token);
		PageRequest request = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);
		NetRegionIp netRegionIp = new NetRegionIp();
		netRegionIp.setNetRegionId(id);
		netRegionIp.setAccountId(getAccoutId(appId));
		Page<NetRegionIp> securityRolePage = manager.getFilterd(netRegionIp, request);
		return new ResponseEntity(securityRolePage, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "listAddNetRegionIp/{appId}/", method = { RequestMethod.POST })
	public ResponseEntity listNewCreate(@PathVariable(value = "appId") String appId,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber, Integer id, String token,
			HttpServletRequest req) {
		super.checkToken(token);
		PageRequest request = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);

		String sessionId = req.getSession().getId();
		String sessionKey = "temp_netRegionIp_" + sessionId;
		Object cache = cacheClient.getCache(sessionKey);

		List<NetRegionIp> list = (List<NetRegionIp>) cache;
		int ipID = 0;
		for (NetRegionIp item : list) {
			item.setId(ipID);
			ipID++;
		}

		Page<NetRegionIp> page = new PageImpl<NetRegionIp>(list, request, list.size());

		return new ResponseEntity(page, HttpStatus.OK);
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "createNetRegionIp/{appId}/", method = RequestMethod.GET)
	public String enterCreateConfigIp(@PathVariable(value = "appId") String appId, Model model, long id) {
		model.addAttribute("appId", appId);
		model.addAttribute("id", id);
		return "enterprise/security/createNetRegion";
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "modifyNetRegionIp/{appId}/", method = RequestMethod.GET)
	public String enterModify(@PathVariable(value = "appId") String appId, long id, Model model) {
		model.addAttribute("appId", appId);
		PageRequest request = new PageRequest();
		request.setSize(Constants.DEFAULT_PAGE_SIZE);
		NetRegion admin = manager.getById(id);

		if (null != admin) {
			model.addAttribute("securityRole", admin);
		} else {
			logger.error("the modified data is not exists.");
		}
		return "enterprise/security/modifyNetRegionIp";
	}

	/**
	 * 
	 * @param admin
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "modifyNetRegionIp/{appId}/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> modify(@PathVariable(value = "appId") String appId, NetRegionIp netRegionIp,
			HttpServletRequest req, String token) throws IOException {
		super.checkToken(token);
		String[] description = new String[] { getEnterpriseName(), appId, netRegionIp.getIpStart(),
				netRegionIp.getIpEnd() };
		checkRequestValue(netRegionIp);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setAppId(appId);
		owner.setIp(IpUtils.getClientAddress(req));
		try {
			manager.modify(netRegionIp);
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_UPDATE, description);
		} catch (RuntimeException e) {
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_UPDATE_ERROR, description);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	/**
	 * 
	 * @param admin
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "createNetRegionIp/{appId}/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> create(@PathVariable(value = "appId") String appId, NetRegionIp netRegionIp,
			HttpServletRequest req, String token) throws IOException {
		super.checkToken(token);
		String[] description = new String[] { getEnterpriseName(), appId, netRegionIp.getIpStart(),
				netRegionIp.getIpEnd() };
		checkRequestValue(netRegionIp);
		netRegionIp.setAccountId(getAccoutId(appId));
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setAppId(appId);
		owner.setIp(IpUtils.getClientAddress(req));
		try {
			manager.create(netRegionIp);
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_ADD, description);
		} catch (RuntimeException e) {
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_ADD_ERROR, description);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	/**
	 * 
	 * @param admin
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "addNetRegionIp/{appId}/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> add(@PathVariable(value = "appId") String appId, NetRegionIp netRegionIp,
			HttpServletRequest req, String token) throws IOException {
		super.checkToken(token);
		checkRequestValue(netRegionIp);
		netRegionIp.setAccountId(getAccoutId(appId));

		String sessionId = req.getSession().getId();
		String sessionKey = "temp_netRegionIp_" + sessionId;
		Date expireTime = new Date(System.currentTimeMillis() + 600000);
		Object cache = cacheClient.getCache(sessionKey);
		List<NetRegionIp> list = new ArrayList<NetRegionIp>(16);
		if (null != cache) {
			list = (List<NetRegionIp>) cache;
		}
		list.add(netRegionIp);
		boolean success = cacheClient.setCache(sessionKey, list, expireTime);
		if (!success) {
			throw new UnknownSessionException("Store session to memcache failed.");
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkRequestValue(NetRegionIp netRegionIp) {
		Set violations = validator.validate(netRegionIp);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		if(!validata_ip(netRegionIp.getIpStart(), netRegionIp.getIpEnd())){
			throw new ConstraintViolationException(violations);
		}
	}
	
	public boolean validata_ip(String startIp, String endIp) {
		InetAddress addressIpStart = null;
		InetAddress addressIpEnd = null;
		try {
			addressIpStart = InetAddress.getByName(startIp);
			addressIpEnd = InetAddress.getByName(endIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		if (addressIpStart instanceof Inet6Address) {
			if (addressIpEnd instanceof Inet6Address) {
				return true;
			} else {
				return false;
			}

		}
		if (addressIpStart instanceof Inet4Address) {
			if (addressIpEnd instanceof Inet4Address) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param appId
	 * 			@param model @return @throws
	 */
	@RequestMapping(value = "/enterImportNetRegionIp/{appId}/", method = RequestMethod.GET)
	public String enterImportPage(@PathVariable(value = "appId") String appId, Model model) {
		enterpriseAccountManager.bindAppCheck(appId);
		model.addAttribute("appId", appId);
		return "enterprise/security/importNetRegionIp";
	}

	/**
	 * 
	 * @param appId
	 * 			@param model @return @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "getImportNetRegionIp/{appId}/", method = RequestMethod.POST)
	public ResponseEntity getImportNetRegionIp(
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
			@PathVariable(value = "appId") String appId, Model model, HttpServletRequest httpRequest, String token) {
		super.checkToken(token);
		Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
		List<NetWorkRegionIpExcelImport> list = networkRegionExcelImportService.getListExcelImport(getAccoutId(appId));
		StringBuilder sb;
		for (NetWorkRegionIpExcelImport ni : list) {
			sb = new StringBuilder();
			sb.append(NetworkIpExcelEnum.NETREGIONIP_EXPORT_SUCCEEDED_DESC.getDetails(locale, null));
			sb.append(':');
			sb.append(ni.getSucceededCount());
			sb.append(',');
			sb.append(NetworkIpExcelEnum.NETREGIONIP_EXPORT_FAILED_DESC.getDetails(locale, null));
			sb.append(ni.getFailedCount());
			ni.setResultStr(sb.toString());
			ni.setResultData(null);
		}

		return new ResponseEntity(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/netRegionIpTemplateFile", method = RequestMethod.GET)
	public void downloadUserInfoTemplateFile(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			excelService.downloadNetRegionIpTemplateFile(request, response);
		} catch (IOException e) {
			logger.error(e.toString());
			response.getOutputStream().close();
		} finally {
			IOUtils.closeQuietly(response.getOutputStream());

		}

	}

	/**
	 * 
	 * @param appId
	 * @param model
	 * 
	 * @return
	 */
	@RequestMapping(value = "/importNetRegionIp/{appId}/", method = RequestMethod.POST)
	public String importNetRegion(@PathVariable(value = "appId") String appId, MultipartHttpServletRequest request,
			Model model, HttpServletRequest req, String token) {
		super.checkToken(token);
		String[] description = new String[] { getEnterpriseName(), appId };

		int id = 0;
		String saveState = "success";
		String errMsg = "";
		List<ImportNetRegionIp> allUsers = new ArrayList<ImportNetRegionIp>(10);
		Locale locale;
		LogOwner owner = new LogOwner();

		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(request));
		owner.setAppId(null);
		owner.setLoginName(getEnterpriseEmail());
		owner.setAuthServerId(null);

		try {
			boolean isAllowed = excelService.isDuplicateTask(getAccoutId(appId));
			if (!isAllowed) {
				saveState = "conflict";
			}

			locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
			String language = locale.getLanguage();
			if (SecurityConfigConstants.LOCAL_ZH_CN.equals(language)
					|| SecurityConfigConstants.LOCAL_ZH.equals(language)) {
				locale = Locale.CHINA;
			} else {
				locale = Locale.ENGLISH;
			}
			Map<String, MultipartFile> fileMap = request.getFileMap();
			MultipartFile file = null;
			String fileName;
			List<ImportNetRegionIp> users;
			for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
				file = entry.getValue();
				if (null == file) {
					continue;
				}
				fileName = file.getOriginalFilename();
				if (StringUtils.isBlank(fileName)) {
					throw new InvalidParamterException("file name is null");

				}
				if (!checkpostfix(fileName)) {
					throw new InvalidParamterException("file is not excel");
				}
				if (file.getSize() > excelFileSize) {
					logger.error("The size of the file to be uploaded cannot exceed 20 MB.");
					throw new InvalidParamterException("the excel size is more than 20 MB");
				}

				users = excelService.parseFile2007ForNetRegionIp(file.getInputStream(), locale);
				if (null != users) {
					allUsers.addAll(users);
				}
			}

			if (allUsers.isEmpty()) {
				if (!"excelFileSize".equals(errMsg)) {
					errMsg = "formatFileError";
				}
				throw new InvalidParamterException("file format error");
			}
			ImportNetRegionIpThread sut = new ImportNetRegionIpThread(allUsers, id, locale, excelService,
					getAccoutId(appId));
			logger.info("begin to import user");
			Executors.newSingleThreadExecutor().execute(sut);
		} catch (InvalidParamterException e) {
			logger.error("import excel failed", e);
			enterpriseAdminLogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_ERROR, description);
		} catch (Exception e) {
			errMsg = "serverException";
			logger.error("import excel failed", e);
			enterpriseAdminLogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_ERROR, description);
		}
		enterpriseAdminLogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION, description);
		List<NetWorkRegionIpExcelImport> list = networkRegionExcelImportService.getByAppId(String.valueOf(id));
		model.addAttribute("excelImportList", list);
		model.addAttribute("saveState", saveState);
		model.addAttribute("errMsg", errMsg);
		model.addAttribute("unit", NetWorkRegionIpExcelImport.MAX_IMPORT);
		model.addAttribute("id", id);
		return "enterprise/security/importNetRegionIp";
	}

	@RequestMapping(value = "deleteNetRegionIp/{appId}/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteNetRegionIp(@PathVariable(value = "appId") String appId, String ids,
			HttpServletRequest req, String token) {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setAppId(appId);
		owner.setIp(IpUtils.getClientAddress(req));
		if (StringUtils.isBlank(ids)) {
			logger.error("ids is null");
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_DELETE_ERROR,
					new String[] { getEnterpriseName(), appId });
			throw new InvalidParamterException();
		}
		String[] idArray = ids.split(",");
		for (int i = 0; i < idArray.length; i++) {
			deleteNetRegionIpForOne(appId, owner, idArray, i);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "deleteNewAddNetRegionIp/{appId}/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteNewAddNetRegionIp(@PathVariable(value = "appId") String appId, String ids,
			HttpServletRequest req, String token) {
		super.checkToken(token);
		String sessionId = req.getSession().getId();
		String sessionKey = "temp_netRegionIp_" + sessionId;
		Object cache = cacheClient.getCache(sessionKey);
		List<NetRegionIp> list = (List<NetRegionIp>) cache;
		String[] idArray = ids.split(",");
		for (int i = idArray.length - 1; i >= 0; i--) {
			list.remove(Integer.parseInt(idArray[i]));
		}
		Date expireTime = new Date(System.currentTimeMillis() + 600000);
		cacheClient.setCache(sessionKey, list, expireTime);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "deleteCloseNetRegionIp/{appId}/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> cleanAllNetRegionIp(@PathVariable(value = "appId") String appId, String ids,
			HttpServletRequest req, String token) {
		super.checkToken(token);
		String sessionId = req.getSession().getId();
		String sessionKey = "temp_netRegionIp_" + sessionId;
		Object cache = cacheClient.getCache(sessionKey);
		List<NetRegionIp> list = (List<NetRegionIp>) cache;
		for (NetRegionIp item : list) {
			item.setAccountId(getAccoutId(appId));
			cacheClient.deleteCache(sessionKey);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	private void deleteNetRegionIpForOne(String appId, LogOwner owner, String[] idArray, int i) {
		NetRegionIp netRegionIp = netRegionService.getNetRegionIpById(Long.parseLong(idArray[i]));
		if (null == netRegionIp) {
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_DELETE_ERROR,
					new String[] { getEnterpriseName(), appId });
			throw new NoSuchNetworkRegionIpException();
		}
		String[] description = new String[] { getEnterpriseName(), appId, netRegionIp.getIpStart(),
				netRegionIp.getIpEnd() };
		try {
			manager.delete(Integer.parseInt(idArray[i]));
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_DELETE, description);
		} catch (NumberFormatException e) {
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_DELETE_ERROR, description);
			throw new InvalidParamterException("parse id to int value failed");
		} catch (IOException e) {
			logger.warn("", e);
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_DELETE_ERROR, description);
		}
	}

	@RequestMapping(value = "export/{appId}/", method = RequestMethod.GET)
	public void exportUserInfo(@PathVariable(value = "appId") String appId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String[] description;
		Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(request));
		owner.setAppId(appId);
		try {
			String netRegionIpIds = excelService.exportNetRegionList(getAccoutId(appId), request, response);
			description = new String[] { getEnterpriseName(), appId, netRegionIpIds };
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP, description);
		} catch (Exception e) {
			logger.error(e.toString());
			description = new String[] { admin.getLoginName(), appId };
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_NETWORK_REGION_IP_ERROR, description);
			response.getOutputStream().close();
		}

		finally {
			IOUtils.closeQuietly(response.getOutputStream());

		}
	}

	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/deleteImportResult/{appId}/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteImportResult(@PathVariable(value = "appId") String appId, long id,
			HttpServletRequest req, String token) {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(req));
		owner.setAppId(appId);
		logger.info("delete import result:[id:" + id + "]");
		String[] description = new String[] { getEnterpriseName(), appId, String.valueOf(id) };
		int deleteResult = networkRegionExcelImportService.delete(id);
		if (deleteResult < 1) {
			adminlogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_NETREGION_IMPORT_RESULT_ERROR, description);
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		adminlogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_NETREGION_IMPORT_RESULT, description);
		return new ResponseEntity(HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "getImportResult/{appId}/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity getImportResult(@PathVariable(value = "appId") String appId, String token) {
		super.checkToken(token);
		String returnValue = "";
		List<NetWorkRegionIpExcelImport> list = networkRegionExcelImportService.getListExcelImport(getAccoutId(appId));
		for (NetWorkRegionIpExcelImport e : list) {
			if (e.getStatus() == NetWorkRegionIpExcelImport.IMPORTING) {
				returnValue = "importing";
				continue;
			}
		}
		return new ResponseEntity(returnValue, HttpStatus.OK);
	}

	@RequestMapping(value = "exportImportExcelResult/{appId}/", method = RequestMethod.GET)
	public void exportImportExcelResult(@PathVariable(value = "appId") String appId, HttpServletRequest request,
			HttpServletResponse response, long id) throws IOException {
		try {
			excelService.exportImportExcelResult(request, response, id);
		} catch (Exception e) {
			logger.error(e.toString());
			response.getOutputStream().close();
		}

		finally {
			IOUtils.closeQuietly(response.getOutputStream());
		}
	}

	private boolean checkpostfix(String name) {
		if (StringUtils.isBlank(name)) {
			return false;
		}
		boolean checkfix = ImageCheckUtil.checksuffix(name, EXCEL_2007, true);
		return checkfix;
	}

	private String getEnterpriseEmail() {
		long enterpriseId = checkAdminAndGetId();
		Enterprise enterprise = enterpriseManager.getById(enterpriseId);
		if (enterprise != null) {
			return enterprise.getContactEmail();
		}
		return null;
	}
}
