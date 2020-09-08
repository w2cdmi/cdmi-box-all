package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.huawei.sharedrive.uam.adminlog.domain.OperateDescriptionType;
import com.huawei.sharedrive.uam.enterprise.domain.ImportNetRegionIp;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIpComparator;
import com.huawei.sharedrive.uam.enterprise.domain.NetWorkRegionIpExcelImport;
import com.huawei.sharedrive.uam.enterprise.domain.NetworkIpExcelEnum;
import com.huawei.sharedrive.uam.enterprise.manager.NetRegionManager;
import com.huawei.sharedrive.uam.enterprise.service.NetworkRegionExcelService;
import com.huawei.sharedrive.uam.enterprise.service.NetworkRegionIpExcelImportService;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.ExistNetworkRegionIpConflictException;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.InvalidVersionsException;

import pw.cdmi.core.utils.IpUtils;
import pw.cdmi.core.utils.UUIDUtils;

@Service
public class NetworkRegionExcelServiceImpl implements NetworkRegionExcelService
{
    private static Logger logger = LoggerFactory.getLogger(NetworkRegionExcelServiceImpl.class);
    
    @Autowired
    private NetworkRegionIpExcelImportService regionIpExcelImportService;
    
    @Autowired
    private NetRegionManager manager;
    
    private static final String[] EXCEL_TEMPLATE_NET_REGION_IP = new String[]{"netRegionName",
        "netRegionDesc", "ipStart", "ipEnd"};
    
    private static final String EXCEL_2007 = ".xlsx";
    
    private static final String LOCAL_ZH_CN = "zh_CN";
    
    private static final String LOCAL_ZH = "zh";
    
    private static final String CHARSET_UTF8 = "UTF-8";
    
