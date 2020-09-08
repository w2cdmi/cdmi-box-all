package pw.cdmi.box.disk.system.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy(false)
public class InitServiceImpl
{
    @Autowired
    private AccessAddressServiceImpl accessAddressServiceImpl;
    
    @PostConstruct
    public void init()
    {
        accessAddressServiceImpl.loadAccessAddressFromDb();
    }
    
}
