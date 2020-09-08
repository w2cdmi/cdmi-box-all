package com.huawei.sharedrive.isystem.init;

import org.junit.Test;

import com.huawei.sharedrive.isystem.init.develop.LogbackConfigReplaceTools;
import com.huawei.sharedrive.isystem.init.develop.PropertiesReplaceTools;
import com.huawei.sharedrive.isystem.init.develop.ShiroConfigReplaceTools;
import com.huawei.sharedrive.isystem.init.develop.SpringConfigReplaceTools;

public class InitDevEnrion
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