    @Override
    public void downloadNetRegionIpTemplateFile(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        String language = locale.getLanguage();
        String filename = "";
        if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language))
        {
            locale = Locale.CHINA;
            filename = NetworkIpExcelEnum.NET_REGION_IP_TEMPLATE.getDetails(locale, null);
            filename = java.net.URLEncoder.encode(filename, CHARSET_UTF8);
        }
        else
        {
            locale = Locale.ENGLISH;
            filename = NetworkIpExcelEnum.NET_REGION_IP_TEMPLATE.getDetails(locale, null);
        }
        String fileHeaderStr = NetworkIpExcelEnum.NETREGIONIP_BULB.getDetails(locale, null);
        String[] fileHeader = fileHeaderStr.split("@");
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + EXCEL_2007);
        XSSFWorkbook hwb = new XSSFWorkbook();
        XSSFSheet sheet = hwb.createSheet();
        XSSFRow hRow = sheet.createRow(0);
        int length = fileHeader.length - 1;
        for (int i = 0; i < length; i++)
        {
            createCell(hwb, hRow, i, fileHeader[i]);
        }
        hwb.write(response.getOutputStream());
        
    }
    
    @Override
    public String exportNetRegionList(long accountId, HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        String utcTime = getUtcTime();
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        String language = locale.getLanguage();
        String filename = "";
        if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language))
        {
            locale = Locale.CHINA;
            filename = NetworkIpExcelEnum.NET_REGION_IP_EXPORT_EXCEL_NAME.getDetails(locale, null);
            filename = java.net.URLEncoder.encode(filename, CHARSET_UTF8);
        }
        else
        {
            locale = Locale.ENGLISH;
            filename = NetworkIpExcelEnum.NET_REGION_IP_EXPORT_EXCEL_NAME.getDetails(locale, null);
        }
        String fileHeaderStr = NetworkIpExcelEnum.NETREGIONIP_EXPORT_BULB.getDetails(locale, null);
        String[] fileHeader = fileHeaderStr.split("@");
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + utcTime + EXCEL_2007);
        XSSFWorkbook hwb = new XSSFWorkbook();
        int length = fileHeader.length;
        
        NetRegionIp netRegionIp = new NetRegionIp();
        netRegionIp.setAccountId(accountId);
        List<NetRegionIp> listNetRegion = manager.getListRegionIp(netRegionIp);
        StringBuffer buf = new StringBuffer();
        if (!listNetRegion.isEmpty())
        {
            NetRegionIp netregionIp;
            for (int i = 0; i < listNetRegion.size(); i++)
            {
                netregionIp = listNetRegion.get(i);
                if (netregionIp != null)
                {
                    buf.append(netregionIp.getId());
                }
                if (i < listNetRegion.size() - 1)
                {
                    buf.append(',');
                }
            }
        }
        XSSFSheet sheet = setExcelHeader(hwb, fileHeader, length - 1);
        
        netRegionToExcel(listNetRegion, sheet);
        hwb.write(response.getOutputStream());
        response.getOutputStream().flush();
        return buf.toString();
    }
    
    @Override
    public List<ImportNetRegionIp> parseFile2003ForNetRegionIp(InputStream excelFile, Locale locale)
        throws BusinessException
    {
        List<ImportNetRegionIp> results = new ArrayList<ImportNetRegionIp>(10);
        try
        {
            POIFSFileSystem pfs = new POIFSFileSystem(excelFile);
            HSSFWorkbook workbook = new HSSFWorkbook(pfs);
            HSSFSheet sheet = workbook.getSheetAt(0);
            if (null == sheet)
            {
                return null;
            }
            int totalRow = sheet.getLastRowNum();
            
            if (totalRow == 0)
            {
                totalRow = sheet.getPhysicalNumberOfRows();
            }
            Row row;
            ImportNetRegionIp netRegionIp;
            for (int i = 1; i <= totalRow; i++)
            {
                row = sheet.getRow(i);
                netRegionIp = getUserByExcel2003ForNetRegionIp(row, i);
                results.add(netRegionIp);
            }
        }
        catch (IOException e)
        {
            logger.error(e.toString());
        }
        finally
        {
            IOUtils.closeQuietly(excelFile);
        }
        return results;
    }
    
    @Override
    public List<ImportNetRegionIp> parseFile2007ForNetRegionIp(InputStream excelFile, Locale locale)
        throws BusinessException
    {
        List<ImportNetRegionIp> results = new ArrayList<ImportNetRegionIp>(10);
        try
        {
            XSSFWorkbook workBook = new XSSFWorkbook(excelFile);
            
            XSSFSheet sheet = workBook.getSheetAt(0);
            if (null == sheet)
            {
                return null;
            }
            
            int totalRow = sheet.getLastRowNum();
            
            if (totalRow == 0)
            {
                totalRow = sheet.getPhysicalNumberOfRows();
            }
            Row row = null;
            NetRegionIp nr = new NetRegionIp();
            List<NetRegionIp> list = manager.getFilterdList(nr);
            Collections.sort(list, new NetRegionIpComparator());
            for (int i = 1; i <= totalRow; i++)
            {
                
                row = sheet.getRow(i);
                if (row != null)
                {
                    ImportNetRegionIp netRegionIp = getUserByExcel2007ForNetRegionIp(row, i, list);
                    results.add(netRegionIp);
                }
            }
        }
        catch (IOException e)
        {
            logger.error(e.toString());
        }
        finally
        {
            IOUtils.closeQuietly(excelFile);
        }
        return results;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private void importNetRegionIpToBD(List<ImportNetRegionIp> importUserList, ImportNetRegionIp importUser,
        long appId, long accountId, Locale locale)
    {
        
        if (null == importUser)
        {
            return;
        }
        if (!importUser.isParseSucess())
        {
            importUserList.add(importUser);
            return;
        }
        NetRegionIp netRegionIp = null;
        try
        {
            NetRegion netRegion = new NetRegion();
            netRegion.setNetRegionName(importUser.getNetRegionIp().getNetRegionName());
            netRegion.setNetRegionDesc(importUser.getNetRegionIp().getNetRegionDesc());
            netRegion.setAccountId(accountId);
            List<NetRegion> listNetRegion = manager.getFilterdNetRegionList(netRegion);
            
            netRegionIp = importUser.getNetRegionIp();
            netRegionIp.setNetRegionId(appId);
            
            if (listNetRegion.isEmpty())
            {
                long regionIp = manager.create(netRegion);
                netRegionIp.setNetRegionId(regionIp);
                netRegionIp.setAccountId(accountId);
                manager.create(netRegionIp);
            }
            else
            {
                netRegionIp.setNetRegionId(listNetRegion.get(0).getId());
                netRegionIp.setAccountId(accountId);
                manager.create(netRegionIp);
            }
            importUser.setErrorCode(NetworkIpExcelEnum.NETREGIONIP_EXPORT_SUCCEEDED.getDetails(locale, null));
            importUserList.add(importUser);
        }
        catch (InvalidVersionsException e)
        {
            importUser.setErrorCode(OperateDescriptionType.EXCEL_INVALID_VERSION.getDetails(locale, null));
            importUserList.add(importUser);
            return;
        }
        catch (InvalidParamterException e)
        {
            importUser.setErrorCode(OperateDescriptionType.EXCEL_INVALID_PARAMTER.getDetails(locale, null));
            importUserList.add(importUser);
            return;
        }
        catch (InternalServerErrorException e)
        {
            importUser.setErrorCode(OperateDescriptionType.EXCEL_SERVER_ERROR.getDetails(locale, null));
            importUserList.add(importUser);
            return;
        }
        catch (ExistNetworkRegionIpConflictException e)
        {
            importUser.setErrorCode(NetworkIpExcelEnum.NETREGIONIP_EXPORT_EXISTS_CONFLICT.getDetails(locale,
                null));
            importUserList.add(importUser);
            return;
        }
        catch (Exception e)
        {
            importUser.setErrorCode(OperateDescriptionType.EXCEL_OTHER_ERROR.getDetails(locale, null));
            importUserList.add(importUser);
            return;
        }
    }
    
    private ImportNetRegionIp getUserByExcel2003ForNetRegionIp(Row row, int rowNumber)
    
    {
        NetRegionIp netRegionIp = new NetRegionIp();
        ImportNetRegionIp importNetRegionIp = new ImportNetRegionIp();
        boolean isParseSucess = true;
        int i = 0;
        Object value = null;
        for (; i < EXCEL_TEMPLATE_NET_REGION_IP.length; i++)
        {
            try
            {
                HSSFCell cell = (HSSFCell) row.getCell(i);
                if (cell == null)
                {
                    continue;
                }
                
                value = getValueByHSSFCellType2003(cell);
                BeanUtils.setProperty(netRegionIp, EXCEL_TEMPLATE_NET_REGION_IP[i], value);
            }
            catch (IllegalAccessException e)
            {
                isParseSucess = false;
                logger.warn("read excel IllegalAccessException cell:" + i + " row:" + rowNumber);
            }
            catch (InvocationTargetException e)
            {
                isParseSucess = false;
                logger.warn("read excel InvocationTargetException cell:" + i + " row:" + rowNumber);
            }
        }
        importNetRegionIp.setParseSucess(isParseSucess);
        importNetRegionIp.setNetRegionIp(netRegionIp);
        netRegionIp.setIpStartValue(IpUtils.toLong(netRegionIp.getIpStart()));
        netRegionIp.setIpEndValue(IpUtils.toLong(netRegionIp.getIpEnd()));
        return importNetRegionIp;
    }
    
    private Object getValueByHSSFCellType2003(HSSFCell cell)
    {
        Object value = null;
        switch (cell.getCellType())
        {
            case HSSFCell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell))
                {
                    value = cell.getDateCellValue();
                }
                else
                {
                    value = ((Double) cell.getNumericCellValue()).longValue();
                }
                break;
            default:
                break;
        }
        return value;
    }
    
    private ImportNetRegionIp getUserByExcel2007ForNetRegionIp(Row row, int rowNumber, List<NetRegionIp> list)
    {
        NetRegionIp user = new NetRegionIp();
        ImportNetRegionIp importUser = new ImportNetRegionIp();
        boolean isParseSucess = true;
        int i = 0;
        Object value = null;
        for (; i < EXCEL_TEMPLATE_NET_REGION_IP.length; i++)
        {
            try
            {
                XSSFCell cell = (XSSFCell) row.getCell(i);
                
                if (cell == null)
                {
                    continue;
                }
                
                value = getValueByHSSFCellType2007(cell);
                BeanUtils.setProperty(user, EXCEL_TEMPLATE_NET_REGION_IP[i], value);
            }
            catch (IllegalAccessException e)
            {
                isParseSucess = false;
                logger.warn("read excel IllegalAccessException cell:" + i + " row:" + rowNumber);
            }
            catch (InvocationTargetException e)
            {
                isParseSucess = false;
                logger.warn("read excel InvocationTargetException cell:" + i + " row:" + rowNumber);
            }
        }
        long ipStart = IpUtils.toLong(user.getIpStart());
        long ipEnd = IpUtils.toLong(user.getIpEnd());
        
        fillImportUserInfoEx(list, user, importUser, isParseSucess, ipStart, ipEnd);
        return importUser;
    }
    
    private Object getValueByHSSFCellType2007(XSSFCell cell)
    {
        Object value = null;
        switch (cell.getCellType())
        {
            case XSSFCell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell))
                {
                    value = cell.getDateCellValue();
                }
                else
                {
                    value = ((Double) cell.getNumericCellValue()).longValue();
                }
                break;
            default:
                break;
        }
        return value;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void fillImportUserInfoEx(List<NetRegionIp> list, NetRegionIp user, ImportNetRegionIp importUser,
        boolean isParseSucess, long ipStart, long ipEnd)
    {
        importUser.setParseSucess(isParseSucess);
        Date now = new Date();
        user.setCreatedAt(now);
        user.setModifiedAt(now);
        user.setIpStartValue(ipStart);
        user.setIpEndValue(ipEnd);
        
        if (!list.isEmpty())
        {
            NetRegionIp min = list.get(0);
            if (ipStart < min.getIpStartValue() && ipEnd < min.getIpEndValue())
            {
                importUser.setNetRegionIp(user);
            }
            
            NetRegionIp max = list.get(list.size() - 1);
            if (ipStart > max.getIpStartValue() && ipEnd > max.getIpEndValue())
            {
                importUser.setNetRegionIp(user);
            }
        }
        else
        {
            importUser.setNetRegionIp(user);
        }
    }
    
    private void netRegionToExcel(List<NetRegionIp> list, XSSFSheet sheet)
    {
        if (list.isEmpty())
        {
            return;
        }
        int i = 1;
        XSSFRow hRow;
        for (NetRegionIp nr : list)
        {
            hRow = sheet.createRow(i);
            createNetRegionCell(hRow, nr, null);
            i++;
        }
    }
    
    private void createNetRegionCell(XSSFRow row, NetRegionIp nr, String errData)
    {
        if (nr != null)
        {
            row.createCell(0).setCellValue(nr.getNetRegionName());
            row.createCell(1).setCellValue(nr.getNetRegionDesc());
            row.createCell(2).setCellValue(nr.getIpStart());
            row.createCell(3).setCellValue(nr.getIpEnd());
            row.createCell(4).setCellValue(nr.getErrorCode());
        }
        if (StringUtils.isNotEmpty(errData))
        {
            row.createCell(4).setCellValue(errData);
        }
    }
    
    private void createCell(XSSFWorkbook wb, XSSFRow row, int col, String val)
    {
        XSSFCell cell = row.createCell(col);
        cell.setCellValue(val);
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
        cell.setCellStyle(cellStyle);
    }
    
    private String getUtcTime()
    {
        
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = sdf.format(currentDate);
        return utcTime;
    }
    
    private byte[] listToByteForNetRegionIp(List<ImportNetRegionIp> list)
    {
        byte[] bytes = null;
        try
        {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bo);
            out.writeObject(list);
            bytes = bo.toByteArray();
            bo.close();
            out.close();
        }
        catch (IOException e)
        {
            logger.error("The list to byte error serialization");
        }
        
        return bytes;
    }
    
    private XSSFSheet setExcelHeader(XSSFWorkbook hwb, String[] fileHeader, int length)
    {
        XSSFSheet sheet = hwb.createSheet();
        XSSFRow hRow = sheet.createRow(0);
        for (int i = 0; i < length; i++)
        {
            createCell(hwb, hRow, i, fileHeader[i]);
        }
        
        return sheet;
    }
    
    @Transactional(propagation = Propagation.NEVER)
    @Override
    public void importNetRegionIpListToDB(List<ImportNetRegionIp> allUsers, long appId, Locale locale,
        long accountId)
    {
        logger.info("begin to import user");
        long inserId = regionIpExcelImportService.insert(UUIDUtils.getValueAfterMD5(), accountId);
        NetWorkRegionIpExcelImport ei = regionIpExcelImportService.getById(inserId);
        List<ImportNetRegionIp> resultList = new ArrayList<ImportNetRegionIp>(10);
        for (ImportNetRegionIp importUser : allUsers)
        {
            importNetRegionIpToBD(resultList, importUser, appId, accountId, locale);
        }
        
        if (CollectionUtils.isNotEmpty(resultList))
        {
            ei.setResultData(listToByteForNetRegionIp(resultList));
            ei.setStatus(NetWorkRegionIpExcelImport.IMPORT_FAILED);
        }
        else
        {
            ei.setStatus(NetWorkRegionIpExcelImport.IMPORT_SUCCESS);
        }
        
        int succeededCount = 0;
        
        for (ImportNetRegionIp ir : resultList)
        {
            if (ir.getErrorCode().equals(NetworkIpExcelEnum.NETREGIONIP_EXPORT_SUCCEEDED.getDetails(locale,
                null)))
            {
                succeededCount++;
            }
        }
        
        ei.setCompleteTime(new Date());
        
        int failedCount = allUsers.size() - succeededCount;
        ei.setSucceededCount(succeededCount);
        ei.setFailedCount(failedCount);
        ei.setTotalCount(allUsers.size());
        ei.setAccountId(accountId);
        
        regionIpExcelImportService.update(ei);
        logger.info("end to import user");
    }
    
    @Override
    public boolean isDuplicateTask(long accountId)
    {
        boolean isAllowed = true;
        List<NetWorkRegionIpExcelImport> list = regionIpExcelImportService.getListExcelImport(accountId);
        for (NetWorkRegionIpExcelImport ei : list)
        {
            if (ei.getStatus() == NetWorkRegionIpExcelImport.IMPORTING)
            {
                isAllowed = false;
                continue;
            }
        }
        return isAllowed;
    }
    
    @Override
    public void exportImportExcelResult(HttpServletRequest request, HttpServletResponse response, long id)
        throws IOException
    
    {
        OutputStream output = null;
        try
        {
            String utcTime = getUtcTime();
            
            Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
            String language = locale.getLanguage();
            String filename = "";
            if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language))
            {
                locale = Locale.CHINA;
                filename = NetworkIpExcelEnum.NET_REGION_IP_IMPORT_RESULT_EXPORT.getDetails(locale, null);
                filename = java.net.URLEncoder.encode(filename, CHARSET_UTF8);
            }
            else
            {
                locale = Locale.ENGLISH;
                filename = NetworkIpExcelEnum.NET_REGION_IP_IMPORT_RESULT_EXPORT.getDetails(locale, null);
            }
            String fileHeaderStr = NetworkIpExcelEnum.NETREGIONIP_BULB.getDetails(locale, null);
            String[] fileHeader = fileHeaderStr.split("@");
            response.setContentType("application/msexcel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename + utcTime
                + EXCEL_2007);
            XSSFWorkbook hwb = new XSSFWorkbook();
            int length = fileHeader.length;
            
            XSSFSheet sheet = setExcelHeader(hwb, fileHeader, length);
            List<ImportNetRegionIp> listNetRegion = getNetRegionExcelErrDataUsers(id);
            List<NetRegionIp> list = new ArrayList<NetRegionIp>(10);
            for (ImportNetRegionIp im : listNetRegion)
            {
                im.getNetRegionIp().setErrorCode(im.getErrorCode());
                list.add(im.getNetRegionIp());
            }
            netRegionToExcel(list, sheet);
            output = response.getOutputStream();
            hwb.write(output);
        }
        finally
        {
            IOUtils.closeQuietly(output);
        }
        
    }
    
    private List<ImportNetRegionIp> getNetRegionExcelErrDataUsers(long id)
    {
        NetWorkRegionIpExcelImport excelImport = regionIpExcelImportService.getById(id);
        byte[] bytes = excelImport.getResultData();
        return byteToNetRegionIpList(bytes);
    }
    
    @SuppressWarnings("unchecked")
    private List<ImportNetRegionIp> byteToNetRegionIpList(byte[] bytes)
    {
        List<ImportNetRegionIp> list = new ArrayList<ImportNetRegionIp>(10);
        try
        {
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            list = (List<ImportNetRegionIp>) oi.readObject();
            bi.close();
            oi.close();
        }
        catch (IOException e)
        {
            logger.error("The byte to list error deserialization");
        }
        catch (ClassNotFoundException e)
        {
            logger.error("The byte to list error deserialization");
        }
        
        return list;
    }
}
