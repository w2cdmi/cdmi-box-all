package pw.cdmi.box.disk.utils;

import java.io.File;

public final class PreviewUtils
{
    private PreviewUtils()
    {
        
    }
    
    private static final String RELATIVE_PATH = "/static/preview/FlexPaperViewer.swf";
    
    private static boolean canBePreview = false;
    
    private static boolean isInited = false;
    
    private static String path = null;
    
    public static boolean isPreview()
    {
        if (isInited)
        {
            return canBePreview;
        }
        if (null == path)
        {
            File currentFolder = new File(PreviewUtils.class.getResource("").getFile());
            String contentPath = currentFolder.getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getPath();
            path = contentPath + RELATIVE_PATH;
        }
        File swf = new File(path);
        if (swf.exists())
        {
            canBePreview = true;
            isInited = true;
        }
        return canBePreview;
    }
    
}
