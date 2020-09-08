package pw.cdmi.box.disk.test.service;

import javax.annotation.Resource;

import org.junit.Test;

import pw.cdmi.box.disk.httpclient.exception.ClientException;
import pw.cdmi.box.disk.test.AbstractSpringTest;

public class FolderAPITest extends AbstractSpringTest
{
    
    @Resource
    private UserLoginServiceTest userLoginService;
    
    @Test
    public void listFolder() throws ClientException
    {
        if (response == null)
        {
            userLoginService.testLoginPlat();
        }
        
        // TODO
    }
}
