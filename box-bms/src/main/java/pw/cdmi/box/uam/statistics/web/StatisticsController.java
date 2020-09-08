package pw.cdmi.box.uam.statistics.web;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.exception.RestException;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.httpclient.domain.RestUserHistoryStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.TimePoint;
import pw.cdmi.box.uam.httpclient.domain.UserCurrentStatisticsInfo;
import pw.cdmi.box.uam.httpclient.domain.UserHistoryStatisticsCondition;
import pw.cdmi.box.uam.httpclient.domain.UserHistoryStatisticsInfo;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.statistics.domain.ConStatisticsTableNode;
import pw.cdmi.box.uam.statistics.domain.ConcStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.ConcStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ConcStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.ConcurrentHistoryRequest;
import pw.cdmi.box.uam.statistics.domain.FileStoreAddTableNode;
import pw.cdmi.box.uam.statistics.domain.FileStoreHistoryRequest;
import pw.cdmi.box.uam.statistics.domain.FileStoreHistoryTableNode;
import pw.cdmi.box.uam.statistics.domain.FileTableNode;
import pw.cdmi.box.uam.statistics.domain.HistUserStatisDataset;
import pw.cdmi.box.uam.statistics.domain.HistUserStatisResponse;
import pw.cdmi.box.uam.statistics.domain.MilestioneInfo;
import pw.cdmi.box.uam.statistics.domain.NodeCurrentStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.NodeCurrentStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.NodeCurrentStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.NodeHistoryStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.NodeHistoryStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.ObjectCurrentStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.ObjectCurrentStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ObjectCurrentStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.ObjectHistoryRequest;
import pw.cdmi.box.uam.statistics.domain.ObjectHistoryStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.ObjectHistoryStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ObjectHistoryStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.RestUserClusterStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.StatisticsConstants;
import pw.cdmi.box.uam.statistics.domain.StatisticsPacker;
import pw.cdmi.box.uam.statistics.domain.StatisticsTempChart;
import pw.cdmi.box.uam.statistics.domain.StatisticsUtils;
import pw.cdmi.box.uam.statistics.domain.SystemStoreHistoryTableNode;
import pw.cdmi.box.uam.statistics.domain.TerminalCurrentVersionDay;
import pw.cdmi.box.uam.statistics.domain.TerminalCurrentVersionView;
import pw.cdmi.box.uam.statistics.domain.TerminalDeviceHistoryRequest;
import pw.cdmi.box.uam.statistics.domain.TerminalDeviceHistoryView;
import pw.cdmi.box.uam.statistics.domain.TerminalDeviceTypeView;
import pw.cdmi.box.uam.statistics.domain.TerminalHistoryDateSortor;
import pw.cdmi.box.uam.statistics.domain.TerminalHistoryNode;
import pw.cdmi.box.uam.statistics.domain.TerminalHistoryTableResponse;
import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsDay;
import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionCurrentView;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionHistoryTableNode;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionHistoryView;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionHitoryRequest;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionTypeView;
import pw.cdmi.box.uam.statistics.domain.ThirdTerminalData;
import pw.cdmi.box.uam.statistics.domain.UserClusterStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.UserClusterStatisticsList;
import pw.cdmi.box.uam.statistics.domain.UserInterzone;
import pw.cdmi.box.uam.statistics.domain.sortor.StatisticsBeanTimepointSorter;
import pw.cdmi.box.uam.statistics.domain.sortor.TerminalStatisticsDaySortor;
import pw.cdmi.box.uam.statistics.manager.TerminalStatisticsManager;
import pw.cdmi.box.uam.statistics.manager.UserInterzoneStatisticsManager;
import pw.cdmi.box.uam.statistics.service.StatisticsService;
import pw.cdmi.box.uam.statistics.service.TerminalStatisticsService;
import pw.cdmi.box.uam.util.BusinessConstants;
import pw.cdmi.common.domain.Terminal;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.uam.domain.AuthApp;

