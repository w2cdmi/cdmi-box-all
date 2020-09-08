package com.huawei.sharedrive.app.openapi.restv2.statistics;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.SystemTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.statistics.ExcelExportReqeust;
import com.huawei.sharedrive.app.statistics.manager.StatisticsManager;
import com.huawei.sharedrive.app.statistics.service.StatisticsDateUtils;
import io.swagger.annotations.Api;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pw.cdmi.core.exception.InnerException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Controller
@RequestMapping(value = "/api/v2/statistics")
@Api(hidden = true)
public class DataStatisticsAPI<T>
{
    /**
     * default index --0
     */
    private static final Integer DEFAULT_INDEX = 0;
    
    /**
     * sheet headers
     */
    private static final Map<String, List<String>> HEADERS = new HashMap<String, List<String>>(4);
    
    static
    {
        
        HEADERS.put("node", Arrays.asList("day",
            "appId",
            "regionId",
            "fileCount",
            "trashFileCount",
            "deletedFileCount",
            "spaceUsed",
            "trashSpaceUsed",
            "deletedSpaceUsed",
            "addedFileCount",
            "addedTrashFileCount",
            "addedDeletedFileCount",
            "addedSpaceUsed",
            "addedTrashSpaceUsed",
            "addedDeletedSpaceUsed"));
        HEADERS.put("object", Arrays.asList("day",
            "regionId",
            "fileCount",
            "actualFileCount",
            "spaceUsed",
            "actualSpaceUsed",
            "addedFileCount",
            "addedActualFileCount",
            "addedSpaceUsed",
            "addedActualSpaceUsed"));
        HEADERS.put("user", Arrays.asList("day", "appId", "regionId", "userCount", "addedUserCount"));
        HEADERS.put("concurrence_file", Arrays.asList("day", "maxUpload", "maxDownload"));
        
    }
    
    /**
     * default grid width
     */
    private static final int DEFAULT_WIDTH = 30;
    
    /**
     * sheetname
     */
    private static final String SHEETNAME = "data_statistics_day";
    
    @Autowired
    private FileBaseService fileBaseService;
    
    private static List<String> getExcelHeaders(String headerType)
    {
        List<String> headerList = HEADERS.get(headerType);
        return headerList;
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStatisticsAPI.class);
    
    /**
     * this method to get data from db to create sheet data parameters value getted by
     * reflecting dynamicly
     * 
     * @param <T>
     * @param dataSheet
     * @param textCellStyle
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings({"hiding"})
    private <T> HSSFWorkbook createDataFile(List<T> statisticsDatas, String headerType)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // a excel work book
        HSSFWorkbook dataBook = new HSSFWorkbook();
        // a named 'SHEETNAME' sheet
        HSSFSheet dataSheet = dataBook.createSheet(SHEETNAME);
        // set grid default width
        dataSheet.setDefaultColumnWidth(DEFAULT_WIDTH);
        // set style , font
        HSSFCellStyle headerCellStyle = dataBook.createCellStyle();
        headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFFont textFont = dataBook.createFont();
        textFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerCellStyle.setFont(textFont);
        HSSFCellStyle textCellStyle = dataBook.createCellStyle();
        textCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // header size
        List<String> headers = getExcelHeaders(headerType);
        int headerSize = headers.size();
        // create header info
        HSSFRow row = dataSheet.createRow(DEFAULT_INDEX);
        HSSFCell dataCell = null;
        for (int i = 0; i < headerSize; i++)
        {
            dataCell = row.createCell(i);
            dataCell.setCellStyle(headerCellStyle);
            dataCell.setCellValue(headers.get(i));
        }
        // create content
        createData(statisticsDatas, dataSheet, textCellStyle);
        return dataBook;
    }
    
    /**
     * @param statisticsDatas
     * @param dataSheet
     * @param textCellStyle
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @SuppressWarnings({"unchecked", "hiding"})
    private <T> void createData(List<T> statisticsDatas, HSSFSheet dataSheet, HSSFCellStyle textCellStyle)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        HSSFRow row;
        int index = 0;
        Field[] fields = null;
        int size = 0;
        Field field = null;
        String fieldName = null;
        String methodName = null;
        HSSFCell dataCell = null;
        Class<T> dataClass = null;
        Method method = null;
        Object value = null;
        String textValue = null;
        Class<?>[] classes = null;
        Object[] objects = null;
        for (T data : statisticsDatas)
        {
            index++;
            row = dataSheet.createRow(index);
            // get fields
            fields = data.getClass().getDeclaredFields();
            size = fields.length;
            for (int j = 0; j < size; j++)
            {
                field = fields[j];
                // get field`s name
                fieldName = field.getName();
                // field`s getter name
                methodName = "get" + fieldName.substring(0, 1).toUpperCase(Locale.US)
                    + fieldName.substring(1);
                dataCell = row.createCell(j);
                dataCell.setCellStyle(textCellStyle);
                dataClass = (Class<T>) data.getClass();
                // field`s getter method
                classes = new Class<?>[]{};
                method = dataClass.getMethod(methodName, classes);
                // get value
                objects = new Object[]{};
                value = method.invoke(data, objects);
                // value handler
                textValue = valueHandler(value);
                dataCell.setCellValue(textValue);
            }
        }
    }
    
    /**
     * handle the getted value judge it`s object type,then cast it to String and return
     * 
     * @param value
     * @return
     */
    private String valueHandler(Object value)
    {
        String textValue = null;
        
        if (value instanceof Integer || value instanceof Long)
        {
            textValue = String.valueOf(value);
        }
        if (value instanceof String)
        {
            textValue = (String) value;
        }
        
        return textValue;
    }
    
