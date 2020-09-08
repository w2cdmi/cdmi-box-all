package com.huawei.sharedrive.uam.enterprise.domain;

import java.util.HashMap;
import java.util.Map;

public final class SecOperationConstants
{
    private static final Map<Long, String> OPERATE_TYPES = new HashMap<Long, String>(16);
    
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
    
    public static Map<Long, String> getOperateTypes()
    {
        return OPERATE_TYPES;
    }
    
    private SecOperationConstants()
    {
        
    }
}
