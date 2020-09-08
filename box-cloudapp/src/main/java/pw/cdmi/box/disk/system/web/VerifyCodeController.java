package pw.cdmi.box.disk.system.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.disk.system.service.VerifyCodeHelper;
import pw.cdmi.core.exception.InternalServerErrorException;

@Controller
@RequestMapping(value = "/system/verifycode")
public class VerifyCodeController
{
    @Autowired
    private VerifyCodeHelper verifyCodeHelper;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(VerifyCodeController.class);
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public void get(String uuid, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            verifyCodeHelper.processRequest(request, response, uuid);
        }
        catch (IOException e)
        {
            LOGGER.error("failed in processRequest", e);
            throw new InternalServerErrorException(e);
        }
    }
    
}
