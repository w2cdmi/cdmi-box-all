package pw.cdmi.file.engine.core.web.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

public class NoTempFileItemFactory extends DiskFileItemFactory
{

    @Override
    public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName)
    {
        if (isFormField)
        {
            return super.createItem(fieldName, contentType, isFormField, fileName);
        }
        
        NoTempFileItem result = new NoTempFileItem(fieldName, contentType, isFormField, fileName,
            new NoTempFileExtParam(getSizeThreshold(), getRepository()));
        
        return result;
    }
}