    @RequestMapping(value = "/excel", method = RequestMethod.POST)
    public void getDataHistoryStatistics(@RequestHeader("Authorization") String authorization,
        @RequestBody ExcelExportReqeust exportRequest, @RequestHeader("Date") String date,
        HttpServletResponse response)
    {
        checkHistroyRequest(exportRequest);
        List<T> statisticsDatas = null;
        UserToken userToken = getUserToken(authorization);
        try
        {
            systemTokenHelper.checkSystemToken(authorization, date);
            Calendar beginCal = Calendar.getInstance();
            beginCal.setTimeInMillis(exportRequest.getBeginTime());
            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(exportRequest.getEndTime());
            statisticsDatas = getStatisticsDataList(StatisticsDateUtils.getDay(beginCal),
                StatisticsDateUtils.getDay(endCal),
                exportRequest.getType());
            if (statisticsDatas == null)
            {
                throw new NoSuchItemsException();
            }
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_DATA_ERR,
                null,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_STATISTIC_DATA,
            null,
            null);
        responseToClient(exportRequest.getType(), response, statisticsDatas);
    }
    
    private UserToken getUserToken(String authorization)
    {
        UserToken userToken = new UserToken();
        if (authorization == null)
        {
            userToken.setLoginName("");
            return userToken;
        }
        String[] akArray = authorization.split(",");
        userToken.setLoginName(akArray.length < 2 ? authorization : akArray[1]);
        return userToken;
    }
    
    private void responseToClient(String type, HttpServletResponse response, List<T> statisticsDatas)
    {
        try
        {
            HSSFWorkbook dataBook = createDataFile(statisticsDatas, type);
            // clear response
            response.reset();
            // set response contentType
            response.setContentType("application/octet-stream");
            OutputStream dataOut = null;
            try
            {
                dataOut = response.getOutputStream();
                // write excel data into the OutputStream
                dataBook.write(dataOut);
                dataOut.flush();
            }
            catch (IOException exception)
            {
                LOGGER.error("error accured while flushing data", exception);
            }
            finally
            {
                IOUtils.closeQuietly(dataOut);
            }
        }
        catch (NoSuchMethodException e)
        {
            LOGGER.error("error accured in reflecting handler", e);
            throw new InnerException(e);
        }
        catch (IllegalAccessException e)
        {
            LOGGER.error("error accured in reflecting handler", e);
            throw new InnerException(e);
        }
        catch (InvocationTargetException e)
        {
            LOGGER.error("error accured in reflecting handler", e);
            throw new InnerException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<T> getStatisticsDataList(Integer beginTime, Integer endTime, String type)
    {
        List<T> statisticsDatas = null;
        if ("node".equals(type))
        {
            statisticsDatas = (List<T>) statisticsManager.getNodeStatisticsDayHistoryList(beginTime, endTime);
        }
        else if ("object".equals(type))
        {
            statisticsDatas = (List<T>) statisticsManager.getObjectStatisticsDayHistoryList(beginTime,
                endTime);
        }
        else if ("user".equals(type))
        {
            statisticsDatas = (List<T>) statisticsManager.getUserStatisticsDayHistoryList(beginTime, endTime);
        }
        else if ("concurrence_file".equals(type))
        {
            statisticsDatas = (List<T>) statisticsManager.getSysConcStatisticsDayHistoryList(beginTime,
                endTime);
        }
        return statisticsDatas;
    }
    
    private void checkHistroyRequest(ExcelExportReqeust exportRequest)
    {
        if (null == exportRequest.getBeginTime())
        {
            throw new InvalidParamException("null beginTime");
        }
        if (null == exportRequest.getEndTime())
        {
            exportRequest.setEndTime(Long.valueOf(new Date().getTime()));
        }
        if (exportRequest.getBeginTime().longValue() > exportRequest.getEndTime().longValue())
        {
            throw new InvalidParamException("beginTime larger than endTime");
        }
    }
    
    @Autowired
    private SystemTokenHelper systemTokenHelper;
    
    @Autowired
    private StatisticsManager statisticsManager;
    
}
