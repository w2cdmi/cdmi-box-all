package pw.cdmi.box.uam.enterprise.domain;

import java.util.HashMap;
import java.util.Map;

public class SecOperationConstants
{
    static final Map<Long, String> OPERATE_TYPES = new HashMap<Long, String>(10);
    
    public static final String BUNDLE_NAME = "messages";
    
    public static final String SIGN = "、";
    static
    {
        OPERATE_TYPES.put(2L, "space.operation.upload");
        OPERATE_TYPES.put(3L, "space.operation.download");
        OPERATE_TYPES.put(5L, "space.operation.create");
        OPERATE_TYPES.put(7L, "space.operation.copy");
        OPERATE_TYPES.put(13L, "space.operation.preview");
        OPERATE_TYPES.put(17L, "space.operation.share");
        OPERATE_TYPES.put(19L, "space.operation.link");
        OPERATE_TYPES.put(23L, "space.operation.browser");
        OPERATE_TYPES.put(29L, "space.operation.delete");
        
    }
    
}
