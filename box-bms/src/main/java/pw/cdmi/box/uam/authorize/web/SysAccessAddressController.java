package pw.cdmi.box.uam.authorize.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.AccessAddressService;
import pw.cdmi.common.domain.AccessAddressConfig;

@Controller
@RequestMapping(value = "/sys/sysconfig/access")
public class SysAccessAddressController extends AbstractCommonController
{
    
    @Autowired
    private AccessAddressService accessAddressService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model)
    {
        AccessAddressConfig accessConfig = accessAddressService.getAccessAddress();
        model.addAttribute("accessConfig", accessConfig);
        return "sys/sysConfigManage/accessConfig";
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> save(AccessAddressConfig accessAddressConfig, HttpServletRequest request,
        String token)
    {
        super.checkToken(token);
        if (!validator.validate(accessAddressConfig).isEmpty())
        {
            saveValidateLog(request, OperateType.ChangeAccessAddress);
            throw new InvalidParamterException();
        }
        String[] description = new String[]{accessAddressConfig.getUamOuterAddress(),
            accessAddressConfig.getUfmOuterAddress(), accessAddressConfig.getUfmInnerAddress()};
        String id = systemLogManager.saveFailLog(request,
            OperateType.ChangeAccessAddress,
            OperateDescription.ADMIN_SET_ACCESS,
            null,
            description);
        
        String uamOuterAddress = accessAddressConfig.getUamOuterAddress();
        String uamInnerAddress = accessAddressConfig.getUamInnerAddress();
        String ufmInnerAddress = accessAddressConfig.getUfmInnerAddress();
        String ufmOuterAddress = accessAddressConfig.getUfmOuterAddress();
        if (!"/".equals(uamOuterAddress.substring(uamOuterAddress.length() - 1, uamOuterAddress.length()))
            && !"\\".equals(uamOuterAddress.substring(uamOuterAddress.length() - 1, uamOuterAddress.length())))
        {
            accessAddressConfig.setUamOuterAddress(uamOuterAddress + "/");
        }
        if (!"/".equals(uamInnerAddress.substring(uamInnerAddress.length() - 1, uamInnerAddress.length()))
            && !"\\".equals(uamInnerAddress.substring(uamInnerAddress.length() - 1, uamInnerAddress.length())))
        {
            accessAddressConfig.setUamInnerAddress(uamInnerAddress + "/");
        }
        if (!"/".equals(ufmInnerAddress.substring(ufmInnerAddress.length() - 1, ufmInnerAddress.length()))
            && !"\\".equals(ufmInnerAddress.substring(ufmInnerAddress.length() - 1, ufmInnerAddress.length())))
        {
            accessAddressConfig.setUfmInnerAddress(ufmInnerAddress + "/");
        }
        if (!"/".equals(ufmOuterAddress.substring(ufmOuterAddress.length() - 1, ufmOuterAddress.length()))
            && !"\\".equals(ufmOuterAddress.substring(ufmOuterAddress.length() - 1, ufmOuterAddress.length())))
        {
            accessAddressConfig.setUfmOuterAddress(ufmOuterAddress + "/");
        }
        accessAddressService.saveAccessAddress(accessAddressConfig);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