//import java.util.Locale;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController extends AbstractCommonController
{
    private static final String ALL_APPLICATIONS = "All Applications";
    
    private static final String CURRENT_USER_STATISTICS = "11";
    
    private static final int DEFAULT_INDEX = 0;
    
    /**
     * default grid width
     */
    private static final int DEFAULT_WIDTH = 30;
    
    /**
     * deviceType
     */
    private static final String DEVICETYPE = "deviceType";
    
    /**
     * file extension array
     */
    private static final String[] EXTENSIONS = {".xls", ".xlsx"};
    
    /**
     * filenames
     */
    private static final Map<String, String> FILENAMES = new HashMap<String, String>(16);
    
    /**
     * chartEnterList
     */
    private static final Map<String, String> CHART_ENTER_LIST = new HashMap<String, String>(16);
    
    /**
     * content-disposition
     */
    private static final String HEADER_DISPOSITION = "Content-Disposition";
    
    /**
     * sheet headers
     */
    private static final Map<String, List<String>> HEADERS = new HashMap<String, List<String>>(16);
    
    private static final String HISTORY_USER_STATISTICS = "12";
    
    private static final String INCREMENT_USER_STATISTICS = "13";
    
    private static final String INTERZONE_REGEX = "^[0-9]*[1-9][0-9]*$";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);
    
    /**
     * sheetname
     */
    private static final String SHEETNAME = "data_statistics_day";
    
    private static final long DEFAULT_VAL = 0;
    
    static
    {
        HEADERS.put("terminal", Arrays.asList("day", "deviceType", "clientVersion", "userCount"));
        FILENAMES.put("node", "node_statistics_");
        FILENAMES.put("object", "obj_statistics_");
        FILENAMES.put("terminal", "terminal_statistics_");
        FILENAMES.put("concurrence_file", "system_conc_statistics_");
        FILENAMES.put("user", "user_statistics_");
        
        CHART_ENTER_LIST.put(CURRENT_USER_STATISTICS, "statistics/curUserStatisList");
        CHART_ENTER_LIST.put(HISTORY_USER_STATISTICS, "statistics/histUserStatisList");
        CHART_ENTER_LIST.put(INCREMENT_USER_STATISTICS, "statistics/incrementUserStatisList");
        CHART_ENTER_LIST.put(StatisticsConstants.CURRENT_FILE, "statistics/fileCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.HISTORY_FILE, "statistics/fileHistoryList");
        CHART_ENTER_LIST.put(StatisticsConstants.ADD_FILE, "statistics/fileAddList");
        CHART_ENTER_LIST.put(StatisticsConstants.CURRENT_STORE_FILE, "statistics/fileStoreCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.HISTORY_STORE_FILE, "statistics/fileStoreHistoryList");
        CHART_ENTER_LIST.put(StatisticsConstants.ADD_STORE_FILE, "statistics/fileStoreAddList");
        CHART_ENTER_LIST.put(StatisticsConstants.CONCURRENCE, "statistics/concurrenceHistoryList");
        CHART_ENTER_LIST.put(StatisticsConstants.TERMINAL_MAIN, "statistics/terminalCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.TERMINAL_CURRRENT, "statistics/terminalCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.TERMINAL_HISTORY, "statistics/terminalHistoryList");
        CHART_ENTER_LIST.put(StatisticsConstants.TERMINAL_VERSION_MAIN,
            "statistics/terminalVersionCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.TERMINAL_VERSION_CURRENT,
            "statistics/terminalVersionCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.SYSTEM_STORE_MAIN, "statistics/systemStoreCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.SYSTEM_STORE_CURRENT, "statistics/systemStoreCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.SYSTEM_STORE_HISTORY, "statistics/systemStoreHistoryList");
        CHART_ENTER_LIST.put(StatisticsConstants.USER_STORE_MAIN, "statistics/userStoreCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.USER_STORE_CURRENT, "statistics/userStoreCurrentList");
        CHART_ENTER_LIST.put(StatisticsConstants.USER_STORE_SET_ZONE, "statistics/userStoreInterzone");
        
    }
    
    private static boolean checkInterzoneRegex(String interzone)
    {
        Pattern p = Pattern.compile(INTERZONE_REGEX);
        Matcher m = p.matcher(interzone);
        return m.matches();
    }
    
    private static String getShowXnumber(TimePoint point)
    {
        StringBuilder sb = new StringBuilder();
        if ("year".equalsIgnoreCase(point.getUnit()))
        {
            sb.append(point.getYear());
        }
        else if ("day".equalsIgnoreCase(point.getUnit()))
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, point.getYear());
            cal.set(Calendar.DAY_OF_YEAR, point.getNumber());
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            // sb.append(month).append("-").append(cal.get(Calendar.DAY_OF_MONTH));
            sb.append(year).append('-').append(month).append('-').append(cal.get(Calendar.DAY_OF_MONTH));
        }
        else
        {
            sb.append(point.getYear()).append('-').append(point.getNumber());
        }
        
        return sb.toString();
    }
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private TerminalStatisticsManager terminalManager;
    
    @Autowired
    private TerminalStatisticsService terminalStatisticsService;
    
    @Autowired
    private UserInterzoneStatisticsManager userInterzoneStatisticsManager;
    
    @RequestMapping(value = "chart", method = RequestMethod.GET)
    public String chartEnter(String appId, String treeNodeId, Model model)
    {
        model.addAttribute("authAppList", authAppService.getAuthAppList(null, null, null));
        model.addAttribute("regionList", statisticsService.getRegionInfo());
        model.addAttribute("treeNodeId", treeNodeId);
        if (StringUtils.isBlank(treeNodeId))
        {
            return null;
        }
        if (CHART_ENTER_LIST.get(treeNodeId) != null)
        {
            return CHART_ENTER_LIST.get(treeNodeId);
        }
        if (StringUtils.equals(StatisticsConstants.TERMINAL_VERSION_HISTORY, treeNodeId))
        {
            List<Terminal> deviceTypes = getDeviceType();
            model.addAttribute("deviceList", deviceTypes);
            return "statistics/terminalVersionHistoryList";
        }
        return null;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        model.addAttribute("authAppList", authAppService.getAuthAppList(null, null, null));
        return "statistics/statisticsManageMain";
    }
    
    @RequestMapping(value = "excelExport", method = RequestMethod.GET)
    public void excelExport(Long beginTime, Long endTime, String type, HttpServletRequest request,
        HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException,
        InvocationTargetException
    {
        Integer beginDateInt = 0;
        Integer endDateInt = 0;
        if (null != beginTime)
        {
            Calendar begingDate = Calendar.getInstance();
            begingDate.setTimeInMillis(beginTime);
            begingDate.add(Calendar.DAY_OF_MONTH,-1);
            beginDateInt = getDay(begingDate);
            beginTime = begingDate.getTimeInMillis();
        }
        if (null != endTime)
        {
            Calendar endDate = Calendar.getInstance();
            endDate.setTimeInMillis(endTime);
            endDate.add(Calendar.DAY_OF_MONTH,-1);
            endDateInt = getDay(endDate);
            endTime = endDate.getTimeInMillis();
        }
        String[] description = new String[]{String.valueOf(beginDateInt), String.valueOf(endDateInt), type};
        String id = systemLogManager.saveFailLog(request,
            OperateType.Statistics,
            OperateDescription.STATISTICS_EXPORT_USER_HISTORY,
            null,
            description);
        
        ServletOutputStream outStream = null;
        // format dataFileName
        String dataFileName = getFileName(type) + UUID.randomUUID() + EXTENSIONS[DEFAULT_INDEX];
        // set contentType
        String contentType = request.getSession().getServletContext().getMimeType(dataFileName)
            + ";charset=UTF-8";
        // clear response
        response.reset();
        response.setContentType(contentType);
        try
        {
            outStream = response.getOutputStream();
            response.setHeader(HEADER_DISPOSITION,
                "attachment; filename="
                    + new String(dataFileName.getBytes(Charset.defaultCharset()), "UTF-8"));
            if ("terminal".equals(type))
            {
                String deviceTypeStr = request.getParameter("deviceType");
                Integer deviceType = null;
                if (deviceTypeStr != null)
                {
                    deviceType = Integer.valueOf(deviceTypeStr);
                }
                List<TerminalStatisticsDay> statisticsDatas = terminalStatisticsService.getHistoryListGroupByVersion(beginDateInt, endDateInt,deviceType);
                Collections.sort(statisticsDatas, new TerminalStatisticsDaySortor());
                HSSFWorkbook dataBook = createDataFile(statisticsDatas, type);
                dataBook.write(outStream);
            }
            else
            {
                byte[] b = statisticsService.dataExport(beginTime, endTime, type);
                if (b != null)
                {
                    response.setHeader("Content-Length", "" + b.length);
                    outStream.write(b);
                }
            }
            systemLogManager.updateSuccess(id);
        }
        catch (IOException e)
        {
            LOGGER.info(" writing to the OutputStream failed", e);
        }
        finally
        {
            if (null != outStream)
            {
                try
                {
                    outStream.close();
                }
                catch (IOException e)
                {
                    LOGGER.warn("closing Out Stream failed", e);
                }
            }
        }
    }
    
    @RequestMapping(value = "fixedChartList/{treeNodeId}", method = RequestMethod.GET)
    public ResponseEntity<?> fixedChartList(@PathVariable(value = "treeNodeId") String treeNodeId,
        String appId, HttpServletRequest request) throws RestException
    {
        List<String> urls = new ArrayList<String>(2);
        if (StringUtils.equals(CURRENT_USER_STATISTICS, treeNodeId))
        {
            JFreeChart appChart = ChartFactory.createBarChart("",
                "Application",
                "User Num",
                getCurUserAccount("application", appId),
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
            JFreeChart regionChart = ChartFactory.createBarChart("",
                "Region",
                "User Num",
                getCurUserAccount("region", appId),
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
            decorateBarChart(appChart);
            decorateBarChart(regionChart);
            urls = createChart(appChart, regionChart, 840, 350);
        }
        return new ResponseEntity<List<String>>(urls, HttpStatus.OK);
    }
    
    @RequestMapping(value = "getConcurrenceHistoryView", method = RequestMethod.POST)
    public ResponseEntity<?> getConcurrenceHistoryView(ConcurrentHistoryRequest concStatisticsRequest,
        Model mode, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        checkTimeRule(concStatisticsRequest);
        Map<String, Object> values = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        try
        {
            ConcStatisticsResponse statisticsResponse = getConcHistoryData(new ConcStatisticsRequest(
                concStatisticsRequest));
            values = transConcStatisticsData(statisticsResponse);
            String url = StatisticsUtils.createView(StatisticsConstants.CHART_LINE,
                null,
                null,
                null,
                values.get(StatisticsConstants.DATA),
                statisticsService,
                840,
                350,
                Long.valueOf((int) values.get(StatisticsConstants.MAX_VALUE)),
                Long.valueOf((int) values.get(StatisticsConstants.MIN_VALUE)),
                null,
                null);
            values.put("url", url);
            values.put("unit", concStatisticsRequest.getInterval());
            values.put("response", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(values, HttpStatus.OK);
        }
        catch (RestException e)
        {
            values.put("response", e.getCode());
            return new ResponseEntity<Map<String, Object>>(values, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            values.put("response", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(values, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "getFileAddView/{treeNodeId}", method = RequestMethod.POST)
    public ResponseEntity<?> getFileAddView(ObjectHistoryRequest statisticsRequest,
        @PathVariable("treeNodeId") String treeNodeId, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        checkTimeRule(statisticsRequest);
        String url = "";
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        ObjectHistoryStatisticsResponse statisticsResponse = null;
        // Locale locale =
        // RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        try
        {
            if (StringUtils.equals(StatisticsConstants.ADD_FILE, treeNodeId))
            {
                statisticsResponse = getHistoryObjectData(new ObjectHistoryStatisticsRequest(
                    statisticsRequest));
                map = transAddDefaultData(statisticsResponse);
                long max = (long) map.get(StatisticsConstants.MAX_VALUE);
                long min = (long) map.get(StatisticsConstants.MIN_VALUE);
                max = getActualMax(max, min, statisticsResponse.getData().size());
                url = StatisticsUtils.createView(StatisticsConstants.CHART_LINE,
                    "",
                    "Files",
                    "",
                    map.get(StatisticsConstants.DATA),
                    statisticsService,
                    840,
                    350,
                    max,
                    min,
                    null,
                    null);
                map.put("url", url);
                map.put("response", HttpStatus.OK);
                map.put("unit", statisticsRequest.getInterval());
                return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
            }
        }
        catch (RestException e)
        {
            map.put("response", e.getCode());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            map.put("response", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        map.put("response", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @RequestMapping(value = "getFileCurrentView/{treeNodeId}", method = RequestMethod.POST)
    public ResponseEntity<?> getFileCurrentView(@PathVariable("treeNodeId") String treeNodeId, Model model,
        HttpServletRequest request, String token)
    {
        try
        {
            super.checkToken(token);
            // Locale locale =
            // RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
            if (StringUtils.equals(StatisticsConstants.CURRENT_FILE, treeNodeId))
            {
                String url = StatisticsUtils.createView(StatisticsConstants.CHART_BAR,
                    "",
                    "Files",
                    "",
                    getFileCurrentData(new ObjectCurrentStatisticsRequest()),
                    statisticsService,
                    840,
                    350,
                    DEFAULT_VAL,
                    DEFAULT_VAL,
                    0.16,
                    -0.632);
                model.addAttribute("url", url);
                model.addAttribute("response", HttpStatus.OK);
                return new ResponseEntity<Model>(model, HttpStatus.OK);
            }
        }
        catch (RestException e)
        {
            model.addAttribute("response", e.getCode());
            return new ResponseEntity<Model>(model, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            model.addAttribute("response", e.getMessage());
            return new ResponseEntity<Model>(model, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        model.addAttribute("response", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<Model>(model, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @RequestMapping(value = "getFileHistoryView/{treeNodeId}", method = RequestMethod.POST)
    public ResponseEntity<?> getFileHistoryView(ObjectHistoryRequest statisticsRequest,
        @PathVariable("treeNodeId") String treeNodeId, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        checkTimeRule(statisticsRequest);
        String url = "";
        Map<String, Object> map = new HashMap<String, Object>(16);
        ObjectHistoryStatisticsResponse statisticsResponse = null;
        // Locale locale =
        // RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        try
        {
            if (StringUtils.equals(StatisticsConstants.HISTORY_FILE, treeNodeId))
            {
                statisticsResponse = getHistoryObjectData(new ObjectHistoryStatisticsRequest(
                    statisticsRequest));
                map = transHistoryDefaultData(statisticsResponse);
                long max = (long) map.get(StatisticsConstants.MAX_VALUE);
                long min = (long) map.get(StatisticsConstants.MIN_VALUE);
                long actualMax = getActualMax(max, min, statisticsResponse.getData().size());
                url = StatisticsUtils.createView(StatisticsConstants.CHART_LINE,
                    "",
                    "Files",
                    "",
                    map.get(StatisticsConstants.DATA),
                    statisticsService,
                    840,
                    350,
                    actualMax,
                    min,
                    null,
                    null);
                map.put("url", url);
                map.put("response", HttpStatus.OK);
                map.put("unit", statisticsRequest.getInterval());
                return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
            }
        }
        catch (RestException e)
        {
            map.put("response", e.getCode());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            map.put("response", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        map.put("response", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @RequestMapping(value = "getFileStoreAddView/{treeNodeId}", method = RequestMethod.POST)
    public ResponseEntity<?> getFileStoreAddView(ObjectHistoryRequest statisticsRequest,
        @PathVariable("treeNodeId") String treeNodeId, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        checkTimeRule(statisticsRequest);
        String url = "";
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        ObjectHistoryStatisticsResponse statisticsResponse = null;
        // Locale locale =
        // RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        try
        {
            if (StringUtils.equals(StatisticsConstants.ADD_STORE_FILE, treeNodeId))
            {
                statisticsResponse = getHistoryObjectData(new ObjectHistoryStatisticsRequest(
                    statisticsRequest));
                map = getFileStoreAddData(statisticsResponse);
                url = StatisticsUtils.createView(StatisticsConstants.CHART_BAR,
                    null,
                    "Add Store space(MB)",
                    null,
                    map.get(StatisticsConstants.DATA),
                    statisticsService,
                    840,
                    350,
                    DEFAULT_VAL,
                    DEFAULT_VAL,
                    0.04,
                    -1.18);
                map.put("url", url);
                map.put("response", HttpStatus.OK);
                map.put("unit", statisticsRequest.getInterval());
                return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
            }
        }
        catch (RestException e)
        {
            map.put("response", e.getCode());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            map.put("response", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        map.put("response", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private void checkTimeRule(ObjectHistoryRequest statisticsRequest)
    {
        Date beginTime = statisticsRequest.getBeginTime();
        Date endTime = statisticsRequest.getEndTime();
        long nowTime = DateUtils.now().getTime();
        
        handleTime(beginTime, endTime, nowTime);
    }
    
    private void checkTimeRule(TerminalVersionHitoryRequest versionRequest)
    {
        Date beginTime = versionRequest.getBeginTime();
        Date endTime = versionRequest.getEndTime();
        long nowTime = DateUtils.now().getTime();
        
        handleTime(beginTime, endTime, nowTime);
    }
    
    private void checkTimeRule(TerminalDeviceHistoryRequest statisticsRequest)
    {
        Date beginTime = statisticsRequest.getBeginTime();
        Date endTime = statisticsRequest.getEndTime();
        long nowTime = DateUtils.now().getTime();
        
        handleTime(beginTime, endTime, nowTime);
    }
    
    private void handleTime(Date beginTime, Date endTime, long nowTime)
    {
        if (null == beginTime)
        {
            LOGGER.error("beginTime be null");
            throw new InvalidParamterException("beginTime be null");
        }
        
        if (null != endTime && beginTime.getTime() > endTime.getTime())
        {
            LOGGER.error("beginTime behind endTime", beginTime, ">", endTime);
            throw new InvalidParamterException("beginTime behind endTime");
        }
        
        if (beginTime.getTime() > nowTime)
        {
            LOGGER.error("beginTime behind nowTime", beginTime.getTime(), ">", nowTime);
            throw new InvalidParamterException("beginTime behind endTime");
        }
    }
    
    private void checkTimeRule(ConcurrentHistoryRequest concStatisticsRequest)
    {
        Date beginTime = concStatisticsRequest.getBeginTime();
        Date endTime = concStatisticsRequest.getEndTime();
        long nowTime = DateUtils.now().getTime();
        
        handleTime(beginTime, endTime, nowTime);
    }
    
    @RequestMapping(value = "getFileStoreCurrentView", method = RequestMethod.POST)
    public ResponseEntity<?> getFileStoreCurrentView(HttpServletRequest request, String token)
    {
        super.checkToken(token);
        Map<String, Object> map = new HashMap<String, Object>(16);
        String regionUrl = "";
        String applicationUrl = "";
        // Locale locale =
        // RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        try
        {
            NodeCurrentStatisticsRequest applicationRequest = new NodeCurrentStatisticsRequest(
                StatisticsConstants.GROUPBY_APP);
            NodeCurrentStatisticsResponse applicationResponse = getNodeCurrentData(applicationRequest);
            DefaultPieDataset applicationData = getFileStoreCurrentApplicationData(applicationResponse);
            applicationUrl = StatisticsUtils.createView(StatisticsConstants.CHART_PIE,
                "Statistics of application(MB)",
                null,
                null,
                applicationData,
                statisticsService,
                840,
                350,
                DEFAULT_VAL,
                DEFAULT_VAL,
                null,
                null);
            // 获取区域图片
            NodeCurrentStatisticsRequest regionRequest = new NodeCurrentStatisticsRequest(
                StatisticsConstants.GROUPBY_REGION);
            NodeCurrentStatisticsResponse regionResponse = getNodeCurrentData(regionRequest);
            DefaultPieDataset regionData = getFileStoreCurrentRegionData(regionResponse);
            regionUrl = StatisticsUtils.createView(StatisticsConstants.CHART_PIE,
                "Statistics of region(MB)",
                null,
                null,
                regionData,
                statisticsService,
                840,
                350,
                DEFAULT_VAL,
                DEFAULT_VAL,
                null,
                null);
            map.put("applicationUrl", applicationUrl);
            map.put("regionUrl", regionUrl);
            map.put("response", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
        }
        catch (RestException e)
        {
            map.put("response", e.getCode());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            map.put("response", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "getFileStoreHistoryView", method = RequestMethod.POST)
    public ResponseEntity<?> getFileStoreHistoryView(FileStoreHistoryRequest historyRequest,
        HttpServletRequest request, String token)
    {
        super.checkToken(token);
        String url = "";
        Map<String, Object> map = new HashMap<String, Object>(16);
        NodeHistoryStatisticsResponse statisticsResponse = null;
        // Locale locale =
        // RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        try
        {
            if (StringUtils.isEmpty(historyRequest.getAppId()))
            {
                historyRequest.setAppId(null);
            }
            
            statisticsResponse = getHistoryFileStoreData(historyRequest);
            map = transFileStoreHistoryData(statisticsResponse);
            long max = (long) map.get(StatisticsConstants.MAX_VALUE);
            long min = (long) map.get(StatisticsConstants.MIN_VALUE);
            long actualMax = getActualMax(max, min, statisticsResponse.getTotalCount());
            url = StatisticsUtils.createView(StatisticsConstants.CHART_LINE,
                "",
                "Logical space(MB)",
                "",
                map.get(StatisticsConstants.DATA),
                statisticsService,
                840,
                350,
                actualMax,
                min,
                null,
                null);
            map.put("url", url);
            map.put("response", HttpStatus.OK);
            map.put("unit", historyRequest.getInterval());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
        }
        catch (RestException e)
        {
            map.put("response", e.getCode());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            map.put("response", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "getSystemStoreCurrentView", method = RequestMethod.POST)
    public ResponseEntity<?> getSystemStoreCurrentView(Model model, HttpServletRequest request, String token)
    {
        try
        {
            super.checkToken(token);
            // Locale locale =
            // RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
            String url = StatisticsUtils.createView(StatisticsConstants.CHART_BAR,
                "",
                "Store(MB)",
                "",
                getSystemStoreCurrentData(new ObjectCurrentStatisticsRequest()),
                statisticsService,
                840,
                350,
                DEFAULT_VAL,
                DEFAULT_VAL,
                0.16,
                -0.632);
            model.addAttribute("url", url);
            model.addAttribute("response", HttpStatus.OK);
            return new ResponseEntity<Model>(model, HttpStatus.OK);
        }
        catch (RestException e)
        {
            model.addAttribute("response", e.getCode());
            return new ResponseEntity<Model>(model, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            model.addAttribute("response", e.getMessage());
            return new ResponseEntity<Model>(model, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "getchart/{chartId}", method = RequestMethod.GET)
    public void downloadChart(@PathVariable String chartId, HttpServletResponse response) throws IOException
    {
        OutputStream output = null;
        try
        {
            StatisticsTempChart dbChart = this.statisticsService.getTempChart(chartId);
            output = response.getOutputStream();
            IOUtils.write(dbChart.getChartImage(), output);
        }
        finally
        {
            IOUtils.closeQuietly(output);
        }
        
    }
    
    @RequestMapping(value = "getSystemStoreHistoryView", method = RequestMethod.POST)
    public ResponseEntity<?> getSystemStoreHistoryView(ObjectHistoryRequest statisticsRequest,
        HttpServletRequest request, String token)
    {
        super.checkToken(token);
        checkTimeRule(statisticsRequest);
        String url = "";
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        ObjectHistoryStatisticsResponse statisticsResponse = null;
        // Locale locale =
        // RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        try
        {
            statisticsResponse = getHistoryObjectData(new ObjectHistoryStatisticsRequest(statisticsRequest));
            map = transSystemStoreHistoryData(statisticsResponse);
            long max = (long) map.get(StatisticsConstants.MAX_VALUE);
            long min = (long) map.get(StatisticsConstants.MIN_VALUE);
            long actualMax = getActualMax(max, min, statisticsResponse.getData().size());
            url = StatisticsUtils.createView(StatisticsConstants.CHART_LINE,
                "",
                "Store(MB)",
                "",
                map.get(StatisticsConstants.DATA),
                statisticsService,
                840,
                350,
                actualMax,
                min,
                null,
                null);
            map.put("url", url);
            map.put("response", HttpStatus.OK);
            map.put("unit", statisticsRequest.getInterval());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
        }
        catch (RestException e)
        {
            map.put("response", e.getCode());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            map.put("response", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "getTerminalCurrentView", method = RequestMethod.POST)
    public ResponseEntity<?> getTerminalCurrentView(HttpServletRequest request, String token)
    {
        Map<String, Object> values = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        try
        {
            super.checkToken(token);
            List<TerminalStatisticsInfo> terminalInfoes = getCurrrentTerminalData();
            String url = StatisticsUtils.createView(StatisticsConstants.CHART_BAR,
                null,
                "User data",
                null,
                transCurrentTerminalData(terminalInfoes),
                statisticsService,
                840,
                350,
                DEFAULT_VAL,
                DEFAULT_VAL,
                0.15,
                -0.35);
            values.put("url", url);
            values.put("response", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(values, HttpStatus.OK);
        }
        catch (Exception e)
        {
            values.put("response", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(values, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "getTerminalHistoryView", method = RequestMethod.POST)
    public ResponseEntity<?> getTerminalHistoryView(HttpServletRequest request,
        TerminalDeviceHistoryRequest historyRequest, String token)
    {
        super.checkToken(token);
        checkTimeRule(historyRequest);
        Map<String, Object> data = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        try
        {
            TerminalDeviceHistoryView deviceHistories = getHistoryTerminalData(historyRequest);
            Map<String, Object> values = transHistoryTerminalData(deviceHistories);
            long max = Long.valueOf((int) values.get(StatisticsConstants.MAX_VALUE));
            long min = Long.valueOf((int) values.get(StatisticsConstants.MIN_VALUE));
            long actualMax = getActualMax(max, min, deviceHistories.getDeviceHistoryList().size());
            String url = StatisticsUtils.createView(StatisticsConstants.CHART_LINE,
                null,
                "User Data",
                null,
                values.get(StatisticsConstants.DATA),
                statisticsService,
                840,
                350,
                actualMax,
                min,
                null,
                null);
            data.put("url", url);
            data.put("tableData", values.get(StatisticsConstants.TABLE_DATA));
            data.put("unit", historyRequest.getInterval());
            data.put("response", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
        }
        catch (ParseException e)
        {
            data.put("response", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            data.put("response", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "getTerminalVersionCurrentView", method = RequestMethod.POST)
    public ResponseEntity<?> getTerminalVersionCurrentView(HttpServletRequest request, String token)
    {
        Map<String, Object> data = new HashMap<String, Object>(16);
        try
        {
            super.checkToken(token);
            Map<String, Object> tempData = null;
            TerminalVersionCurrentView versionData = getCurrentTerminalDataByDeviceType();
            List<String> urls = new ArrayList<String>(versionData.getData().size());
            
            String url = null;
            if (versionData.getData().size() != 0)
            {
                for (TerminalCurrentVersionView currentVersion : versionData.getData())
                {
                    tempData = transCurrentVersionData(currentVersion.getDeviceName(), versionData.getData());
                    url = StatisticsUtils.createView(StatisticsConstants.CHART_BAR,
                        null,
                        "User data",
                        currentVersion.getDeviceName(),
                        tempData.get(StatisticsConstants.DATA),
                        statisticsService,
                        840,
                        450,
                        DEFAULT_VAL,
                        DEFAULT_VAL,
                        0.15,
                        -0.35);
                    urls.add(url);
                }
            }
            else
            {
                tempData = transCurrentVersionData(null, null);
                url = StatisticsUtils.createView(StatisticsConstants.CHART_BAR,
                    null,
                    "User Data",
                    "",
                    tempData.get(StatisticsConstants.DATA),
                    statisticsService,
                    840,
                    450,
                    DEFAULT_VAL,
                    DEFAULT_VAL,
                    0.15,
                    -0.35);
                urls.add(url);
            }
            
            data.put("url", urls);
            data.put("response", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
        }
        catch (ParseException e)
        {
            data.put("response", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (RuntimeException e)
        {
            data.put("response", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    @RequestMapping(value = "getTerminalVersionHisotryView", method = RequestMethod.POST)
    public ResponseEntity<?> getTerminalVersionHisotryView(HttpServletRequest request,
        TerminalVersionHitoryRequest versionRequest, String token)
    {
        super.checkToken(token);
        checkTimeRule(versionRequest);
        Map<String, Object> data = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        try
        {
            TerminalVersionHistoryView versionDataes = getHistoryTerminalVersionData(versionRequest);
            Map<String, Object> values = transVersionHistoryData(versionDataes);
            String url = StatisticsUtils.createView(StatisticsConstants.CHART_LINE,
                null,
                "User Data",
                null,
                values.get(StatisticsConstants.DATA),
                statisticsService,
                840,
                350,
                Long.valueOf((int) values.get(StatisticsConstants.MAX_VALUE)),
                Long.valueOf((int) values.get(StatisticsConstants.MIN_VALUE)),
                null,
                null);
            data.put("url", url);
            data.put("response", HttpStatus.OK);
            data.put(StatisticsConstants.TABLE_DATA, values.get(StatisticsConstants.TABLE_DATA));
            data.put("unit", versionRequest.getInterval());
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
        }
        catch (ParseException e)
        {
            data.put("response", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (RuntimeException e)
        {
            data.put("response", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<Map<String, Object>>(data, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    @RequestMapping(value = "getUserStoreCurrentView", method = RequestMethod.POST)
    public ResponseEntity<?> getUserStoreCurrentView(String milestione, String token)
    {
        super.checkToken(token);
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        
        UserInterzone userInterzone = userInterzoneStatisticsManager.query();
        String[] interzones = userInterzone.getInterzone().split(StatisticsConstants.SIGNL);
        List<MilestioneInfo> milestioneInfoList = new ArrayList<MilestioneInfo>(StatisticsConstants.LIST_SIZE);
        MilestioneInfo milestioneInfo = null;
        for (int i = 0; i < interzones.length; i++)
        {
            if (StringUtils.equals(interzones[i], StatisticsConstants.DEFAULT_INTEZONE))
            {
                continue;
            }
            if (!checkInterzoneRegex(interzones[i]))
            {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            milestioneInfo = new MilestioneInfo(Long.parseLong(interzones[i]));
            milestioneInfoList.add(milestioneInfo);
        }
        if (milestioneInfoList.isEmpty())
        {
            milestioneInfoList.add(new MilestioneInfo(StatisticsConstants.DEFAULT_MILESTONE));
        }
        try
        {
            UserClusterStatisticsList statisticsList = getUserStoreCurrenData(new RestUserClusterStatisticsRequest(
                milestioneInfoList));
            String url = StatisticsUtils.createView(StatisticsConstants.CHART_PIE,
                "Store Statistics(MB/user)",
                null,
                null,
                getUserStoreCurrentData(statisticsList),
                statisticsService,
                840,
                350,
                DEFAULT_VAL,
                DEFAULT_VAL,
                null,
                null);
            map.put("url", url);
            map.put("response", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
        }
        catch (RestException e)
        {
            map.put("response", e.getCode());
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            map.put("response", e.getMessage());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 
     * @param condition
     * @return
     * @throws RestException
     */
    @RequestMapping(value = "histUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> histUserStatisticsList(UserHistoryStatisticsCondition condition,
        String treeNodeId, String statisticsField, HttpServletRequest servletRequest, String token)
        throws RestException
    {
        super.checkToken(token);
        String legend = "";
        HistUserStatisResponse response = new HistUserStatisResponse();
        RestUserHistoryStatisticsRequest request = new RestUserHistoryStatisticsRequest();
        if (!isSetRequestTimeSuccess(condition, request))
        {
            return null;
        }
        request.setAppId(condition.getAppId());
        request.setInterval(condition.getInterval());
        request.setRegionId(condition.getRegionId());
        if (StringUtils.equalsIgnoreCase(statisticsField, "region"))
        {
            if (condition.getRegionId() == null)
            {
                legend = "All Regions";
            }
            else
            {
                legend = getRegionNameById(condition.getRegionId());
            }
            
            JFreeChart chart = ChartFactory.createLineChart("",
                "",
                "User account",
                getHistUserAccount(request, condition, legend).getDefaultCategoryDataset(),
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
            response.setHistUserStatisDataset(getHistUserAccount(request, condition, legend));
            long max = getHistUserAccount(request, condition, legend).getMax();
            long min = getHistUserAccount(request, condition, legend).getMin();
            decorateLineChart(chart, max, min);
            List<String> urls = createChart(chart, null, 840, 350);
            response.setUrls(urls);
        }
        else if (StringUtils.equalsIgnoreCase(statisticsField, "app"))
        {
            if (StringUtils.isBlank(condition.getAppId()))
            {
                legend = ALL_APPLICATIONS;
            }
            else
            {
                legend = condition.getAppId();
            }
            JFreeChart chart = ChartFactory.createLineChart("",
                "",
                "User account",
                getHistUserAccount(request, condition, legend).getDefaultCategoryDataset(),
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
            response.setHistUserStatisDataset(getHistUserAccount(request, condition, legend));
            long max = getHistUserAccount(request, condition, legend).getMax();
            long min = getHistUserAccount(request, condition, legend).getMin();
            decorateLineChart(chart, max, min);
            List<String> urls = createChart(chart, null, 840, 350);
            response.setUrls(urls);
        }
        else
        {
            return new ResponseEntity<HistUserStatisResponse>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<HistUserStatisResponse>(response, HttpStatus.OK);
    }
    
    @RequestMapping(value = "increUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> increUserStatisticsList(UserHistoryStatisticsCondition condition,
        String treeNodeId, HttpServletRequest servletRequest, String token) throws RestException
    {
        super.checkToken(token);
        String legend = "";
        RestUserHistoryStatisticsRequest request = new RestUserHistoryStatisticsRequest();
        HistUserStatisResponse response = new HistUserStatisResponse();
        
        if (!isSetRequestTimeSuccess(condition, request))
        {
            return null;
        }
        
        request.setAppId(condition.getAppId());
        request.setInterval(condition.getInterval());
        request.setRegionId(condition.getRegionId());
        
        if (StringUtils.isBlank(condition.getAppId()))
        {
            legend = ALL_APPLICATIONS;
        }
        else
        {
            legend = condition.getAppId();
        }
        JFreeChart chart = ChartFactory.createLineChart("",
            "",
            "Add user account",
            getIncreUserAccount(request, condition, legend).getDefaultCategoryDataset(),
            PlotOrientation.VERTICAL,
            true,
            false,
            false);
        response.setHistUserStatisDataset(getHistUserAccount(request, condition, legend));
        long max = getIncreUserAccount(request, condition, legend).getMax();
        long min = getIncreUserAccount(request, condition, legend).getMin();
        decorateLineChart(chart, max, min);
        List<String> urls = createChart(chart, null, 840, 350);
        response.setUrls(urls);
        return new ResponseEntity<HistUserStatisResponse>(response, HttpStatus.OK);
    }
    
    private boolean isSetRequestTimeSuccess(UserHistoryStatisticsCondition condition,
        RestUserHistoryStatisticsRequest request)
    {
        Date beginTime = condition.getBeginTime();
        Date endTime = condition.getEndTime();
        if (null == beginTime)
        {
            LOGGER.error("beginTime be null");
            return false;
        }
        
        if (null == endTime)
        {
            if (beginTime.getTime() > DateUtils.now().getTime())
            {
                return false;
            }
            request.setBeginTime(beginTime.getTime());
            request.setEndTime(DateUtils.now().getTime());
        }
        if (null != endTime && beginTime.getTime() > endTime.getTime())
        {
            return false;
        }
        
        if (null != endTime)
        {
            request.setBeginTime(beginTime.getTime());
            request.setEndTime(endTime.getTime());
        }
        return true;
    }
    
    @InitBinder
    public void initBinder(ServletRequestDataBinder binder)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    private List<String> createChart(JFreeChart upperChart, JFreeChart downChart, int width, int height)
    {
        StatisticsTempChart statisticsTempChart = new StatisticsTempChart();
        ByteArrayOutputStream bosUpperChart = new ByteArrayOutputStream();
        ByteArrayOutputStream bosDownChart = new ByteArrayOutputStream();
        List<String> urls = new ArrayList<String>(2);
        String url1 = "";
        String url2 = "";
        try
        {
            if (upperChart == null)
            {
                url1 = "";
            }
            else
            {
                ChartUtilities.writeChartAsPNG(bosUpperChart, upperChart, width, height);
                byte[] upper = bosUpperChart.toByteArray();
                statisticsTempChart.setChartImage(upper);
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                statisticsTempChart.setId(uuid);
                statisticsService.saveAndDeleteExpired(statisticsTempChart);
                url1 = StatisticsUtils.GETCHART + uuid;
            }
            if (downChart == null)
            {
                url2 = "";
            }
            else
            {
                ChartUtilities.writeChartAsPNG(bosDownChart, downChart, width, height);
                byte[] down = bosDownChart.toByteArray();
                
                statisticsTempChart.setChartImage(down);
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                statisticsTempChart.setId(uuid);
                statisticsService.saveAndDeleteExpired(statisticsTempChart);
                url2 = StatisticsUtils.GETCHART + uuid;
            }
            
            urls.add(url1);
            urls.add(url2);
            return urls;
        }
        catch (Exception exception)
        {
            LOGGER.error("get chart image failed", exception);
            return null;
        }
        finally
        {
            try
            {
                bosUpperChart.close();
            }
            catch (Exception e)
            {
                LOGGER.error("Error in close client stream!", e);
            }
            try
            {
                bosDownChart.close();
            }
            catch (Exception e)
            {
                LOGGER.error("Error in close client stream!", e);
            }
        }
    }
    
    /**
     * @param statisticsDatas
     * @param dataSheet
     * @param textCellStyle
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
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
        Class<?>[] paramTypes = new Class[]{};
        Object[] params = new Object[]{};
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
                methodName = "get" + fieldName.substring(0, 1).toUpperCase(Locale.getDefault())
                    + fieldName.substring(1);
                dataCell = row.createCell(j);
                dataCell.setCellStyle(textCellStyle);
                dataClass = (Class<T>) data.getClass();
                // field`s getter method
                method = dataClass.getMethod(methodName, paramTypes);
                // get value
                value = method.invoke(data, params);
                // value handler
                textValue = valueHandler(fieldName, value);
                dataCell.setCellValue(textValue);
            }
        }
    }
    
    /**
     * this method to get data from db to create sheet data parameters value getted by
     * reflecting dynamicly
     * 
     * @param <T>
     * @param statisticsDatas
     * @param headerType
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
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
        List<String> headers = HEADERS.get(headerType);
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
    
    @SuppressWarnings("deprecation")
    private void decorateBarChart(JFreeChart chart)
    {
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setAxisOffset(RectangleInsets.ZERO_INSETS);
        plot.setNoDataMessage("No data");
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelLocation(AxisLabelLocation.HIGH_END);
        domainAxis.setLabelFont(new Font("宋书", Font.PLAIN, 15));
        domainAxis.setTickLabelFont(new Font("宋书", Font.PLAIN, 15));
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setPositiveArrowVisible(true);
        rangeAxis.setUpperMargin(1);
        rangeAxis.setLabelLocation(AxisLabelLocation.HIGH_END);
        rangeAxis.setLabelFont(new Font("宋书", Font.PLAIN, 15));
        rangeAxis.setVerticalTickLabels(false);
        rangeAxis.setTickLabelsVisible(true);
        rangeAxis.setTickMarksVisible(true);
        rangeAxis.setLabelAngle(Math.PI / 2.0);
        rangeAxis.setLabelLocation(AxisLabelLocation.MIDDLE);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("宋书", Font.PLAIN, 15));
        renderer.setMaximumBarWidth(0.15);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
            TextAnchor.BASELINE_CENTER));
        renderer.setItemLabelAnchorOffset(10D);// 设置柱形图上的文字偏离值
        renderer.setItemLabelsVisible(true);
        renderer.setSeriesPaint(0, new Color(15, 102, 184));
        renderer.setBarPainter(new StandardBarPainter());
    }
    
    @SuppressWarnings("deprecation")
    private void decorateLineChart(JFreeChart chart, long max, long min)
    {
        chart.setBorderVisible(false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setOutlineVisible(false);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setNoDataMessage("No data");
        plot.setNoDataMessageFont(new Font("宋书", Font.PLAIN, 15));
        LegendTitle title = chart.getLegend();
        LegendTitle legend = null;
        if (title != null)
        {
            legend = title;
            legend.setItemFont(new Font("宋书", Font.PLAIN, 15));
            legend.setBorder(0, 0, 0, 0);
        }
        
        LineAndShapeRenderer renderder = (LineAndShapeRenderer) plot.getRenderer();
        renderder.setShapesVisible(true);
        renderder.setDrawOutlines(false);
        renderder.setShapesFilled(true);
        renderder.setBaseShapesVisible(true);
        renderder.setDrawOutlines(true);
        renderder.setUseFillPaint(true);
        renderder.setBaseFillPaint(Color.white);
        renderder.setSeriesStroke(0, new BasicStroke(3F));
        renderder.setSeriesOutlineStroke(0, new BasicStroke(2.0F));
        renderder.setSeriesPaint(0, new Color(15, 102, 184));
        renderder.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4D, -4D, 8D, 8D));
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setLabelAngle(Math.PI / 2.0);
        rangeAxis.setPositiveArrowVisible(true);
        rangeAxis.setLabelFont(new Font("宋书", Font.PLAIN, 15));
        rangeAxis.setRange(min - 1, max + 1);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // rangeAxis.setAutoTickUnitSelection(false);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("宋书", Font.PLAIN, 15));
        domainAxis.setCategoryMargin(0.05);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    }
    
    private long getActualMax(long max, long min, int size)
    {
        if (size != 0)
        {
            return max + (max - min) / size;
        }
        return max;
    }
    
    private String getAllDate(TimePoint timePoint)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(timePoint.getYear());
        if ("day".equalsIgnoreCase(timePoint.getUnit()))
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, timePoint.getYear());
            cal.set(Calendar.DAY_OF_YEAR, timePoint.getNumber());
            int month = cal.get(Calendar.MONTH) + 1;
            sb.append('-').append(month).append('-').append(cal.get(Calendar.DAY_OF_MONTH));
        }
        else if ("season".equalsIgnoreCase(timePoint.getUnit()))
        {
            sb.append('-').append(timePoint.getNumber());
        }
        else if ("month".equalsIgnoreCase(timePoint.getUnit())
            || "week".equalsIgnoreCase(timePoint.getUnit()))
        {
            sb.append('-').append(timePoint.getNumber());
        }
        return sb.toString();
    }
    
    private ConcStatisticsResponse getConcHistoryData(ConcStatisticsRequest concStatisticsRequest)
        throws RestException
    
    {
        return statisticsService.getConcStatisticsHistoryData(concStatisticsRequest);
    }
    
    private TerminalVersionCurrentView getCurrentTerminalDataByDeviceType() throws ParseException
    {
        return terminalManager.getCurrentDataByDeviceType();
    }
    
    private List<TerminalStatisticsInfo> getCurrrentTerminalData()
    {
        return terminalManager.getCurrentData();
    }
    
    /**
     * 
     * @param groupBy
     * @param appId
     * @return
     * @throws RestException
     */
    private DefaultCategoryDataset getCurUserAccount(String groupBy, String appId) throws RestException
    {
        Long account = null;
        String columnItem = "";
        String rowItem = "";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<UserCurrentStatisticsInfo> data = statisticsService.getCurUserAccount(groupBy, appId);
        for (UserCurrentStatisticsInfo userCurrenStatisticsInfo : data)
        {
            if ("application".equalsIgnoreCase(groupBy))
            {
                account = userCurrenStatisticsInfo.getUserCount();
                columnItem = userCurrenStatisticsInfo.getAppId();
                rowItem = "application";
                dataset.addValue(account, rowItem, columnItem);
            }
            if ("region".equalsIgnoreCase(groupBy))
            {
                account = userCurrenStatisticsInfo.getUserCount();
                columnItem = userCurrenStatisticsInfo.getRegionName();
                rowItem = "region";
                dataset.addValue(account, rowItem, columnItem == null ? "no region" : columnItem);
            }
        }
        return dataset;
    }
    
    private String getDate(TimePoint timePoint)
    {
        StringBuilder sb = new StringBuilder();
        if ("year".equalsIgnoreCase(timePoint.getUnit()))
        {
            sb.append(timePoint.getYear());
        }
        else if ("day".equalsIgnoreCase(timePoint.getUnit()))
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, timePoint.getYear());
            cal.set(Calendar.DAY_OF_YEAR, timePoint.getNumber());
            int month = cal.get(Calendar.MONTH) + 1;
            sb.append(month).append('-').append(cal.get(Calendar.DAY_OF_MONTH));
        }
        else
        {
            sb.append(timePoint.getYear()).append('-').append(timePoint.getNumber());
        }
        
        return sb.toString();
    }
    
    private int getDay(Calendar calendar)
    {
        int day = calendar.get(Calendar.YEAR) * 10000;
        day += (calendar.get(Calendar.MONTH) + 1) * 100;
        day += calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }
    
    private List<Terminal> getDeviceType()
    {
        List<Terminal> terminals = new ArrayList<Terminal>(BusinessConstants.INITIAL_CAPACITIES);
        terminals.add(getTerminal(Terminal.CLIENT_TYPE_ANDROID, Terminal.CLIENT_TYPE_ANDROID_STR));
        terminals.add(getTerminal(Terminal.CLIENT_TYPE_IOS, Terminal.CLIENT_TYPE_IOS_STR));
        terminals.add(getTerminal(Terminal.CLIENT_TYPE_PC, Terminal.CLIENT_TYPE_PC_STR));
        terminals.add(getTerminal(Terminal.CLIENT_TYPE_WEB, Terminal.CLIENT_TYPE_WEB_STR));
        return terminals;
    }
    
    private CategoryDataset getFileCurrentData(ObjectCurrentStatisticsRequest fileCurrentRequest)
        throws RestException
    {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        ObjectCurrentStatisticsResponse statisticsResponse = statisticsService.getFileCurrentData(fileCurrentRequest);
        if (statisticsResponse.getData() != null)
        {
            for (ObjectCurrentStatisticsInfo info : statisticsResponse.getData())
            {
                dataSet.addValue(info.getFileCount(), "User data", "user data");
                dataSet.addValue(info.getActualFileCount(), "Actual data", "actual data");
            }
        }
        return dataSet;
    }
    
    private String getFileName(String type)
    {
        
        return FILENAMES.get(type);
    }
    
    private Map<String, Object> getFileStoreAddData(ObjectHistoryStatisticsResponse statisticsResponse)
    {
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        List<ObjectHistoryStatisticsInfo> data = statisticsResponse.getData();
        List<FileStoreAddTableNode> tableData = new ArrayList<FileStoreAddTableNode>(data.size());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String dateStr = null;
        long spaceUsed = 0;
        long actualSpaceUsed = 0;
        FileStoreAddTableNode fileStoreAddTableNode = null;
        for (ObjectHistoryStatisticsInfo info : statisticsResponse.getData())
        {
            dateStr = getDate(info.getTimePoint());
            spaceUsed = info.getSpaceUsed();
            actualSpaceUsed = info.getActualSpaceUsed();
            dataset.addValue(info.getActualSpaceUsed(), "Actual space", dateStr);
            dataset.addValue(info.getSpaceUsed(), "Current space", dateStr);
            fileStoreAddTableNode = new FileStoreAddTableNode(spaceUsed, actualSpaceUsed,
                getAllDate(info.getTimePoint()));
            tableData.add(fileStoreAddTableNode);
        }
        map.put(StatisticsConstants.DATA, dataset);
        map.put(StatisticsConstants.TABLE_DATA, tableData);
        return map;
    }
    
    private DefaultPieDataset getFileStoreCurrentApplicationData(
        NodeCurrentStatisticsResponse statisticsResponse)
    {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        if (statisticsResponse.getData() == null)
        {
            return pieDataset;
        }
        for (NodeCurrentStatisticsInfo info : statisticsResponse.getData())
        {
            pieDataset.setValue(info.getAppId(), info.getSpaceUsed());
        }
        return pieDataset;
    }
    
    private DefaultPieDataset getFileStoreCurrentRegionData(NodeCurrentStatisticsResponse statisticsResponse)
    {
        DefaultPieDataset pieDataSet = new DefaultPieDataset();
        if (statisticsResponse.getData() != null)
        {
            for (NodeCurrentStatisticsInfo info : statisticsResponse.getData())
            {
                pieDataSet.setValue(info.getRegionName(), info.getSpaceUsed());
            }
        }
        return pieDataSet;
    }
    
    private NodeHistoryStatisticsResponse getHistoryFileStoreData(FileStoreHistoryRequest historyRequest)
        throws RestException
    {
        NodeHistoryStatisticsResponse statisticsResponse = statisticsService.getNodeHistroyData(historyRequest);
        return statisticsResponse;
    }
    
    private ObjectHistoryStatisticsResponse getHistoryObjectData(
        ObjectHistoryStatisticsRequest statisticsRequest) throws RestException
    {
        ObjectHistoryStatisticsResponse statisticsResponse = statisticsService.getFileHistoryData(statisticsRequest);
        return statisticsResponse;
    }
    
    private TerminalDeviceHistoryView getHistoryTerminalData(TerminalDeviceHistoryRequest terminalRequest)
        throws ParseException
    {
        if (terminalRequest.getBeginTime() == null)
        {
            terminalRequest.setBeginTime(new Date());
        }
        if (terminalRequest.getEndTime() == null)
        {
            terminalRequest.setEndTime(new Date());
        }
        return terminalManager.getListGroupByDeviceType(StatisticsUtils.getDay(terminalRequest.getBeginTime()
            .getTime()),
            StatisticsUtils.getDay(terminalRequest.getEndTime().getTime()),
            terminalRequest.getInterval());
    }
    
    private TerminalVersionHistoryView getHistoryTerminalVersionData(
        TerminalVersionHitoryRequest versionRequest) throws ParseException
    {
        if (versionRequest.getBeginTime() == null)
        {
            versionRequest.setBeginTime(new Date());
        }
        if (versionRequest.getEndTime() == null)
        {
            versionRequest.setEndTime(new Date());
        }
        
        return terminalManager.getListByDeviceType(StatisticsUtils.getDay(versionRequest.getBeginTime()
            .getTime()),
            StatisticsUtils.getDay(versionRequest.getEndTime().getTime()),
            versionRequest.getInterval(),
            versionRequest.getDeviceType());
    }
    
    private HistUserStatisDataset getHistUserAccount(RestUserHistoryStatisticsRequest request,
        UserHistoryStatisticsCondition condition, String legend) throws RestException
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HistUserStatisDataset histUserStatisDataset = new HistUserStatisDataset();
        long account = 0L;
        AuthApp authApp = authAppService.getByAuthAppID(condition.getAuthAppId());
        List<UserHistoryStatisticsInfo> data = statisticsService.getHistUserAccount(request, authApp);
        if (data.isEmpty())
        {
            histUserStatisDataset.setMax(5);
            histUserStatisDataset.setMin(1);
            histUserStatisDataset.setData(null);
            histUserStatisDataset.setDefaultCategoryDataset(dataset);
            return histUserStatisDataset;
        }
        long min = data.get(0).getUserCount();
        long max = data.get(0).getUserCount();
        TimePoint timePoint = null;
        String yearStr = null;
        for (UserHistoryStatisticsInfo userHistoryStatisticsInfo : data)
        {
            account = userHistoryStatisticsInfo.getUserCount();
            if (account > max)
            {
                max = account;
            }
            if (account < min)
            {
                min = account;
            }
            userHistoryStatisticsInfo.setLegend(legend);
            timePoint = userHistoryStatisticsInfo.getTimePoint();
            // timePoint.getYear();
            if ("day".equalsIgnoreCase(timePoint.getUnit()))
            {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, timePoint.getYear());
                cal.set(Calendar.DAY_OF_YEAR, timePoint.getNumber());
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                userHistoryStatisticsInfo.setDate(day);
                userHistoryStatisticsInfo.setMonth(month);
            }
            yearStr = getShowXnumber(timePoint);
            dataset.addValue(account, legend, yearStr);
        }
        histUserStatisDataset.setMax(max);
        histUserStatisDataset.setMin(min);
        histUserStatisDataset.setData(data);
        histUserStatisDataset.setDefaultCategoryDataset(dataset);
        return histUserStatisDataset;
    }
    
    private HistUserStatisDataset getIncreUserAccount(RestUserHistoryStatisticsRequest request,
        UserHistoryStatisticsCondition condition, String legend) throws RestException
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HistUserStatisDataset histUserStatisDataset = new HistUserStatisDataset();
        int added = 0;
        AuthApp authApp = authAppService.getByAuthAppID(condition.getAuthAppId());
        List<UserHistoryStatisticsInfo> data = statisticsService.getHistUserAccount(request, authApp);
        histUserStatisDataset.setData(data);
        if (data.isEmpty())
        {
            histUserStatisDataset.setMax(5);
            histUserStatisDataset.setMin(1);
            histUserStatisDataset.setDefaultCategoryDataset(null);
            return histUserStatisDataset;
        }
        long min = data.get(0).getAdded();
        long max = data.get(0).getAdded();
        TimePoint timePoint = null;
        String yearStr = null;
        for (UserHistoryStatisticsInfo userHistoryStatisticsInfo : data)
        {
            added = userHistoryStatisticsInfo.getAdded();
            if (added > max)
            {
                max = added;
            }
            if (added < min)
            {
                min = added;
            }
            timePoint = userHistoryStatisticsInfo.getTimePoint();
            // timePoint.getYear();
            yearStr = getShowXnumber(timePoint);
            dataset.addValue(added, legend, yearStr);
        }
        histUserStatisDataset.setMax(max);
        histUserStatisDataset.setMin(min);
        histUserStatisDataset.setDefaultCategoryDataset(dataset);
        return histUserStatisDataset;
    }
    
    private NodeCurrentStatisticsResponse getNodeCurrentData(NodeCurrentStatisticsRequest nodeCurrentRequest)
        throws RestException
    {
        return statisticsService.getNodeCurrentData(nodeCurrentRequest);
    }
    
    private String getRegionNameById(int regionId)
    {
        
        List<RestRegionInfo> regionInfoList = statisticsService.getRegionInfo();
        if (regionInfoList == null)
        {
            return "";
        }
        String legend = "";
        for (RestRegionInfo regionInfo : regionInfoList)
        {
            if (regionInfo.getId() == regionId)
            {
                legend = regionInfo.getName();
            }
        }
        return legend;
    }
    
    private CategoryDataset getSystemStoreCurrentData(ObjectCurrentStatisticsRequest fileCurrentRequest)
        throws RestException
    {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        ObjectCurrentStatisticsResponse statisticsResponse = statisticsService.getFileCurrentData(fileCurrentRequest);
        if (statisticsResponse.getData() != null)
        {
            for (ObjectCurrentStatisticsInfo info : statisticsResponse.getData())
            {
                dataSet.addValue(info.getSpaceUsed(), "User space data", "space data");
                dataSet.addValue(info.getActualSpaceUsed(), "Actual space data", "actual space data");
            }
        }
        return dataSet;
    }
    
    private Terminal getTerminal(Integer deviceType, String deviceName)
    {
        Terminal terminal = new Terminal();
        terminal.setDeviceName(deviceName);
        terminal.setDeviceType(deviceType);
        return terminal;
    }
    
    private UserClusterStatisticsList getUserStoreCurrenData(RestUserClusterStatisticsRequest interzoneRequest)
        throws RestException
    {
        return statisticsService.getUserStoreCurrenData(interzoneRequest);
    }
    
    private DefaultPieDataset getUserStoreCurrentData(UserClusterStatisticsList clusterStatisticsList)
    {
        DefaultPieDataset pieDataSet = new DefaultPieDataset();
        if (clusterStatisticsList.getData() != null)
        {
            StringBuffer sbTmep = null;
            StringBuffer sb = null;
            for (UserClusterStatisticsInfo info : clusterStatisticsList.getData())
            {
                if (info.getEnd() == null)
                {
                    sbTmep = new StringBuffer();
                    sbTmep.append(info.getBegin() / StatisticsConstants.TRANS_MB);
                    sbTmep.append("-Max");
                    pieDataSet.setValue(sbTmep.toString(), info.getUserCount());
                    continue;
                }
                sb = new StringBuffer();
                sb.append(info.getBegin() / StatisticsConstants.TRANS_MB);
                sb.append('-');
                sb.append(info.getEnd() / StatisticsConstants.TRANS_MB);
                pieDataSet.setValue(sb.toString(), info.getUserCount());
            }
        }
        return pieDataSet;
        
    }
    
    private Map<String, Object> transAddDefaultData(ObjectHistoryStatisticsResponse statisticsResponse)
    {
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        List<ObjectHistoryStatisticsInfo> data = statisticsResponse.getData();
        Collections.sort(data, new StatisticsBeanTimepointSorter());
        List<FileTableNode> tableData = new ArrayList<FileTableNode>(data.size());
        long max = 0;
        long min = 0;
        String dateStr = null;
        long actualFileCount = 0;
        long fileCount = 0;
        FileTableNode fileTableNode = null;
        if (statisticsResponse.getData() != null)
        {
            for (ObjectHistoryStatisticsInfo info : statisticsResponse.getData())
            {
                dateStr = getDate(info.getTimePoint());
                actualFileCount = info.getAddedActualFileCount();
                fileCount = info.getAddedFileCount();
                dataSet.addValue(info.getAddedActualFileCount(), "Actual file count", dateStr);
                dataSet.addValue(fileCount, "User file count", dateStr);
                fileTableNode = new FileTableNode(fileCount, actualFileCount, getAllDate(info.getTimePoint()));
                tableData.add(fileTableNode);
                if (actualFileCount >= fileCount)
                {
                    max = getMax(max, actualFileCount);
                    min = getMin(min, fileCount);
                }
                else if (fileCount >= actualFileCount)
                {
                    max = getMax(max, fileCount);
                    min = getMin(min, actualFileCount);
                }
            }
        }
        map.put(StatisticsConstants.MAX_VALUE, max);
        map.put(StatisticsConstants.MIN_VALUE, min);
        map.put(StatisticsConstants.DATA, dataSet);
        map.put(StatisticsConstants.TABLE_DATA, tableData);
        return map;
    }
    
    private long getMin(long min, long fileCount)
    {
        if (min > fileCount)
        {
            min = fileCount;
        }
        return min;
    }
    
    private long getMax(long max, long actualFileCount)
    {
        if (max < actualFileCount)
        {
            max = actualFileCount;
        }
        return max;
    }
    
    private Map<String, Object> transConcStatisticsData(ConcStatisticsResponse statisticsResponse)
    {
        Map<String, Object> values = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        Integer max = 0;
        Integer min = 0;
        Integer maxDownLoad = null;
        Integer maxUpload = null;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String downLoadTitle = "Concurrent download num";
        String uploadLoadTitle = "Concurrent upload num";
        List<ConcStatisticsInfo> datas = statisticsResponse.getData();
        Collections.sort(datas, new StatisticsBeanTimepointSorter());
        List<ConStatisticsTableNode> tableData = new ArrayList<ConStatisticsTableNode>(datas.size());
        String dateStr = null;
        ConStatisticsTableNode conStatisticsTableNode = null;
        for (ConcStatisticsInfo info : datas)
        {
            dateStr = getDate(info.getTimePoint());
            maxDownLoad = info.getMaxDownload();
            maxUpload = info.getMaxUpload();
            dataset.addValue(maxDownLoad, downLoadTitle, dateStr);
            dataset.addValue(maxUpload, uploadLoadTitle, dateStr);
            conStatisticsTableNode = new ConStatisticsTableNode(maxUpload, maxDownLoad,
                getAllDate(info.getTimePoint()));
            tableData.add(conStatisticsTableNode);
            if (maxDownLoad > maxUpload)
            {
                if (max < maxDownLoad)
                {
                    max = maxDownLoad;
                }
                if (min > maxUpload)
                {
                    min = maxUpload;
                }
            }
            else
            {
                if (max < maxUpload)
                {
                    max = maxUpload;
                }
                if (min > maxDownLoad)
                {
                    min = maxDownLoad;
                }
            }
        }
        values.put(StatisticsConstants.MAX_VALUE, max);
        values.put(StatisticsConstants.MIN_VALUE, min);
        values.put(StatisticsConstants.DATA, dataset);
        values.put(StatisticsConstants.TABLE_DATA, tableData);
        return values;
    }
    
    private CategoryDataset transCurrentTerminalData(List<TerminalStatisticsInfo> terminalInfoes)
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (TerminalStatisticsInfo ts : terminalInfoes)
        {
            
            dataset.addValue(ts.getUserCount(), ts.getDeviceType(), ts.getDeviceType());
        }
        return dataset;
    }
    
    private Map<String, Object> transCurrentVersionData(String deviceName,
        List<TerminalCurrentVersionView> currentView)
    {
        Map<String, Object> values = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (currentView == null)
        {
            values.put(StatisticsConstants.DATA, dataset);
            return values;
        }
        for (TerminalCurrentVersionView historyNode : currentView)
        {
            if (StringUtils.equals(historyNode.getDeviceName(), deviceName))
            {
                for (TerminalCurrentVersionDay versionDay : historyNode.getData())
                {
                    dataset.addValue(versionDay.getUserCount(), deviceName, versionDay.getClientVersion());
                }
            }
        }
        values.put(StatisticsConstants.DATA, dataset);
        return values;
    }
    
    private Map<String, Object> transFileStoreHistoryData(NodeHistoryStatisticsResponse statisticsResponse)
    {
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        List<NodeHistoryStatisticsInfo> data = statisticsResponse.getData();
        List<FileStoreHistoryTableNode> tableData = new ArrayList<FileStoreHistoryTableNode>(data.size());
        long max = DEFAULT_VAL;
        long min = DEFAULT_VAL;
        if (statisticsResponse.getData() != null)
        {
            String dateStr = null;
            long spaceUsed = DEFAULT_VAL;
            FileStoreHistoryTableNode fileStoreHistoryTableNode = null;
            for (NodeHistoryStatisticsInfo info : statisticsResponse.getData())
            {
                dateStr = getDate(info.getTimePoint());
                spaceUsed = info.getSpaceUsed();
                dataSet.addValue(spaceUsed, "Content", dateStr);
                if (spaceUsed > max)
                {
                    max = spaceUsed;
                }
                if (spaceUsed < min)
                {
                    min = spaceUsed;
                }
                fileStoreHistoryTableNode = new FileStoreHistoryTableNode(spaceUsed,
                    getAllDate(info.getTimePoint()));
                tableData.add(fileStoreHistoryTableNode);
            }
        }
        map.put(StatisticsConstants.MAX_VALUE, max);
        map.put(StatisticsConstants.MIN_VALUE, min);
        map.put(StatisticsConstants.DATA, dataSet);
        map.put(StatisticsConstants.TABLE_DATA, tableData);
        return map;
    }
    
    private Map<String, Object> transHistoryDefaultData(ObjectHistoryStatisticsResponse statisticsResponse)
    {
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        List<FileTableNode> tableData = new ArrayList<FileTableNode>(BusinessConstants.INITIAL_CAPACITIES);
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        long max = 0;
        long min = 0;
        if (statisticsResponse.getData() != null)
        {
            List<ObjectHistoryStatisticsInfo> data = statisticsResponse.getData();
            Collections.sort(data, new StatisticsBeanTimepointSorter());
            String dateStr = null;
            long actualFileCount = DEFAULT_VAL;
            long fileCount = DEFAULT_VAL;
            FileTableNode fileTableNode = null;
            for (ObjectHistoryStatisticsInfo info : statisticsResponse.getData())
            {
                dateStr = getDate(info.getTimePoint());
                dataSet.addValue(info.getActualFileCount(), "Actual file count", dateStr);
                dataSet.addValue(info.getFileCount(), "User file count", dateStr);
                actualFileCount = info.getActualFileCount();
                fileCount = info.getFileCount();
                fileTableNode = new FileTableNode(fileCount, actualFileCount, getAllDate(info.getTimePoint()));
                tableData.add(fileTableNode);
                if (actualFileCount >= fileCount)
                {
                    max = getMax(max, actualFileCount);
                    min = getMin(min, fileCount);
                }
                else if (fileCount >= actualFileCount)
                {
                    max = getMax(max, fileCount);
                    min = getMin(min, actualFileCount);
                }
            }
        }
        
        map.put(StatisticsConstants.DATA, dataSet);
        map.put(StatisticsConstants.MAX_VALUE, max);
        map.put(StatisticsConstants.MIN_VALUE, min);
        map.put(StatisticsConstants.TABLE_DATA, tableData);
        return map;
    }
    
    private Map<String, Object> transHistoryTerminalData(TerminalDeviceHistoryView deviceHistoryData)
    {
        Map<String, Object> values = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int max = 0;
        int min = 0;
        int userCount = 0;
        ThirdTerminalData tempThird = null;
        List<ThirdTerminalData> thirdList = new ArrayList<ThirdTerminalData>(10);
        List<TerminalHistoryNode> historyList = null;
        for (TerminalDeviceTypeView termianlDevice : deviceHistoryData.getDeviceHistoryList())
        {
            historyList = termianlDevice.getDataList();
            for (TerminalHistoryNode terminalNode : historyList)
            {
                userCount = terminalNode.getUserCount();
                tempThird = new ThirdTerminalData(userCount, termianlDevice.getDeviceType(),
                    termianlDevice.getDeviceName(), terminalNode.getTimePoint());
                thirdList.add(tempThird);
                if (max < userCount)
                {
                    max = userCount;
                }
                if (min > userCount)
                {
                    min = userCount;
                }
            }
        }
        Collections.sort(thirdList, new TerminalHistoryDateSortor());
        for (ThirdTerminalData tempData : thirdList)
        {
            dataset.addValue(tempData.getCount(), tempData.getClientVersion(), tempData.getDateStr());
        }
        List<TerminalHistoryTableResponse> tableDataes = StatisticsPacker.packerTerminalHistoryData(thirdList);
        values.put(StatisticsConstants.DATA, dataset);
        values.put(StatisticsConstants.MAX_VALUE, max);
        values.put(StatisticsConstants.MIN_VALUE, min);
        values.put(StatisticsConstants.TABLE_DATA, tableDataes);
        return values;
    }
    
    private Map<String, Object> transSystemStoreHistoryData(ObjectHistoryStatisticsResponse statisticsResponse)
    {
        Map<String, Object> map = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        List<ObjectHistoryStatisticsInfo> data = statisticsResponse.getData();
        Collections.sort(data, new StatisticsBeanTimepointSorter());
        List<SystemStoreHistoryTableNode> tableData = new ArrayList<SystemStoreHistoryTableNode>(data.size());
        long max = 0;
        long min = 0;
        String dateStr = null;
        long actualSpaceUsed = DEFAULT_VAL;
        long spaceUsed = DEFAULT_VAL;
        SystemStoreHistoryTableNode systemStoreHistoryTableNode = null;
        for (ObjectHistoryStatisticsInfo info : data)
        {
            dateStr = getDate(info.getTimePoint());
            actualSpaceUsed = info.getActualSpaceUsed();
            spaceUsed = info.getSpaceUsed();
            dataSet.addValue(actualSpaceUsed, "actual", dateStr);
            dataSet.addValue(spaceUsed, "current", dateStr);
            systemStoreHistoryTableNode = new SystemStoreHistoryTableNode(spaceUsed, actualSpaceUsed,
                getAllDate(info.getTimePoint()));
            tableData.add(systemStoreHistoryTableNode);
            if (actualSpaceUsed >= spaceUsed)
            {
                max = getMax(max, actualSpaceUsed);
                min = getMin(min, spaceUsed);
            }
            else if (spaceUsed >= actualSpaceUsed)
            {
                max = getMax(max, spaceUsed);
                min = getMin(min, actualSpaceUsed);
            }
        }
        map.put(StatisticsConstants.DATA, dataSet);
        map.put(StatisticsConstants.MAX_VALUE, max);
        map.put(StatisticsConstants.MIN_VALUE, min);
        map.put(StatisticsConstants.TABLE_DATA, tableData);
        return map;
    }
    
    private Map<String, Object> transVersionHistoryData(TerminalVersionHistoryView versionView)
    {
        Map<String, Object> values = new HashMap<String, Object>(StatisticsConstants.LIST_SIZE);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<TerminalVersionHistoryTableNode> tableData = new ArrayList<TerminalVersionHistoryTableNode>(100);
        int max = 0;
        int min = 0;
        List<ThirdTerminalData> thirdList = new ArrayList<ThirdTerminalData>(10);
        ThirdTerminalData tempThirdData = null;
        List<TerminalHistoryNode> terminalList = null;
        int termpCount = 0;
        TerminalVersionHistoryTableNode terminalVersionHistoryTableNode = null;
        for (TerminalVersionTypeView versionType : versionView.getVersionHistoyList())
        {
            terminalList = versionType.getDataList();
            for (TerminalHistoryNode terminalHistoryNode : terminalList)
            {
                tempThirdData = new ThirdTerminalData(terminalHistoryNode.getUserCount(),
                    versionType.getVersionName(), terminalHistoryNode.getTimePoint());
                thirdList.add(tempThirdData);
                // dataset.addValue(terminalHistoryNode.getUserCount(),
                // versionType.getVersionName(), dateStr);
                termpCount = terminalHistoryNode.getUserCount();
                terminalVersionHistoryTableNode = new TerminalVersionHistoryTableNode(
                    terminalHistoryNode.getUserCount(), versionType.getVersionName(),
                    getAllDate(terminalHistoryNode.getTimePoint()), terminalHistoryNode.getTimePoint());
                tableData.add(terminalVersionHistoryTableNode);
                if (max < termpCount)
                {
                    max = termpCount;
                }
                if (min > termpCount)
                {
                    min = termpCount;
                }
            }
        }
        TerminalHistoryDateSortor timePointSorter = new TerminalHistoryDateSortor();
        Collections.sort(thirdList, timePointSorter);
        for (ThirdTerminalData tempData : thirdList)
        {
            dataset.addValue(tempData.getCount(), tempData.getClientVersion(), tempData.getDateStr());
        }
        values.put(StatisticsConstants.DATA, dataset);
        values.put(StatisticsConstants.MAX_VALUE, max);
        values.put(StatisticsConstants.MIN_VALUE, min);
        Collections.sort(tableData, timePointSorter);
        values.put(StatisticsConstants.TABLE_DATA, tableData);
        return values;
    }
    
    /**
     * handle the getted value judge it`s object type,then cast it to String and return
     * 
     * @param fieldName
     * @param value
     * @return
     */
    private String valueHandler(String fieldName, Object value)
    {
        String textValue = null;
        if (DEVICETYPE.equals(fieldName))
        {
            textValue = getDeviceTypeValue(value);
        }
        else
        {
            if (value instanceof Integer || value instanceof Long)
            {
                textValue = String.valueOf(value);
            }
            if (value instanceof String)
            {
                textValue = (String) value;
            }
        }
        return textValue;
    }
    
    private String getDeviceTypeValue(Object value)
    {
        String textValue = null;
        if (value instanceof Integer)
        {
            Integer device = Integer.valueOf(String.valueOf(value));
            switch (device)
            {
                case Terminal.CLIENT_TYPE_WEB:
                    textValue = Terminal.CLIENT_TYPE_WEB_STR;
                    break;
                case Terminal.CLIENT_TYPE_PC:
                    textValue = Terminal.CLIENT_TYPE_PC_STR;
                    break;
                case Terminal.CLIENT_TYPE_IOS:
                    textValue = Terminal.CLIENT_TYPE_IOS_STR;
                    break;
                case Terminal.CLIENT_TYPE_ANDROID:
                    textValue = Terminal.CLIENT_TYPE_ANDROID_STR;
                    break;
                default:
                    break;
            }
        }
        return textValue;
    }
}
