package pw.cdmi.box.disk.files.web;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.core.exception.ErrorCode;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.box.disk.client.api.NodeClient;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;

@Controller
@RequestMapping(value = "/path")
public class PathController extends CommonController
{
    
    private NodeClient nodeClient;
    
    @Autowired
    private ResourceBundleMessageSource resourceBundle;
    
    @Resource
    private RestClient ufmClientService;
    
    @RequestMapping(value = "/{ownerId}/{nodeId}", method = RequestMethod.GET)
    public ResponseEntity<String> enter(@PathVariable long ownerId, @PathVariable long nodeId)
    {
        nodeClient = new NodeClient(ufmClientService);
        try
        {
            List<RestFolderInfo> parentList = nodeClient.getNodePath(getToken(), ownerId, nodeId);
            String path = generalNodePath(parentList);
            return new ResponseEntity<String>(path, HttpStatus.OK);
        }
        catch (RestException e)
        {
            if (ErrorCode.NO_SUCH_PARENT.getCode().equals(e.getCode()))
            {
                String msg = resourceBundle.getMessage("file.info.parentNotExist", null, Locale.getDefault());
                return new ResponseEntity<String>(HtmlUtils.htmlEscape(msg), HttpStatus.OK);
            }
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private String generalNodePath(List<RestFolderInfo> parentList)
    {
        if (CollectionUtils.isEmpty(parentList))
        {
            return TrashController.PATH_SEPARATER;
        }
        Collections.reverse(parentList);
        StringBuffer buffer = new StringBuffer();
        for (RestFolderInfo folderInfo : parentList)
        {
            buffer.append(TrashController.PATH_SEPARATER).append(folderInfo.getName());
        }
        buffer.append(TrashController.PATH_SEPARATER);
        return buffer.toString();
    }
}
