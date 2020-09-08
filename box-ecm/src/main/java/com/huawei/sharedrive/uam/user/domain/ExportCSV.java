package com.huawei.sharedrive.uam.user.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportCSV
{
    
    private static Logger logger = LoggerFactory.getLogger(ExportCSV.class);
    
    public File createCSVFile(List<Map<String, String>> exportData, Map<String, String> rowMapper,
        String templatePath, String fileName)
    {
        File csvFile = null;
        BufferedWriter csvFileOutputStream = null;
        try
        {
            csvFile = new File(templatePath + fileName + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists())
            {
                FileUtils.forceMkdir(parent);
            }
            if (csvFile.exists())
            {
                FileUtils.forceDelete(csvFile);
            }
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile),
                "GB2312"), 1024);
            output(exportData, rowMapper, csvFileOutputStream);
            
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            IOUtils.closeQuietly(csvFileOutputStream);
            
        }
        return csvFile;
    }
    
    private void output(List<Map<String, String>> exportData, Map<String, String> rowMapper,
        BufferedWriter csvFileOutputStream) throws IOException
    {
        Entry<String, String> propertyEntry = null;
        for (Iterator<Entry<String, String>> propertyIterator = rowMapper.entrySet().iterator(); propertyIterator.hasNext();)
        {
            propertyEntry = (Entry<String, String>) propertyIterator.next();
            csvFileOutputStream.write("\"" + propertyEntry.getValue() + "\"");
            if (propertyIterator.hasNext())
            {
                csvFileOutputStream.write(",");
            }
        }
        csvFileOutputStream.newLine();
        Map<String, String> row;
        for (Iterator<Map<String, String>> iterator = exportData.iterator(); iterator.hasNext();)
        {
            row = (Map<String, String>) iterator.next();
            for (Iterator<Entry<String, String>> propertyIterator = row.entrySet().iterator(); propertyIterator.hasNext();)
            {
                propertyEntry = (Entry<String, String>) propertyIterator.next();
                csvFileOutputStream.write("\"" + propertyEntry.getValue() + "\"");
                if (propertyIterator.hasNext())
                {
                    csvFileOutputStream.write(",");
                }
            }
            if (iterator.hasNext())
            {
                csvFileOutputStream.newLine();
            }
        }
        csvFileOutputStream.flush();
    }
}
