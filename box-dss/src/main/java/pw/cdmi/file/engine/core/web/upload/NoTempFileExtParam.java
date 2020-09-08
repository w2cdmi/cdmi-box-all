package pw.cdmi.file.engine.core.web.upload;

import java.io.File;

public class NoTempFileExtParam
{
    private int sizeThreshold;
    
    private File repository;
    
    public NoTempFileExtParam( int sizeThreshold, File repository)
    {
        this.sizeThreshold = sizeThreshold;
        this.repository = repository;
    }
    
    public int getSizeThreshold()
    {
        return this.sizeThreshold;
    }

    public void setSizeThreshold(int sizeThreshold)
    {
        this.sizeThreshold = sizeThreshold;
    }

    public File getRepository()
    {
        return repository;
    }

    public void setRepository(File repository)
    {
        this.repository = repository;
    }
}
