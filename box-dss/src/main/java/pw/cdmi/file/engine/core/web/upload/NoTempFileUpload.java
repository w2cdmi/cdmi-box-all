package pw.cdmi.file.engine.core.web.upload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemHeadersSupport;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重写FileUpload，实现POST上传文件的时候，不产生临时文件，但是需要注意的是，该类不支持POST批量文件上传
 * 
 * @author s90006125
 * 
 */
public class NoTempFileUpload extends FileUpload
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NoTempFileUpload.class);
    
    public NoTempFileUpload()
    {
        super();
    }
    
    public NoTempFileUpload(FileItemFactory fileItemFactory)
    {
        super(fileItemFactory);
    }
    
    public List<FileItem> parseRequest(HttpServletRequest request) throws FileUploadException
    {
        return parseRequest(new ServletRequestContext(request));
    }
    
    @Override
    public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException
    {
        List<FileItem> items = new ArrayList<FileItem>(1);
        boolean successful = false;
        try
        {
            FileItemIterator iter = getItemIterator(ctx);
            NoTempFileItemFactory fac = (NoTempFileItemFactory) getFileItemFactory();
            
            FileItemStream item = null;
            String fileName = null;
            FileItem fileItem = null;
            
            try
            {
                FileItemHeaders fih = null;
                
                boolean hasNext = iter.hasNext();
                while(hasNext)
                {
                    item = iter.next();
                    // Don't use getName() here to prevent an InvalidFileNameException.
                    fileName = ((FileItemStream) item).getName();
                    fileItem = fac.createItem(item.getFieldName(),
                        item.getContentType(),
                        item.isFormField(),
                        fileName);
                    items.add(fileItem);
                    
                    if (fileItem.isFormField())
                    {
                        Streams.copy(item.openStream(), fileItem.getOutputStream(), true);
                    }
                    else
                    {
                        ((NoTempFileItem) fileItem).setInputStream(item.openStream());
                    }
                    
                    fih = item.getHeaders();
                    ((FileItemHeadersSupport) fileItem).setHeaders(fih);
                    // 不支持批量上传文件
                    if (!fileItem.isFormField())
                    {
                        break;
                    }
                    
                    hasNext = iter.hasNext();
                }
            }
            catch (FileUploadIOException e)
            {
                throw (FileUploadException) e.getCause();
            }
            catch (IOException e)
            {
                throw new IOFileUploadException("Processing of " + MULTIPART_FORM_DATA
                    + " request failed. " + e.getMessage(), e);
            }
            
            successful = true;
            return items;
        }
        catch (FileUploadIOException e)
        {
            throw (FileUploadException) e.getCause();
        }
        catch (IOException e)
        {
            throw new FileUploadException(e.getMessage(), e);
        }
        finally
        {
            if (!successful)
            {
                FileItem fileItem = null;
                Iterator<FileItem> iterator = items.iterator();
                boolean hasNext = iterator.hasNext();
                while(hasNext)
                {
                    fileItem = (FileItem) iterator.next();
                    deleteFileItemWithoutException(fileItem);
                    hasNext = iterator.hasNext();
                }
            }
        }
    }

    private void deleteFileItemWithoutException(FileItem fileItem)
    {
        try
        {
            fileItem.delete();
        }
        catch (Exception e)
        {
            LOGGER.error("Delete Temp FileItem Failed", e);
        }
    }
    
}
