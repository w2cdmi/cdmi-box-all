package com.huawei.sharedrive.init;

import org.junit.Test;

import com.huawei.sharedrive.init.develop.LogbackConfigReplaceTools;
import com.huawei.sharedrive.init.develop.PropertiesReplaceTools;
import com.huawei.sharedrive.init.develop.SpringConfigReplaceTools;

public class InitUfmDevEnviron
{
    @Test
    public void initLocal() throws Exception
    {
        PropertiesReplaceTools propReplacer = new PropertiesReplaceTools();
        propReplacer.testReplace();
        
        SpringConfigReplaceTools springReplacer = new SpringConfigReplaceTools();
        springReplacer.testAppCxtReplace();
        
        LogbackConfigReplaceTools logTools = new LogbackConfigReplaceTools();
        logTools.testAppCxtReplace();
    }
}
