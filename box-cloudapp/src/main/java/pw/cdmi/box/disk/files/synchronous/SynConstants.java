package pw.cdmi.box.disk.files.synchronous;

import pw.cdmi.box.disk.utils.PropertiesUtils;

public class SynConstants
{
    
    public static final String SYNC_METADATA_TEMP_FILE_PATH = PropertiesUtils.getProperty("synchronous.version.file.temp.path",
        "/opt/ramdisk/container_synchronou_files/");
    
}
