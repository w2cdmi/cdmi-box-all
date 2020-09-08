package com.huawei.sharedrive.app.dataserver.service.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


public final class RegionSpareUtils
{
    private RegionSpareUtils()
    {
        
    }
    
    private static final String FILE_NAME = "region_spare.xml";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RegionSpareUtils.class);
    
    private static Map<String, RegionSpareRelation> map = null;
    
    private static final String SEP_SIGNAL = "@@";
    
    
    private static Map<Integer, Integer> spareSizeMap = null;
    
    public static RegionSpareRelation getRegionSpare(int mainRegionId, int priority)
    {
        try
        {
            initMap();
        }
        catch (IOException e)
        {
            LOGGER.error("Can not init xml", e);
            return null;
        }
        return map.get(mainRegionId + SEP_SIGNAL + priority);
    }
    
    public static int getSpareSize(int mainRegionId)
    {
        try
        {
            initMap();
        }
        catch (IOException e)
        {
            LOGGER.error("Can not init xml", e);
            return 0;
        }
        return spareSizeMap.get(mainRegionId);
    }
    
    private static void initMap() throws IOException
    {
        if(null == map)
        {
            synchronized(RegionSpareUtils.class)
            {
                if(null == map)
                {
                    parseXml();
                }
            }
        }
    }
    
    private static void parseXml() throws IOException
    {
        InputStream in = null;
        try
        {
            map = new HashMap<String, RegionSpareRelation>(10);
            URL url = RegionSpareUtils.class.getResource('/' + FILE_NAME);
            if (null == url)
            {
                return;
            }
            File file = new File(url.getFile());
            in = new FileInputStream(file);
            XStream stream = new XStream();
            stream.alias("regionSpareList", List.class);
            stream.alias("spareList", List.class);
            stream.alias("RegionSpare", RegionSpare.class);
            stream.alias("RegionSpareRelation", RegionSpareRelation.class);
            
            @SuppressWarnings("unchecked")
            List<RegionSpare> regionSpareList = (List<RegionSpare>) stream.fromXML(in);
            if (null == regionSpareList)
            {
                return;
            }
            spareSizeMap = new HashMap<Integer, Integer>(10);
            for (RegionSpare tempRegionSpare : regionSpareList)
            {
                for(RegionSpareRelation tempRelation: tempRegionSpare.getSpareList())
                {
                    map.put(tempRegionSpare.getMainRegion() + "@@" + tempRelation.getPriority(), tempRelation);
                }
                spareSizeMap.put(tempRegionSpare.getMainRegion(), tempRegionSpare.getSpareList().size());
            }
            LOGGER.info("map size is " + map.size());
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }
    
}
