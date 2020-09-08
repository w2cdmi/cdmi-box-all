package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.adminlog.domain.OperateDescriptionType;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseuser.dao.UserLdapDAO;
import com.huawei.sharedrive.uam.enterpriseuser.domain.*;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.enterpriseuser.service.ExcelImportService;
import com.huawei.sharedrive.uam.enterpriseuser.service.ImportEmployeesExcelService;
import com.huawei.sharedrive.uam.exception.*;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.RequestContextUtils;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.uam.domain.AuthApp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImportEmployeesExcelServiceImpl implements ImportEmployeesExcelService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeesExcelServiceImpl.class);

	@Autowired
	private EnterpriseUserService enterpriseUserService;

	@Autowired
	private EnterpriseUserManager enterpriseUserManager;

	@Autowired
	private ExcelImportService excelImportService;

	@Autowired
	private LdapUserManager ldapUserManager;

	@Autowired
	private UserAccountManager userAccountManager;

	@Autowired
	private UserLdapDAO userLdapDAO;

	@Autowired
	private AdminLogManager adminLogManager;

	@Autowired
	DepartmentService departmentService;

	@Autowired
	AuthAppService authAppService;

	private static final String[] EXCEL_TEMPLATE_FILE_HEADER_PRO = new String[] { "name", "alias", "staffNo", "email", "mobile",
			"description", "spaceQuota", "maxVersions", "teamSpaceMaxNum", "departmentName" };

	private static final String EXCEL_2003 = ".xls";

	private static final String LOCAL_ZH_CN = "zh_CN";

	private static final String LOCAL_ZH = "zh";

	private static final String CHARSET_UTF8 = "UTF-8";

	private static final int DEFAULT_EXPORT_USER_SIZE = 50000;

	private static final int MAX_DEFAULT_EXPORT_USER_SIZE = 500000;

	public static final String EMPLOYEES_EXPORT = "0";

	//perhaps useless method ? --
	@Override
	public void exportEmployeeList(HttpServletRequest request, HttpServletResponse response,
			EnterpriseUser enterpriseUser, String id) throws IOException {
		Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
		String language = locale.getLanguage();
		String filename = "";
		if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language)) {
			locale = Locale.CHINA;
			filename = OperateDescriptionType.USER_INFORMATION_LIST.getDetails(locale, null);
			filename = java.net.URLEncoder.encode(filename, CHARSET_UTF8);
		} else {
			locale = Locale.ENGLISH;
			filename = OperateDescriptionType.USER_INFORMATION_LIST.getDetails(locale, null);
		}
		String fileHeaderStr = OperateDescriptionType.EMPLOYEES_BULB.getDetails(locale, null);
		String[] fileHeader = fileHeaderStr.split("@");
		response.setContentType("application/msexcel;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename + getUtcTime() + EXCEL_2003);
		HSSFWorkbook hwb = new HSSFWorkbook();
		int length = fileHeader.length;

		if (!id.equals(EMPLOYEES_EXPORT)) {
			HSSFSheet sheet = setExcelHeader(hwb, fileHeader, length);
			List<ImportEnterpriseUser> userPage = getErrDataUsers(id);
			importUserToExcel(userPage, sheet);
		} else {
			length--;
			int total;
			UserLdap userLdap = new UserLdap();
			if (StringUtils.isNotBlank(enterpriseUser.getUserDn())) {
				HttpSession session = request.getSession();
				String sessionId = session.getId();
				ldapUserManager.insertLdapUser(sessionId, enterpriseUser.getUserDn(), enterpriseUser.getUserSource());
				userLdap.setAuthServerId(enterpriseUser.getUserSource());
				userLdap.setDn(enterpriseUser.getUserDn());
				userLdap.setSessionId(sessionId);
				enterpriseUser.setName(enterpriseUser.getDescription());
				total = userLdapDAO.getFilterdCount(userLdap, enterpriseUser);
			} else {
				total = enterpriseUserService.getFilterdCount(enterpriseUser.getDescription(),
						enterpriseUser.getUserSource(), enterpriseUser.getEnterpriseId(),null);
			}

			if (total > MAX_DEFAULT_EXPORT_USER_SIZE) {
				total = MAX_DEFAULT_EXPORT_USER_SIZE;
			}
			HSSFSheet sheet;
			Limit limit;
			List<EnterpriseUser> userPage;
			for (long i = 0; i < total; i += DEFAULT_EXPORT_USER_SIZE) {
				sheet = setExcelHeader(hwb, fileHeader, length);
				limit = new Limit();
				limit.setOffset(i);
				limit.setLength(DEFAULT_EXPORT_USER_SIZE);
				// List<ImportEnterpriseUser> usersPage = null;
				if (StringUtils.isNotBlank(enterpriseUser.getUserDn())) {
					userPage = userLdapDAO.getFilterd(userLdap, enterpriseUser, null, limit);
				} else {
					userPage = enterpriseUserService.getFilterd(enterpriseUser.getDescription(),
							enterpriseUser.getUserSource(),null, enterpriseUser.getEnterpriseId(), null, limit);
				}

				userToExcel(userPage, sheet);
			}
		}
		hwb.write(response.getOutputStream());
		response.getOutputStream().flush();
	}

	@Override
	public List<ImportEnterpriseUser> parseExcelFile2003(InputStream excelFile, Locale locale) throws BusinessException {
		List<ImportEnterpriseUser> results = new ArrayList<ImportEnterpriseUser>(10);
		try {
			POIFSFileSystem pfs = new POIFSFileSystem(excelFile);
			HSSFWorkbook workbook = new HSSFWorkbook(pfs);
			HSSFSheet sheet = workbook.getSheetAt(0);
			if (null == sheet) {
				return null;
			}
			int totalRow = sheet.getLastRowNum();

			if (totalRow == 0) {
				totalRow = sheet.getPhysicalNumberOfRows();
			}
			Row row;
			ImportEnterpriseUser importUser;
			for (int i = 1; i <= totalRow; i++) {
				row = sheet.getRow(i);
				importUser = getUserByExcel2003(row, i, locale);
				results.add(importUser);
			}
		} catch (IOException e) {
			LOGGER.error(e.toString());
		} finally {
			IOUtils.closeQuietly(excelFile);
		}
		return results;
	}

	@Override
	public List<ImportEnterpriseUser> parseExcelFile2007(InputStream excelFile, Locale locale) throws BusinessException {
		List<ImportEnterpriseUser> results = new ArrayList<ImportEnterpriseUser>(10);
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(excelFile);

			XSSFSheet sheet = workBook.getSheetAt(0);
			if (null == sheet) {
				return null;
			}

			int totalRow = sheet.getLastRowNum();

			if (totalRow == 0) {
				totalRow = sheet.getPhysicalNumberOfRows();
			}
			Row row;
			ImportEnterpriseUser importUser;
			for (int i = 1; i <= totalRow; i++) {
				row = sheet.getRow(i);
				importUser = getUserByExcel2007(row, i, locale);
				results.add(importUser);
			}
		} catch (IOException e) {
			LOGGER.error(e.toString());
		} finally {
			IOUtils.closeQuietly(excelFile);
		}
		return results;
	}

	@Transactional(propagation = Propagation.NEVER)
	@Override
	public void importUserListToDB(List<ImportEnterpriseUser> allUsers, LogOwner owner, Locale locale) {
		LOGGER.info("begin to import user");
		ExcelImport tempExcelImport = new ExcelImport();
		tempExcelImport.setAuthServerId(owner.getAuthServerId());
		tempExcelImport.setEnterpriseId(owner.getEnterpriseId());
		String id = excelImportService.insert(tempExcelImport);
		if (id == null) {
			LOGGER.error("More than " + ExcelImport.MAX_IMPORT + " Importing data");
			return;
		}
		String[] description = new String[] { owner.getDescription() };
		adminLogManager.saveAdminLog(owner, AdminLogType.KEY_START_IMPORT, description);

		ImportEnterpriseUserRecord record = buildEnterpriseUser();

		List<Department> deptList = departmentService.listAllDepartmentByEnterpriseId(owner.getEnterpriseId());
		HashMap<String, Long> deptMap = new HashMap<>();
		for(Department d : deptList) {
			deptMap.put(d.getName(), d.getDepartmentId());
		}

		AuthApp app = authAppService.getDefaultWebApp();
		owner.setAppId(app.getAuthAppId());

		for (ImportEnterpriseUser importUser : allUsers) {
			//设置默认APP, 从EXCEL中导入时，并没有导入APP信息
			importUser.setAppId(app.getAuthAppId());

			//设置部门信息
			if(importUser.getDepartmentName() != null && deptMap.get(importUser.getDepartmentName()) != null) {
				importUser.setDepartmentId(deptMap.get(importUser.getDepartmentName()));
			}

			importUserToBD(importUser, owner, record);
		}

		String[] desc = new String[] { owner.getDescription() };

		String total = record.getNewUserNumber() + record.getUpdateUserNumber() + record.getFailUserNumber() + "";

		String accountTotal = record.getNewAccountUser() + record.getUpdateAccountUser() + record.getFailAccountUser() + "";

		description = new String[] { total, record.getNewUserNumber() + "", +record.getUpdateUserNumber() + "",
				+record.getFailUserNumber() + "", accountTotal + "", +record.getNewAccountUser() + "",
				+record.getUpdateAccountUser() + "", +record.getFailAccountUser() + "" };

		adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ENTERPRISE_USER_IMPORT_REPORT, description);
		adminLogManager.saveAdminLog(owner, AdminLogType.KEY_IMPORT_COMPLETE, desc);
		ExcelImport excelImport = new ExcelImport();
		excelImport.setId(id);
		excelImport.setStatus(ExcelImport.IMPORT_SUCCESS);
		excelImportService.update(excelImport);
		LOGGER.info("import user end");
	}

	private void importUserToBD(ImportEnterpriseUser importUser, LogOwner owner, ImportEnterpriseUserRecord record) {
		try {
			EnterpriseUser user = new EnterpriseUser();

			user.setName(importUser.getName());
			user.setAlias(importUser.getAlias());
			user.setEmail(importUser.getEmail());
			user.setMobile(importUser.getMobile());
			user.setDescription(importUser.getDescription());
			user.setStaffNo(importUser.getStaffNo());
			user.setUserSource(owner.getAuthServerId());
			user.setEnterpriseId(owner.getEnterpriseId());
			EnterpriseUser.checkCreateUserParament(user);
			checkCreateUserParament(user.getName());

			//生成用户
			long userId = createLocalUser(user, owner, record);
			user.setId(userId);
			//EnterpriseUser使用departmentId与Department关联，不需要使用UserDepartment表。
//			if(enterpriseUserManager.checkOrganizeEnabled(enterpriseUser.getEnterpriseId())){
//				checkAndSetDepartMent(user);
//			}
			
//			departmentService.getDeptById(id);
			
			
			UserAccount account = new UserAccount();
			account.setUserId(user.getId());
			account.setEnterpriseId(user.getEnterpriseId());
			account.setMaxVersions(importUser.getMaxVersions());
			account.setSpaceQuota(importUser.getSpaceQuota());
			account.setTeamSpaceMaxNum(importUser.getTeamSpaceMaxNum());
			account.setStatus(UserAccount.INT_STATUS_ENABLE);

			//生成账户
			createUserAccount(user, owner, account, record);
		} catch (ExistUserConflictException e) {
			int failUser = record.getFailUserNumber();
			record.setFailUserNumber(++failUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_LOGIN_NAME_EMAIL_EXIST, new String[] { importUser.getName() });
		} catch (AdAuthUserConflictException e) {
			int failUser = record.getFailUserNumber();
			record.setFailUserNumber(++failUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADAUT_HUSER_CONFLICT_ERROR, new String[] { importUser.getName() });
		} catch (NoSuchAuthServerException e) {
			int failAccountUser = record.getFailAccountUser();
			record.setFailAccountUser(++failAccountUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.NON_EXISTENT_APP, new String[] { importUser.getName() , importUser.getAppId()});
		} catch (InvalidAppParamterException e) {
			int failAccountUser = record.getFailAccountUser();
			record.setFailAccountUser(++failAccountUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INVALID_APP_PARAM, new String[] {importUser.getName() , importUser.getAppId()});
		} catch (LocalAuthException e) {
			int failAccountUser = record.getFailAccountUser();
			record.setFailAccountUser(++failAccountUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_NOT_SUPPORT_LOCAL_AUTH, new String[] { importUser.getAppId()});
		} catch (ExistAccountUserConflictException e) {
			int failAccountUser = record.getFailAccountUser();
			record.setFailAccountUser(++failAccountUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXIST_ACCOUNT_USER_CONFLICT, new String[] {importUser.getAppId()});
		} catch (InvalidVersionsException e) {
			int failUser = record.getFailUserNumber();
			record.setFailUserNumber(++failUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXCEL_INVALID_VERSION, new String[] {importUser.getName()});
		} catch (InvalidParamterException e) {
			int failUser = record.getFailUserNumber();
			record.setFailUserNumber(++failUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_IMPORT_USER_EXCEL_INVALID_PARAMTER, new String[] {importUser.getName()});
		} catch (InternalServerErrorException e) {
			int failUser = record.getFailUserNumber();
			record.setFailUserNumber(++failUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXCEL_SERVER_ERROR, new String[] {importUser.getName()});
		} catch (ShaEncryptException e) {
			int failUser = record.getFailUserNumber();
			record.setFailUserNumber(++failUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXCEL_ENCRYPT_ERROR, new String[] {importUser.getName()});
		} catch (NoSuchRegionException e) {
			int failUser = record.getFailUserNumber();
			record.setFailUserNumber(++failUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXCEL_NOSUCHREGION_ERROR, new String[] {importUser.getName()});
		} catch (ExceedQuotaException e) {
			int failAccountUser = record.getFailAccountUser();
			record.setFailAccountUser(++failAccountUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXCEED_MAX_USER_ERROR, new String[] {importUser.getName() , importUser.getAppId()});
		} catch (NoSuchItemException e) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_IMPORT_UR_DEPT_ERROR, new String[] {importUser.getName()});
		}catch (Exception e) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXCEL_OTHER_ERROR, new String[] {importUser.getName()});
		}
	}

	private boolean isUserValid(List<ImportEnterpriseUser> errorUserList, ImportEnterpriseUser importUser) {
		if (null == importUser) {
			return false;
		}
		if (!importUser.isParseSuccess()) {
			errorUserList.add(importUser);
			return false;
		}
		return true;
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private void createUserAccount(EnterpriseUser user, LogOwner owner, UserAccount account, ImportEnterpriseUserRecord record) {
		boolean flag = userAccountManager.createUserAccount(user, owner.getAppId(), account);
		if (flag) {
			int accountUser = record.getNewAccountUser();
			record.setNewAccountUser(++accountUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CREATE_ACCOUNT_USER, new String[] { user.getName(), owner.getAppId() });
		} else {
			int updateAccountUser = record.getUpdateAccountUser();
			record.setUpdateAccountUser(++updateAccountUser);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDATE_ACCOUNT_USER, new String[] { user.getName(), owner.getAppId() });
		}
	}

	private long createLocalUser(EnterpriseUser user, LogOwner owner, ImportEnterpriseUserRecord record) throws IOException {
		String[] description;
		EnterpriseUserEntity userEntity = enterpriseUserManager.createLocalUser(user);
		owner.setEnterpriseId(userEntity.getEnterpriseId());

		if (userEntity.getFlag()) {
			description = new String[] { user.getName() };
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_IMPROT_USER, description);
			int newUser = record.getNewUserNumber();
			record.setNewUserNumber(++newUser);
		}
		if (!userEntity.getFlag()) {
			description = new String[] { user.getName() };
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXECL_UPDATE_USER, description);
			int updateUser = record.getUpdateUserNumber();
			record.setUpdateUserNumber(++updateUser);
		}

		return userEntity.getId();
	}

	private List<ImportEnterpriseUser> getErrDataUsers(String id) {
		ExcelImport user = excelImportService.getById(id);
		byte[] bytes = user.getErrData();
		return byteToList(bytes);
	}

	private ImportEnterpriseUser getUserByExcel2003(Row row, int rowNumber, Locale locale) {
		ImportEnterpriseUser user = new ImportEnterpriseUser();
		boolean success = true;
		HSSFCell cell;
		Object value;
		for (int i = 0; i < EXCEL_TEMPLATE_FILE_HEADER_PRO.length; i++) {
			try {
				cell = (HSSFCell) row.getCell(i);
				if (cell == null) {
					// fillUserIntegerPara(i, user);
					continue;
				}
				value = getValueByHSSFCellType2003(cell);
				BeanUtils.setProperty(user, EXCEL_TEMPLATE_FILE_HEADER_PRO[i], value);
			} catch (IllegalAccessException e2) {
				success = false;
				LOGGER.warn("read excel IllegalAccessException cell:" + i + " row:" + rowNumber);
			} catch (InvocationTargetException e2) {
				success = false;
				LOGGER.warn("read excel InvocationTargetException cell:" + i + " row:" + rowNumber);
			}
		}

		user.setParseSuccess(success);
		if (!success) {
			user.setErrorCode(OperateDescriptionType.EXCEL_PARSE_FAILED.getDetails(locale, null));
		}

		return user;
	}

	private Object getValueByHSSFCellType2003(HSSFCell cell) {
		Object value = null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			value = cell.getRichStringCellValue().getString();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				value = cell.getDateCellValue();
			} else {
				value = ((Double) cell.getNumericCellValue()).longValue();
			}
			break;
		default:
			break;
		}
		return value;
	}

	private ImportEnterpriseUser getUserByExcel2007(Row row, int rowNumber, Locale locale) {
		ImportEnterpriseUser user = new ImportEnterpriseUser();
		boolean success = true;
		int i = 0;
		Object value = null;
		for (; i < EXCEL_TEMPLATE_FILE_HEADER_PRO.length; i++) {
			try {
				XSSFCell cell = (XSSFCell) row.getCell(i);

				if (cell == null) {
					continue;
				}
				value = getValueByHSSFCellType2007(cell);
				BeanUtils.setProperty(user, EXCEL_TEMPLATE_FILE_HEADER_PRO[i], value);
			} catch (IllegalAccessException e) {
				success = false;
				LOGGER.warn("read excel IllegalAccessException cell:" + i + " row:" + rowNumber);
			} catch (InvocationTargetException e) {
				success = false;
				LOGGER.warn("read excel InvocationTargetException cell:" + i + " row:" + rowNumber);
			}
		}
		if (!success) {
			user.setErrorCode(OperateDescriptionType.EXCEL_PARSE_FAILED.getDetails(locale, null));
		}
		user.setParseSuccess(success);

		return user;
	}

	private Object getValueByHSSFCellType2007(XSSFCell cell) {
		Object value = null;
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_STRING:
			value = cell.getRichStringCellValue().getString();
			break;
		case XSSFCell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				value = cell.getDateCellValue();
			} else {
				long longValue = ((Double) cell.getNumericCellValue()).longValue();
				value = longValue;
			}
			break;
		default:
			break;
		}
		return value;
	}

	private void importUserToExcel(List<ImportEnterpriseUser> userList, HSSFSheet sheet) {
		if (null == userList || userList.size() <= 0) {
			return;
		}

		int i = 1;
		HSSFRow hRow;
		for (ImportEnterpriseUser user : userList) {
			hRow = sheet.createRow(i);
//			createUserCell(hRow, user.getUser(), user.getErrorCode());
			i++;
		}
	}

	private void userToExcel(List<EnterpriseUser> userList, HSSFSheet sheet) {
		if (null == userList || userList.size() <= 0) {
			return;
		}
		int i = 1;
		HSSFRow hRow;
		for (EnterpriseUser user : userList) {
			hRow = sheet.createRow(i);
			createUserCell(hRow, user, null);
			i++;
		}
	}

	private void createUserCell(HSSFRow row, EnterpriseUser user, String errData) {
		if (user != null) {
			row.createCell(0).setCellValue(user.getName());
			row.createCell(1).setCellValue(user.getAlias());
			row.createCell(2).setCellValue(user.getEmail());
			row.createCell(3).setCellValue(user.getMobile());
			row.createCell(4).setCellValue(user.getDescription());
		}
		if (StringUtils.isNotEmpty(errData)) {
			row.createCell(5).setCellValue(errData);
		}
	}

	private void createCell(HSSFWorkbook wb, HSSFRow row, int col, String val) {
		HSSFCell cell = row.createCell(col);
		cell.setCellValue(val);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
		cell.setCellStyle(cellStyle);
	}

	private String getUtcTime() {

		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = sdf.format(currentDate);
		return utcTime;
	}

	@SuppressWarnings("unchecked")
	private List<ImportEnterpriseUser> byteToList(byte[] bytes) {
		List<ImportEnterpriseUser> list = new ArrayList<ImportEnterpriseUser>(10);
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);
			list = (List<ImportEnterpriseUser>) oi.readObject();
			bi.close();
			oi.close();
		} catch (IOException e) {
			LOGGER.error("The byte to list error deserialization");
		} catch (ClassNotFoundException e) {
			LOGGER.error("The path bi not found");
		}
		return list;
	}

	private HSSFSheet setExcelHeader(HSSFWorkbook hwb, String[] fileHeader, int length) {
		HSSFSheet sheet = hwb.createSheet();
		HSSFRow hRow = sheet.createRow(0);
		for (int i = 0; i < length; i++) {
			createCell(hwb, hRow, i, fileHeader[i]);
		}

		return sheet;
	}

	private ImportEnterpriseUserRecord buildEnterpriseUser() {
		ImportEnterpriseUserRecord record = new ImportEnterpriseUserRecord();
		record.setNewUserNumber(0);
		record.setUpdateUserNumber(0);
		record.setFailUserNumber(0);
		record.setFailAccountUser(0);
		record.setNewAccountUser(0);
		record.setUpdateAccountUser(0);
		return record;
	}

	protected Long checkAdminAndGetId() {
		Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
		if (null == sessAdmin) {
			throw new ForbiddenException("sessAdmin is null");
		}
		Long adminEnterpriseId = sessAdmin.getEnterpriseId();
        if (null == adminEnterpriseId){
            throw new InvalidParamterException("adminEnterpriseId is null");
        }		
		return adminEnterpriseId;
	}


	private void checkCreateUserParament(String name) {
		String nameRegex = "^(?!.*((<)|(>)|(\\/)|(\\\\))).*$";
		String regex = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(name);
		if (matcher.matches()) {
			throw new InvalidParamterException();
		}
		Pattern namePattern = Pattern.compile(nameRegex);
		Matcher nameMatcher = namePattern.matcher(name);
		if (!nameMatcher.matches()) {
			throw new InvalidParamterException();
		}

	}
}
