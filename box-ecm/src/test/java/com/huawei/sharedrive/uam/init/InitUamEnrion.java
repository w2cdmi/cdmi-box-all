package com.huawei.sharedrive.uam.init;

import org.junit.Test;

import com.huawei.sharedrive.uam.init.develop.LogbackConfigReplaceTools;
import com.huawei.sharedrive.uam.init.develop.PropertiesReplaceTools;
import com.huawei.sharedrive.uam.init.develop.ShiroConfigReplaceTools;
import com.huawei.sharedrive.uam.init.develop.SpringConfigReplaceTools;

public class InitUamEnrion
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
