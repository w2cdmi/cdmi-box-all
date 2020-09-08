package pw.cdmi.box.disk.filelabel.web;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.FilelabelClient;
import pw.cdmi.box.disk.client.domain.node.RestFileLabelRequest;
import pw.cdmi.box.disk.doctype.domain.RestDocTypeList;
import pw.cdmi.box.disk.filelabel.dto.BaseFilelabelResponseDto;
import pw.cdmi.box.disk.filelabel.dto.EntityFilelabelResponseDto;
import pw.cdmi.box.disk.filelabel.dto.FileLabelRequestDto;
import pw.cdmi.box.disk.filelabel.dto.ListFilelabelResponseDto;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.core.restrpc.RestClient;
/**
 * 
 * Desc  : 文件标签接口
 * Author: 77235
 * Date	 : 2016年12月9日
 */
@Controller
@RequestMapping(value = "/filelabel")
public class FileLabelController extends CommonController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileLabelController.class);

    private static final String CONST_LACK_FILENAME_TIP = "fl.lack.labelname";
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    @Qualifier("messageSource")
    private ResourceBundleMessageSource messageSource;
    
    private FilelabelClient fileLabelClient;
    
    @PostConstruct
    void init() {
        this.fileLabelClient = new FilelabelClient(ufmClientService);
    }
    /**
     * 展示搜索标签界面
     * 
     * @return
     */
    @RequestMapping(value = "/showHighSearchView", method = RequestMethod.GET)
    public String showHighSearchView(long ownerId, String searchModel,Model model) {
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("searchModel", searchModel);
        
        return "filelabel/highSearchView";
    }
    
    /**
     * 打开绑定文件标签界面
     * 
     * @return
     */
    @RequestMapping(value = "/showBindFilelabelView", method = RequestMethod.GET)
    public String showBindFilelabelView(long ownerId, long nodeId, Long labelId, int bindType, Model model) {
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("nodeId", nodeId);
        model.addAttribute("labelId", labelId);
        model.addAttribute("bindType", bindType);
        
        return "filelabel/bindFilelabelView";
    }
    
    /**
     * 绑定文件标签
     * 
     * @return
     */
    @RequestMapping(value = "/bindFilelabel", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<EntityFilelabelResponseDto> bindFilelabel(FileLabelRequestDto fileLabelRequest,
        HttpServletRequest httpRequest) {
        
        try {
            super.checkToken(httpRequest);
            if (StringUtils.isEmpty(fileLabelRequest.getLabelName())) {
                throw new RuntimeException(messageSource.getMessage(CONST_LACK_FILENAME_TIP, null, httpRequest.getLocale()));
            }
          
            fileLabelRequest.setLabelName(HtmlUtils.htmlEscape(fileLabelRequest.getLabelName()));
            EntityFilelabelResponseDto respEntity = fileLabelClient.bindFilelabel(getToken(),
                fileLabelRequest, fileLabelRequest.getOwnerId());
            if (StringUtils.isNotEmpty(respEntity.getErrorCode())) {
                respEntity.setErrMsg(
                    messageSource.getMessage(respEntity.getErrorCode(), null, httpRequest.getLocale()));
            }
            
            return new ResponseEntity<EntityFilelabelResponseDto>(respEntity, HttpStatus.OK);
        } catch (Exception e) {
        	LOGGER.error("[FileLabelController] bindFilelabel error:" + e.getMessage(), e);
            EntityFilelabelResponseDto respEntity = new EntityFilelabelResponseDto();
            return new ResponseEntity<EntityFilelabelResponseDto>(respEntity, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 解除文件标签绑定
     * @return
     */
    @RequestMapping(value = "/unbindFilelabel", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<BaseFilelabelResponseDto> unbindFilelabel(RestFileLabelRequest fileLabelRequest,
        HttpServletRequest httpRequest) {
        
        try {
            super.checkToken(httpRequest);
            
            BaseFilelabelResponseDto retDto = fileLabelClient.unbindFilelabel(getToken(), fileLabelRequest,
                fileLabelRequest.getOwnerId());
            if (StringUtils.isNotEmpty(retDto.getErrorCode())) {
                retDto.setErrMsg(messageSource.getMessage(retDto.getErrorCode(), null, httpRequest.getLocale()));
            }
         
            return new ResponseEntity<BaseFilelabelResponseDto>(retDto, retDto.getStatus());
        } catch (Exception e) {
        	LOGGER.error("[FileLabelController] unbindFilelabel error:" + e.getMessage(), e);
            return new ResponseEntity<BaseFilelabelResponseDto>(HttpStatus.UNAUTHORIZED);
        }
    }
    
    
    @RequestMapping(value = "/listFilabel/{ownerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ListFilelabelResponseDto> listFilelabels(@PathVariable("ownerId") long ownerId,
        @RequestParam(defaultValue = "1") int labelType, @RequestParam(defaultValue = "1") int reqPage,
        @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest httpRequest,
        @RequestParam(defaultValue = "") String filelabelName) {
        try {
            super.checkToken(httpRequest);
            
            ListFilelabelResponseDto retDto = fileLabelClient.queryEnterpriseFilelabels(ownerId, labelType, reqPage,
                pageSize, filelabelName);
            if (StringUtils.isNotEmpty(retDto.getErrorCode())) {
                retDto.setErrMsg(
                    messageSource.getMessage(retDto.getErrorCode(), null, httpRequest.getLocale()));
            }
            
            return new ResponseEntity<ListFilelabelResponseDto>(retDto, retDto.getStatus());
        } catch (Exception e) {
        	LOGGER.error("[FileLabelController] listFilelabels error:" + e.getMessage(), e);
            return new ResponseEntity<ListFilelabelResponseDto>(HttpStatus.UNAUTHORIZED);
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "/getDefaultDoctype/{ownerId}", method = RequestMethod.GET)
    public ResponseEntity<RestDocTypeList> findDoctypeByOwner(@PathVariable("ownerId") long ownerId,
        HttpServletRequest request) {
        try {
            super.checkToken(request);
            RestDocTypeList defaultDocTypes = fileLabelClient.getDefaultDoctype(getCurrentUser(), ownerId, ownerId);
           
            return new ResponseEntity<RestDocTypeList>(defaultDocTypes, HttpStatus.OK);
        } catch (Exception e) {
        	LOGGER.error("[FileLabelController] findDoctypeByOwner error:" + e.getMessage(), e);
            return new ResponseEntity<RestDocTypeList>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "/listLatestLabels/{ownerId}", method = RequestMethod.GET)
    public ResponseEntity<ListFilelabelResponseDto> listUserLatestViewedLabels(@PathVariable("ownerId") long ownerId,
        HttpServletRequest request) {
        try {
            super.checkToken(request);
            ListFilelabelResponseDto latestLabels = fileLabelClient.queryUserLatestViewedLabels(ownerId);
           
            return new ResponseEntity<ListFilelabelResponseDto>(latestLabels, HttpStatus.OK);
        } catch (Exception e) {
        	LOGGER.error("[FileLabelController] listUserLatestViewedLabels error:" + e.getMessage(), e);
            return new ResponseEntity<ListFilelabelResponseDto>(HttpStatus.BAD_REQUEST);
        }
    }
}
