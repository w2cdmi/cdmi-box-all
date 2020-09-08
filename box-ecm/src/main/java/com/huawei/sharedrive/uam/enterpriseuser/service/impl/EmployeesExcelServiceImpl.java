package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import com.huawei.it.support.usermanage.util.Base64;
import com.huawei.sharedrive.uam.adminlog.domain.OperateDescriptionType;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseuser.dao.UserLdapDAO;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ExcelImport;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ImportEnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EmployeesExcelService;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.enterpriseuser.service.ExcelImportService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.BundleUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EmployeesExcelServiceImpl implements EmployeesExcelService {
	private static Logger logger = LoggerFactory.getLogger(EmployeesExcelServiceImpl.class);

	@Autowired
	private EnterpriseUserService enterpriseUserService;

	@Autowired
	private ExcelImportService excelImportService;

	@Autowired
	private LdapUserManager ldapUserManager;
	
	@Autowired
	private EnterpriseManager enterpriseManager;

	@Autowired
	private UserLdapDAO userLdapDAO;

	private static final String EXCEL_2003 = ".xls";

	private static final String LOCAL_ZH_CN = "zh_CN";

	private static final String LOCAL_ZH = "zh";

	private static final String CHARSET_UTF8 = "UTF-8";

	private static final int DEFAULT_EXPORT_USER_SIZE = 50000;

	private static final int MAX_DEFAULT_EXPORT_USER_SIZE = 500000;

	public static final String EMPLOYEES_EXPORT = "0";
	
	private Locale localeInfo;
	private static final String RESOURCE_FEILE = "messages";
    

	@Override
	public void downloadEmployeeInfoTemplateFile(HttpServletRequest request, HttpServletResponse response, long enterpriseId)
			throws IOException {
		Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
		String language = locale.getLanguage();
		String filename = "";
		if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language)) {
			locale = Locale.CHINA;
			filename = OperateDescriptionType.USER_INFORMATION_TEMPLATE.getDetails(locale, null);
		} else {
			locale = Locale.ENGLISH;
			filename = OperateDescriptionType.USER_INFORMATION_TEMPLATE.getDetails(locale, null);
		}

		String fileHeaderStr ="";
		if(enterpriseManager.checkOrganizeEnabled(enterpriseId)){
			fileHeaderStr=OperateDescriptionType.IMPROT_EMPLOYEES_ORG_BULB.getDetails(locale, null);
		}else{
			fileHeaderStr=OperateDescriptionType.IMPROT_EMPLOYEES_BULB.getDetails(locale, null);
		}
		String[] fileHeader = fileHeaderStr.split("@");
		response.setContentType("application/msexcel;charset=utf-8");
		if (request.getHeader("USER-AGENT").toLowerCase().indexOf("firefox") > 0) {
			filename = "=?UTF-8?B?" + (new String(Base64.base64Encode(filename.getBytes("UTF-8")))) + "?=";
//			filename = new String(filename.getBytes("UTF-8"),"iso-8859-1");//
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename+"\"");
		} else{
			filename = java.net.URLEncoder.encode(filename, CHARSET_UTF8);
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		}
		HSSFWorkbook hwb = new HSSFWorkbook();
		HSSFSheet sheet = hwb.createSheet();
		HSSFRow hRow = sheet.createRow(0);
		int length = fileHeader.length - 1;
		for (int i = 0; i < length; i++) {
			createCell(hwb, hRow, i, fileHeader[i]);
		}
		hwb.write(response.getOutputStream());

	}

	@Override
	public void exportEmployeeList(HttpServletRequest request, HttpServletResponse response,
			EnterpriseUser enterpriseUser, String id) throws IOException {
		Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
		localeInfo=locale;
		String language = locale.getLanguage();
		String filename = "";
		if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language)) {
			locale = Locale.CHINA;
			filename = OperateDescriptionType.USER_INFORMATION_LIST.getDetails(locale, null);
		} else {
			locale = Locale.ENGLISH;
			filename = OperateDescriptionType.USER_INFORMATION_LIST.getDetails(locale, null);
		}
		String fileHeaderStr = "";
		//If enterprise does not support department management
		if(enterpriseManager.checkOrganizeEnabled(enterpriseUser.getEnterpriseId())){
			fileHeaderStr=OperateDescriptionType.EMPLOYEES_ORG_BULB.getDetails(locale, null);
		}else{
			fileHeaderStr=OperateDescriptionType.EMPLOYEES_BULB.getDetails(locale, null);
		}
		String[] fileHeader = fileHeaderStr.split("@");
		
		response.setContentType("application/msexcel;charset=utf-8");
		if (request.getHeader("USER-AGENT").toLowerCase().indexOf("firefox") > 0) {
			filename = "=?UTF-8?B?" + (new String(Base64.base64Encode(filename.getBytes("UTF-8")))) + "?=";
//			filename = new String(filename.getBytes("UTF-8"),"iso-8859-1");//
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename+ getUtcTime() + EXCEL_2003 + "\"");
		} else{
			filename = java.net.URLEncoder.encode(filename, CHARSET_UTF8);
			response.setHeader("Content-Disposition", "attachment; filename=" + filename + getUtcTime() + EXCEL_2003);
		}

		HSSFWorkbook hwb = new HSSFWorkbook();
		int length = fileHeader.length;

		if (!id.equals(EMPLOYEES_EXPORT)) {
			HSSFSheet sheet = setExcelHeader(hwb, fileHeader, length);

			List<ImportEnterpriseUser> userList = getErrDataUsers(id);
			exportUserToExcel(sheet, userList);
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

	private List<ImportEnterpriseUser> getErrDataUsers(String id) {
		ExcelImport user = excelImportService.getById(id);
		byte[] bytes = user.getErrData();
		return byteToList(bytes);
	}

	private void exportUserToExcel(HSSFSheet sheet, List<ImportEnterpriseUser> userList) {
		if (null == userList || userList.size() <= 0) {
			return;
		}

		int i = 1;
		HSSFRow hRow;
		for (ImportEnterpriseUser user : userList) {
			hRow = sheet.createRow(i);
			createEnterpriseUserCell(hRow, user);
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
		//set default dept info 
		int dynamicIndx =4;
		if (user != null) {
			
			row.createCell(0).setCellValue(user.getName());
			row.createCell(1).setCellValue(user.getAlias());
			row.createCell(2).setCellValue(user.getEmail());
			row.createCell(3).setCellValue(user.getMobile());
			row.createCell(dynamicIndx).setCellValue(user.getDescription());
		}
		if (StringUtils.isNotEmpty(errData)) {
			row.createCell(dynamicIndx+1).setCellValue(errData);
		}
	}

	private void createEnterpriseUserCell(HSSFRow row, ImportEnterpriseUser user) {
		int dynamicIndx =4;
		if (user != null) {
			if(enterpriseManager.checkOrganizeEnabled(user.getEnterpriseId())){
				String deptName;
				if(StringUtils.isBlank(user.getDepartmentName())){
					BundleUtil.addBundle(RESOURCE_FEILE,Locale.getAvailableLocales());
					if (LOCAL_ZH_CN.equals(localeInfo.getLanguage()) || LOCAL_ZH.equals(localeInfo.getLanguage())) {
						localeInfo = Locale.CHINA;
						}else{
							localeInfo = Locale.ENGLISH;
						}
					deptName=BundleUtil.getText(RESOURCE_FEILE,localeInfo, "enterprise.employee.dept.unspecified");
				}else{
					deptName=user.getDepartmentName();
				}
				row.createCell(4).setCellValue(deptName);
				dynamicIndx++;
			}
			row.createCell(0).setCellValue(user.getName());
			row.createCell(1).setCellValue(user.getAlias());
			row.createCell(2).setCellValue(user.getEmail());
			row.createCell(3).setCellValue(user.getMobile());
			row.createCell(dynamicIndx).setCellValue(user.getDescription());
			row.createCell(dynamicIndx+1).setCellValue(user.getAppId());
			row.createCell(dynamicIndx+2).setCellValue((user.getSpaceQuota() == null ? "" : user.getSpaceQuota()) + "");
			row.createCell(dynamicIndx+3).setCellValue((user.getMaxVersions() == null ? "" : user.getMaxVersions()) + "");
			row.createCell(dynamicIndx+4).setCellValue((user.getTeamSpaceMaxNum() == null ? "" : user.getTeamSpaceMaxNum()) + "");


			if (!StringUtils.isNotEmpty(user.getErrorCode())) {
				row.createCell(dynamicIndx+5).setCellValue(user.getErrorCode());
			}
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
			logger.error("The byte to list error deserialization");
		} catch (ClassNotFoundException e) {
			logger.error("The path bi not found");
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
}
