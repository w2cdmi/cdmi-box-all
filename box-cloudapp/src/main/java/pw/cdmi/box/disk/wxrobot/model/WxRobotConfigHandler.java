package pw.cdmi.box.disk.wxrobot.model;

import java.util.HashMap;
import java.util.Map;

public class WxRobotConfigHandler {
    
    public static Map<String, Boolean> getConfig(long value)
    {
    	Boolean file =  (int) (value % 10)==1? true : false;
    	Boolean image = (int) (value % 100)/10==1? true : false;
    	Boolean video = (int) (value % 1000)/100==1? true : false;
        Map<String, Boolean> config=new HashMap<>();
        config.put("file", file);
        config.put("image", image);
        config.put("video", video);
        return config;
    }
}
