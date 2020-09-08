package com.huawei.sharedrive.bms.init;

import org.junit.Test;

import com.huawei.sharedrive.bms.init.develop.LogbackConfigReplaceTools;
import com.huawei.sharedrive.bms.init.develop.PropertiesReplaceTools;
import com.huawei.sharedrive.bms.init.develop.ShiroConfigReplaceTools;
import com.huawei.sharedrive.bms.init.develop.SpringConfigReplaceTools;

public class InitBmsEnrion
{
    
    
    
    @Test
    public void initLocal() throws Exception
    {
        PropertiesReplaceTools propReplacer = new PropertiesReplaceTools();
        propReplacer.testReplace();
        
        SpringConfigReplaceTools springReplacer = new SpringConfigReplaceTools();
        springReplacer.testAppCxtReplace();
        
        ShiroConfigReplaceTools shireReplacer = new ShiroConfigReplaceTools();
        shireReplacer.testAppCxtReplace();
        
        LogbackConfigReplaceTools logTools = new LogbackConfigReplaceTools();
        logTools.testAppCxtReplace();
    }
    
}
