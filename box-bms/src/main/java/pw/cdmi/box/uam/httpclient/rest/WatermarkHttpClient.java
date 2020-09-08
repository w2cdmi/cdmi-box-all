package pw.cdmi.box.uam.httpclient.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.uam.exception.InternalServerErrorException;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.StreamResponse;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.uam.domain.AuthApp;

public class WatermarkHttpClient
{
    
    private static Logger logger = LoggerFactory.getLogger(WatermarkHttpClient.class);
    
    private static final String URL = "api/v2/accounts";
    
    private static final String LAST_WORD = "/watermark";
    
    private RestClient ufmClientService;
    
    public WatermarkHttpClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public void putWaterMark(EnterpriseAccount enterpriseAccount, InputStream image, long imagelength,
        AuthApp app) throws InternalServerErrorException
    {
        Map<String, String> headers = getAppAtuhHeader("application",
            app.getUfmAccessKeyId(),
            app.getUfmSecretKey());
        String apiPath = URL + "/" + enterpriseAccount.getAccountId() + LAST_WORD;
        TextResponse response = ufmClientService.performStreamPutTextResponse(apiPath,
            headers,
            image,
            imagelength);
        if (response.getStatusCode() != 200)
        {
            throw new InternalServerErrorException(response.getContentType());
        }
        
    }
    
    public byte[] getWaterMark(EnterpriseAccount enterpriseAccount, AuthApp app)
        throws InternalServerErrorException
    {
        
        Map<String, String> headers = getAppAtuhHeader("application",
            app.getUfmAccessKeyId(),
            app.getUfmSecretKey());
        String apiPath = URL + "/" + enterpriseAccount.getAccountId() + LAST_WORD;
        StreamResponse response = ufmClientService.performGetStream(apiPath, headers);
        if (response.getStatusCode() != 200)
        {
            throw new InternalServerErrorException();
        }
        byte[] in2b = null;
        InputStream in = null;
        ByteArrayOutputStream swapStream = null;
        try
        {
            in = response.getInputStream();
            if (in == null)
            {
                return null;
            }
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc;
            while ((rc = in.read(buff, 0, 100)) > 0)
            {
                swapStream.write(buff, 0, rc);
            }
            in2b = swapStream.toByteArray();
        }
        catch (IOException e)
        {
            logger.info("IOEXCEPTION", e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(swapStream);
        }
        return in2b;
    }
    
    private Map<String, String> getAppAtuhHeader(String authType, String accessKeyId, String appSecretKey)
    {
        Map<String, String> headers = new HashMap<String, String>(16);
        Date now = new Date();
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, now, null);
        String appSignatureKey = SignatureUtils.getSignature(StringUtils.trimToEmpty(appSecretKey), dateStr);
        String authorization = authType + "," + accessKeyId + ',' + appSignatureKey;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);
        headers.put("Content-Type", "image/png");
        return headers;
    }
    
}
