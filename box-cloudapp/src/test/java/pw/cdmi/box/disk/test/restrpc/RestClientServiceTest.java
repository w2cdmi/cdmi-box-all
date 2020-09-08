package pw.cdmi.box.disk.test.restrpc;

import java.util.HashMap;

import javax.annotation.Resource;

import org.junit.Test;

import pw.cdmi.box.disk.test.AbstractSpringTest;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

public class RestClientServiceTest extends AbstractSpringTest
{
    @Resource
    private RestClient ufmClientService;
    
    @Test
    public void test()
    {
        TextResponse response = ufmClientService.performGetText("/api/v2/regions", new HashMap<String, String>(0));
        System.out.println(response.getStatusCode());
    }
}
