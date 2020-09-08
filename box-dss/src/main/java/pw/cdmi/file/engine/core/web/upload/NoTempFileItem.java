package pw.cdmi.file.engine.core.web.upload;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.disk.DiskFileItem;

public class NoTempFileItem extends DiskFileItem
{
    private static final long serialVersionUID = 7917588498460945468L;
    
    private transient InputStream inputStream;
    
    public NoTempFileItem(String fieldName, String contentType, boolean isFormField, String fileName,
        NoTempFileExtParam noTempFileExtParam)
    {
        super(fieldName, contentType, isFormField, fileName, noTempFileExtParam.getSizeThreshold(), noTempFileExtParam.getRepository());
    }
    
    @Override
    public InputStream getInputStream() throws IOException
    {
        return this.inputStream;
    }
    
    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }
}
