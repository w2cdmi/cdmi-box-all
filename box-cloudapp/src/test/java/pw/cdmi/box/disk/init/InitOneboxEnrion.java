package pw.cdmi.box.disk.init;

import org.junit.Test;

import pw.cdmi.box.disk.init.develop.LogbackConfigReplaceTools;
import pw.cdmi.box.disk.init.develop.PropertiesReplaceTools;
import pw.cdmi.box.disk.init.develop.ShiroConfigReplaceTools;
import pw.cdmi.box.disk.init.develop.SpringConfigReplaceTools;

public class InitOneboxEnrion
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
