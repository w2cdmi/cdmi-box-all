package pw.cdmi.box.uam.statistics.domain;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.uam.statistics.service.StatisticsService;

public final class StatisticsUtils
{
    private StatisticsUtils()
    {
    }
    
    public static final String GETCHART = "/statistics/getchart/";
    
    private static Logger logger = LoggerFactory.getLogger(StatisticsUtils.class);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static String createView(String chartType, String title, String yAxisTitle, String xAxisTitle,
        Object categoryDataset, StatisticsService statisticsService, int width, int height, long max,
        long min, Double barWidth, Double itemMargin)
    {
        JFreeChart freeChart = createChart(chartType,
            title,
            yAxisTitle,
            xAxisTitle,
            categoryDataset,
            max,
            min,
            barWidth,
            itemMargin);
        StatisticsTempChart statisticsTempChart = new StatisticsTempChart();
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        
        String url;
        try
        {
            ChartUtilities.writeChartAsPNG(byteOutputStream, freeChart, width, height);
            byte[] bytePng = byteOutputStream.toByteArray();
            String chartId = UUID.randomUUID().toString().replaceAll("-", "");
            statisticsTempChart.setId(chartId);
            statisticsTempChart.setChartImage(bytePng);
            statisticsService.saveAndDeleteExpired(statisticsTempChart);
            url = GETCHART + chartId;
            logger.info("Imager url is" + url);
            return url;
        }
        catch (Exception e)
        {
            logger.error("get chart image failed", e);
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(byteOutputStream);
        }
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private static JFreeChart createChart(String chartType, String title, String yAxisTitle,
        String xAxisTitle, Object object, long max, long min, Double barWidth, Double itemMargin)
    {
        JFreeChart freeChart = null;
        
        if (StringUtils.equals(chartType, StatisticsConstants.CHART_BAR))
        {
            if (object instanceof DefaultCategoryDataset)
            {
                DefaultCategoryDataset categoryDataset = (DefaultCategoryDataset) object;
                freeChart = ChartFactory.createBarChart(title,
                    xAxisTitle,
                    yAxisTitle,
                    categoryDataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);
                decorateBarChart(freeChart, barWidth, itemMargin);
            }
        }
        else if (StringUtils.equals(chartType, StatisticsConstants.CHART_LINE))
        {
            if (object instanceof DefaultCategoryDataset)
            {
                DefaultCategoryDataset categoryDataset = (DefaultCategoryDataset) object;
                freeChart = ChartFactory.createLineChart(title,
                    xAxisTitle,
                    yAxisTitle,
                    categoryDataset,
                    PlotOrientation.VERTICAL,
                    true,
                    false,
                    false);
                decorateLineChart(freeChart, max, min);
            }
            
        }
        else if (StringUtils.equals(chartType, StatisticsConstants.CHART_PIE))
        {
            if (object instanceof DefaultPieDataset)
            {
                DefaultPieDataset categoryDataset = (DefaultPieDataset) object;
                freeChart = ChartFactory.createPieChart(title, categoryDataset, true, true, false);
                decoratePieChart(freeChart, categoryDataset);
            }
        }
        
        return freeChart;
        
    }
    
    /**
     * 渲染chart
     * 
     * @param jfreeChart
     */
    @SuppressWarnings("deprecation")
    private static void decorateBarChart(JFreeChart jfreeChart, double barWidth, double itemMargin)
    {
        CategoryPlot plot = (CategoryPlot) jfreeChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setAxisOffset(RectangleInsets.ZERO_INSETS);
        plot.setNoDataMessage("No Data");// 如果后台无数据
        // 得到x轴用于自定义
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelLocation(AxisLabelLocation.HIGH_END);
        domainAxis.setLabelFont(new Font("宋书", Font.PLAIN, 15));
        domainAxis.setTickLabelFont(new Font("宋书", Font.PLAIN, 15));
        domainAxis.setVisible(true);
        domainAxis.setTickLabelsVisible(true);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        // domainAxis.s
        // 得到y轴用于自定义
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setPositiveArrowVisible(true);
        rangeAxis.setUpperMargin(0.1);
        rangeAxis.setLabelLocation(AxisLabelLocation.HIGH_END);
        rangeAxis.setLabelFont(new Font("宋书", Font.PLAIN, 15));
        rangeAxis.setVerticalTickLabels(false);
        rangeAxis.setTickLabelsVisible(true);
        rangeAxis.setTickMarksVisible(true);
        rangeAxis.setLabelAngle(0);
        // 得到renderer用于渲染具体图块
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(itemMargin);// 设置量柱子间距
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("宋书", Font.PLAIN, 15));
        renderer.setMaximumBarWidth(barWidth);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
            TextAnchor.BASELINE_CENTER));
        renderer.setItemLabelAnchorOffset(10D);// 设置柱形图上的文字偏离值
        renderer.setItemLabelsVisible(false);
        renderer.setSeriesPaint(0, new Color(42, 144, 91), true);
        renderer.setSeriesPaint(1, new Color(15, 102, 184), true);
        renderer.setBarPainter(new StandardBarPainter());
    }
    
    private static void decoratePieChart(JFreeChart chart, PieDataset dataset)
    {
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");// 创建主题样式
        standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));// 设置标题字体
        standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));// 设置图例的字体
        standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));// 设置轴向的字体
        ChartFactory.setChartTheme(standardChartTheme);// 应用主题样式
        
        // 得到plot用于自定义数据区图表
        PiePlot plot = (PiePlot) chart.getPlot();
        int length = StatisticsConstants.COLORS.length;
        int size = dataset.getItemCount();
        int count = length > size ? size : length;
        while (count > 0)
        {
            plot.setSectionPaint(dataset.getKey(count - 1), StatisticsConstants.COLORS[count - 1]);
            --count;
        }
        
        plot.setBackgroundPaint(null);
        plot.setSectionOutlinesVisible(true);
        plot.setShadowPaint(null);
        plot.setLabelLinksVisible(false);
        plot.setLabelShadowPaint(null);
        plot.setLabelBackgroundPaint(null);
        plot.setLabelFont(new Font("隶书", Font.BOLD, 12));
        plot.setLabelOutlinePaint(null);
        plot.setNoDataMessage("No Data");// 如果后台无数据
        PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0}={1}({2})",
            new DecimalFormat("0"), new DecimalFormat("0.00%"));
        // 饼块标示自定义
        plot.setLabelGenerator(generator);
        plot.setLegendLabelGenerator(generator);
        
    }
    
    /**
     * 渲染chart
     * 
     * @param chart
     */
    @SuppressWarnings("deprecation")
    private static void decorateLineChart(JFreeChart chart, long max, long min)
    {
        chart.setBorderVisible(false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setOutlineVisible(false);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setNoDataMessage("No data");// 如果后台无数据
        /** 样本示例图标对象 */
        LegendTitle title = chart.getLegend();
        if (title != null)
        {
            LegendTitle legend = title;
            legend.setItemFont(new Font("宋书", Font.PLAIN, 15));
            legend.setBorder(0, 0, 0, 0);
        }
        
        LineAndShapeRenderer renderder = (LineAndShapeRenderer) plot.getRenderer();
        renderder.setShapesVisible(true);
        renderder.setDrawOutlines(false);
        renderder.setAutoPopulateSeriesFillPaint(true);
        renderder.setAutoPopulateSeriesOutlinePaint(true);
        renderder.setAutoPopulateSeriesOutlineStroke(true);
        renderder.setAutoPopulateSeriesPaint(true);
        renderder.setAutoPopulateSeriesShape(true);
        renderder.setAutoPopulateSeriesStroke(true);
        renderder.setShapesFilled(true);
        renderder.setBaseShapesVisible(true);
        renderder.setDrawOutlines(true);
        renderder.setUseFillPaint(true);
        renderder.setBaseFillPaint(Color.white);
        renderder.setSeriesStroke(0, new BasicStroke(3F));
        renderder.setSeriesOutlineStroke(0, new BasicStroke(2.0F));
        renderder.setSeriesStroke(1, new BasicStroke(3F));
        renderder.setSeriesOutlineStroke(1, new BasicStroke(2.0F));
        renderder.setSeriesPaint(0, new Color(15, 102, 184));// 设置第一条数据线颜色
        renderder.setSeriesPaint(1, new Color(4, 199, 47));// 设置第二条数据线颜色
        renderder.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4D, -4D, 8D, 8D));// 将折线数据节点形状改为圆形
        renderder.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-4D, -4D, 8D, 8D));// 将折线数据节点形状改为圆形
        ValueAxis rangeAxis = plot.getRangeAxis();// 获取y轴对象
        rangeAxis.setAutoRange(true);// 设y轴为数据自适范围
        rangeAxis.setPositiveArrowVisible(true);// 设y轴箭头可见
        
        rangeAxis.setLabelFont(new Font("宋书", Font.PLAIN, 15));
        // 从后台得到数的最小和最大。然后设置y轴范围的上下
        rangeAxis.setRange(min - 1, max + 1);
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("宋书", Font.PLAIN, 15));
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
    }
    
    public static Integer getDay(Long dateTime)
    {
        Integer beginDay = null;
        if (dateTime != null)
        {
            Calendar ca = Calendar.getInstance();
            ca.setTime(new Date(dateTime));
            beginDay = ca.get(Calendar.YEAR) * 10000 + (ca.get(Calendar.MONTH) + 1) * 100
                + ca.get(Calendar.DAY_OF_MONTH);
        }
        return beginDay;
    }
}
