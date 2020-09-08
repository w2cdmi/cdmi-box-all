package pw.cdmi.box.disk.openapi.rest.v2;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.oauth2.client.UserTokenHelper;
import pw.cdmi.box.disk.openapi.rest.v2.domain.ClientObject;
import pw.cdmi.box.disk.openapi.rest.v2.domain.ClientValiCode;
import pw.cdmi.box.disk.openapi.rest.v2.domain.GetClientInfoRequest;
import pw.cdmi.box.disk.openapi.rest.v2.domain.GetClientInfoResponse;
import pw.cdmi.box.disk.system.service.ClientManageService;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.common.domain.ClientManage.ClientType;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.ErrorCode;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.exception.NoSuchClientException;

@Controller
@RequestMapping(value = "/api/v2/client")
public class ClientManageAPIController
{
    private static final int MAX_DOWNLOAD_THREADS = Integer.parseInt(PropertiesUtils.getProperty("client.download.max.threads",
        "100"));
    
    private static final int TIME_OUT = Integer.parseInt(PropertiesUtils.getProperty("client.download.timeout.senconds",
        "5"));
    
    private static final int LENGTH_SIZE = 255;
    
    private static final Semaphore SEMAPHORE = new Semaphore(MAX_DOWNLOAD_THREADS);
    
    @Autowired
    private ClientManageService clientManageService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downLoadClient(@RequestParam(value = "type", required = true) String type, OutputStream os,
        HttpServletRequest request, HttpServletResponse response)
    {
        InputStream is = null;
        try
        {
            if (!SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))
            {
                throw new InternalServerErrorException();
            }
            
            ClientObject client = clientManageService.getClientObject(type);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream; charset=utf-8");
            String agent = request.getHeader("User-Agent");
            
            response.setHeader("Connection", "close");
            String attachment = "attachment; filename=\"" + getAttachName(client.getName(), agent) + "\"";
            response.setHeader("Content-Disposition", attachment);
            response.setHeader("Content-Length", String.valueOf(client.getSize()));
            is = client.getContent();
            byte[] b = new byte[1024 * 64];
            int length;
            while ((length = is.read(b)) != -1)
            {
                os.write(b, 0, length);
            }
            
        }
        catch (RuntimeException e)
        {
            throw new InternalServerErrorException(e);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
        finally
        {
            SEMAPHORE.release();
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }
    
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GetClientInfoResponse> getClientInfo(@RequestBody GetClientInfoRequest request,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        request.checkParameter();
        
        userTokenHelper.imperfectCheckTokenAndGetUserForV2(token);
        
        ClientManage clientManage = clientManageService.getVersionInfoByType(request.getClientType());
        
        if (clientManage == null)
        {
            
            throw new NoSuchClientException("Client is not exsit, client type: " + request.getClientType());
        }
        
        GetClientInfoResponse clientInfo = new GetClientInfoResponse();
        clientInfo.setDownloadUrl(clientManage.getDownloadUrl());
        try
        {
            String versionInfo = clientManage.getVersionInfo() == null ? "" : new String(
                clientManage.getVersionInfo(), "UTF-8");
            clientInfo.setVersionInfo(versionInfo);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new InternalServerErrorException("Get client version info failed", e);
        }
        return new ResponseEntity<GetClientInfoResponse>(clientInfo, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/featurecode/{clientType}/{versionInfo:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ClientValiCode> getCheckCodeByVersionInfo(
        @RequestHeader("Authorization") String token,
        @RequestHeader(value = "Date", required = false) String date,
        @PathVariable(value = "clientType") String clientType,
        @PathVariable(value = "versionInfo") String versionInfo, HttpServletRequest request)
        throws BaseRunException
    {
        
        userTokenHelper.checkTokenAndGetUserForV2(token);
        ClientManage client = new ClientManage();
        client.setType(clientType);
        client.setVersion(versionInfo);
        if (StringUtils.isBlank(client.getVersion()) || StringUtils.isBlank(client.getType())
            || client.getVersion().length() > LENGTH_SIZE)
        {
            throw new InvalidParamException(ErrorCode.INVALID_PARAMETER.getMessage());
        }
        ClientType[] clients = ClientManage.ClientType.values();
        boolean flag = true;
        for (ClientType type : clients)
        {
            if (client.getType().equalsIgnoreCase(type.toString()))
            {
                flag = false;
            }
        }
        if (flag)
        {
            throw new InvalidParamException(ErrorCode.INVALID_PARAMETER.getMessage());
        }
        ClientManage clientManage = clientManageService.getClientManage(client);
        ClientValiCode result = new ClientValiCode();
        result.setFeaturecode(clientManage.getCheckCode());
        result.setArithmetic("SHA-256");
        return new ResponseEntity<ClientValiCode>(result, HttpStatus.OK);
    }
    
    private String getAttachName(String fileName, String agent) throws UnsupportedEncodingException
    {
        String attachName = URLEncoder.encode(fileName, "UTF-8");
        String lowerAgent = StringUtils.lowerCase(agent);
        if (StringUtils.isBlank(lowerAgent))
        {
            return attachName;
        }
        if (lowerAgent.contains("firefox"))
        {
            attachName = new String(fileName.getBytes("utf-8"), "iso-8859-1");
        }
        else if (lowerAgent.contains("chrome"))
        {
            attachName = specialCharDecodeChrome(URLEncoder.encode(fileName, "UTF-8"));
        }
        else
        {
            attachName = specialCharDecode(URLEncoder.encode(fileName, "UTF-8"));
        }
        return attachName;
    }
    
    private String specialCharDecode(String str)
    {
        String result = str.replace("+", "%20")
            .replace("%21", "!")
            .replace("%24", "$")
            .replace("%20", " ")
            .replace("%25", "%")
            .replace("%28", "(")
            .replace("%29", ")")
            .replace("%2B", "+")
            .replace("%2C", ",")
            .replace("%3A", ":")
            .replace("%3D", "=")
            .replace("%40", "@")
            .replace("%5B", "[")
            .replace("%5D", "]")
            .replace("%5E", "^")
            .replace("%60", "`")
            .replace("%7B", "{")
            .replace("%7D", "}")
            .replace("%27", "'")
            .replace("%26", "&");
        return result;
    }
    
    private String specialCharDecodeChrome(String str)
    {
        String result = str.replace("+", "%20")
            .replace("%21", "!")
            .replace("%23", "#")
            .replace("%24", "$")
            .replace("%25", "%")
            .replace("%28", "(")
            .replace("%29", ")")
            .replace("%2B", "+")
            .replace("%2C", ",")
            .replace("%3A", ":")
            .replace("%3D", "=")
            .replace("%40", "@")
            .replace("%5B", "[")
            .replace("%5D", "]")
            .replace("%5E", "^")
            .replace("%60", "`")
            .replace("%7B", "{")
            .replace("%7D", "}")
            .replace("%27", "'")
            .replace("%26", "&");
        return result;
    }
    
}
