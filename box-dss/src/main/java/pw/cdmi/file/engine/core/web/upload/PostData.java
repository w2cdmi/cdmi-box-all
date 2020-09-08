package pw.cdmi.file.engine.core.web.upload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

/**
 * 封装post请求的参数
 * 
 * @author s90006125
 * 
 */
public class PostData
{
    private Map<String, String> parameters = new HashMap<String, String>(1);
    
    private FileItem fileData;
    
    public PostData(List<FileItem> items)
    {
        for (FileItem item : items)
        {
            if (null == item)
            {
                continue;
            }
            
            if (item.isFormField())
            {
                parameters.put(item.getFieldName(), item.getString());
            }
            else
            {
                fileData = item;
            }
        }
    }
    
    public FileItem getFile()
    {
        return this.fileData;
    }
    
    public String getParameter(String key)
    {
        return parameters.get(key);
    }
}
