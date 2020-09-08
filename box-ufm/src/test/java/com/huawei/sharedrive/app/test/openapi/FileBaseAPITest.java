package com.huawei.sharedrive.app.test.openapi;

import com.huawei.sharedrive.app.openapi.domain.node.*;
import com.huawei.sharedrive.app.openapi.domain.share.RestLinkCreateRequestV2;
import com.huawei.sharedrive.app.openapi.domain.share.RestShareRequestV2;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;
import org.apache.commons.lang.StringUtils;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.RandomGUID;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class FileBaseAPITest
{
    protected static final String METHOD_DELETE = "DELETE";
    
    protected static final String METHOD_GET = "GET";
    
    protected static final String METHOD_POST = "POST";
    
    protected static final String METHOD_PUT = "PUT";
    
    protected static final String INIT_DATA = "testData/file/initData.txt";
    
    protected static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";
    
    protected static final String SHARE_ADD = "testData/share/initData.txt";
    
    protected Long fileParentId;
    
    protected long userId1;
    
    protected long userId2;
    
    protected boolean showResult = true;
    
    protected String url;
    
    public FileBaseAPITest()
    {
        try
        {
            userId1 = MyTestUtils.getTestUser1().getCloudUserId();
            userId2 = MyTestUtils.getTestUser2().getCloudUserId();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Get owner id failed!", e);
        }
    }
    
    protected RestFileInfo getFileInfo(Long ownerId, Long nodeId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + nodeId;
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_GET);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestFileInfo fileInfo = JSonUtils.stringToObject(result, RestFileInfo.class);
            return fileInfo;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Get file info failed", e);
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected INodeLink createLink(Long ownerId, Long nodeId)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId + "/" + nodeId;
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            INodeLink response = JSonUtils.stringToObject(result, INodeLink.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create file link failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected INodeLink createObjectLink(Long ownerId, Long nodeId)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId + "/" + nodeId;
        InputStream stream = null;
        BufferedReader in = null;
        
        RestLinkCreateRequestV2 request = new RestLinkCreateRequestV2();
        request.setPlainAccessCode("tongxuan123");
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST, JsonUtils.toJson(request));
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            INodeLink response = JSonUtils.stringToObject(result, INodeLink.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create file link failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected void cleanNode(Long ownerId, Long nodeId)
    {
        try
        {
            String url = MyTestUtils.SERVER_URL_UFM_V2 + "trash/" + ownerId + "/" + nodeId;
            deleteNode(userId1, nodeId);
            HttpURLConnection connection = getConnection(url, METHOD_DELETE);
            MyResponseUtils.assert200(connection, showResult);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Clean node failed");
        }
    }
    
    protected INodeLink createLinkByExpireTime(Long ownerId, Long nodeId, Long expireAt)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId + "/" + nodeId;
        InputStream stream = null;
        BufferedReader in = null;
        
        RestLinkCreateRequestV2 request = new RestLinkCreateRequestV2();
        request.setAccessCodeMode("static");
        request.setPlainAccessCode("qww4433d@");
        request.setEffectiveAt(1391845193374L);
        if (expireAt == null)
        {
            Date now = new Date();
            Date tomorrow = DateUtils.getDateAfter(now, 1);
            expireAt = tomorrow.getTime();
        }
        request.setExpireAt(expireAt);
        
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST, JsonUtils.toJson(request));
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            INodeLink response = JSonUtils.stringToObject(result, INodeLink.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create file link failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected INodeLink createLinkByPlianAccessCode(Long ownerId, Long nodeId, String plainAccessCode)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId + "/" + nodeId;
        InputStream stream = null;
        BufferedReader in = null;
        
        RestLinkCreateRequestV2 request = new RestLinkCreateRequestV2();
        if (plainAccessCode != null)
        {
            request.setPlainAccessCode(plainAccessCode);
        }
        
        request.setEffectiveAt(1391845193374L);
        request.setExpireAt(1421845193374L);
        
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST, JsonUtils.toJson(request));
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            INodeLink response = JSonUtils.stringToObject(result, INodeLink.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create file link failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected RestFolderInfo createFolder(Long ownerId, String name, Long parent,Long contentCreatedAt,Long contentModifiedAt)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId;
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName(name);
        request.setParent(parent);
        request.setContentCreatedAt(contentCreatedAt);
        request.setContentModifiedAt(contentModifiedAt);
        
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            String body = JSonUtils.toJson(request);
            HttpURLConnection connection = getConnection(url, METHOD_POST, body);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestFolderInfo response = JSonUtils.stringToObject(result, RestFolderInfo.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create folder Failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected RestFolderInfo createFolder(Long ownerId, String name, Long parent)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId;
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName(name);
        request.setParent(parent);
        
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            String body = JSonUtils.toJson(request);
            HttpURLConnection connection = getConnection(url, METHOD_POST, body);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestFolderInfo response = JSonUtils.stringToObject(result, RestFolderInfo.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create folder Failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected RestFolderInfo createFolder(Long ownerId, String name, Long parent, String token)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId;
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName(name);
        request.setParent(parent);
        
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            String body = JSonUtils.toJson(request);
            HttpURLConnection connection = getConnection(url, METHOD_POST, token, body);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestFolderInfo response = JSonUtils.stringToObject(result, RestFolderInfo.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create folder Failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected RestFolderInfo createRandomFolder()
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1;
        Long parent = Long.parseLong(MyFileUtils.getDataFromFile(INIT_DATA, "folderParent1"));
        
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName(new RandomGUID().getValueAfterMD5());
        request.setParent(parent);
        
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            String body = JSonUtils.toJson(request);
            HttpURLConnection connection = getConnection(url, METHOD_POST, body);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestFolderInfo response = JSonUtils.stringToObject(result, RestFolderInfo.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create folder failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected void deleteNode(Long ownerId, Long nodeId)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "nodes/" + ownerId + "/" + nodeId;
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_DELETE);
            MyResponseUtils.assert200(connection, showResult);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Delete node failed, node id:" + nodeId, e);
        }
    }
    
    protected HttpURLConnection getConnection(String requestUrl, String method) throws Exception
    {
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
    
    protected HttpURLConnection getConnectionForUser2(String requestUrl, String method, String body)
        throws Exception
    {
        if (StringUtils.isBlank(body))
        {
            return getConnection(requestUrl, method);
        }
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Authorization", MyTestUtils.getTestUserToken2());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        
        System.out.println("request body: " + body);
        connection.getOutputStream().write(body.getBytes());
        return connection;
    }
    
    protected HttpURLConnection getConnection(String requestUrl, String method, String body) throws Exception
    {
        if (StringUtils.isBlank(body))
        {
            return getConnection(requestUrl, method);
        }
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        
        System.out.println("request body: " + body);
        connection.getOutputStream().write(body.getBytes());
        return connection;
    }
    
    protected HttpURLConnection getConnection(String requestUrl, String method, String authorization,
        String body) throws Exception
    {
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", authorization);
        connection.setDoInput(true);
        
        if (StringUtils.isNotBlank(body))
        {
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }
        else
        {
            connection.setDoOutput(false);
            
        }
        return connection;
    }
    
    protected HttpURLConnection getConnection(String requestUrl, String method, String authorization,String accessAuthorization,
        String date, String body) throws Exception
    {
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Date", date);
        connection.setRequestProperty("Authorization", authorization);
        if(accessAuthorization != null)
        {
            connection.setRequestProperty("x-usertoken", accessAuthorization);
        }
        connection.setDoInput(true);
        
        if (StringUtils.isNotBlank(body))
        {
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }
        else
        {
            connection.setDoOutput(false);
            
        }
        return connection;
    }
    
    protected HttpURLConnection getConnection(String requestUrl, String method, String authorization,
        String date, String body) throws Exception
    {
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        System.out.println(connection.getRequestMethod());
        connection.setRequestProperty("Date", date);
        connection.setRequestProperty("Authorization", authorization);
        connection.setDoInput(true);
        
        if (StringUtils.isNotBlank(body))
        {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }
        else
        {
            connection.setDoOutput(false);
            
        }
        return connection;
    }
    
    protected HttpURLConnection getConnectionPreiew(String requestUrl, String method, String authorization,
    		String accountId,String convertRealStartTime, String body) throws Exception
    		{
    	URL url = new URL(requestUrl);
    	System.out.println("Request url : " + requestUrl);
    	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	connection.setRequestMethod(method);
    	System.out.println(connection.getRequestMethod());
    	connection.setRequestProperty("Authorization", authorization);
    	connection.setRequestProperty("Date", convertRealStartTime);
    	connection.setDoInput(true);
    	
    	if (StringUtils.isNotBlank(body))
    	{
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		connection.getOutputStream().write(body.getBytes());
    	}
    	else
    	{
    		connection.setDoOutput(false);
    		
    	}
    	return connection;
    		}
    
    protected HttpURLConnection getConnectionWithUnauthToken(String requestUrl, String method, String body)
        throws Exception
    {
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "unauthorizedToken");
        connection.setDoInput(true);
        
        if (StringUtils.isNotBlank(body))
        {
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }
        else
        {
            connection.setDoOutput(false);
            
        }
        return connection;
    }
    
    protected FilePreUploadResponse uploadFile()
    {
        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "filePath1");
        Long parent = Long.parseLong(MyFileUtils.getDataFromFile(INIT_DATA, "fileParent1"));
        FilePreUploadResponse response = preUpload(filePath, parent);
        postFileObject(response.getUploadUrl(), filePath);
        return response;
    }
    
    protected FilePreUploadResponse uploadFile(Long parent)
    {
        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "filePath1");
        FilePreUploadResponse response = preUpload(filePath, parent);
        postFileObject(response.getUploadUrl(), filePath);
        return response;
    }
    
    protected FilePreUploadResponse uploadFile(String filePath, Long parent)
    {
        FilePreUploadResponse response = preUpload(filePath, parent);
        postFileObject(response.getUploadUrl(), filePath);
        return response;
    }
    
    protected RestFolderInfo createFolderInTeamSpace(Long ownerId, Long parentId)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId;
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName(new RandomGUID().getValueAfterMD5());
        request.setParent(parentId);
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            String body = JSonUtils.toJson(request);
            HttpURLConnection connection = getConnection(url, METHOD_POST, body);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestFolderInfo response = JSonUtils.stringToObject(result, RestFolderInfo.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create folder failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected RestVersionLists listVersions(Long ownerId, Long fileId)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_GET);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestVersionLists response = JSonUtils.stringToObject(result, RestVersionLists.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("list Versions failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    protected RestShareRequestV2 addShare(Long ownerId, Long nodeId, String fileUrl, String name)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + ownerId + "/" + nodeId;
        String body = MyFileUtils.getDataFromFile(fileUrl, name);
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestShareRequestV2 shareReuest = JSonUtils.stringToObject(result, RestShareRequestV2.class);
            return shareReuest;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create Share failed");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    private void close(Closeable obj)
    {
        if (obj != null)
        {
            try
            {
                obj.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private void postFileObject(String uploadUrl, String filePath)
    {
        OutputStream out = null;
        DataInputStream in = null;
        try
        {
            URL realURL = new URL(uploadUrl);
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection conn = (HttpURLConnection) realURL.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            
            String BOUNDARY = "---------------------------7d4a6d158c9"; // 分隔符
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setChunkedStreamingMode(0);
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n\r\n").getBytes();// 定义最后数据分隔线
            
            File file = new File(filePath);
            
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"file" + 1 + "\";filename=\"" + file.getName()
                + "\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");
            byte[] data = sb.toString().getBytes();
            
            out = conn.getOutputStream();
            out.write(data);
            in = new DataInputStream(new FileInputStream(file));
            int bytes = -1;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1)
            {
                out.write(bufferOut, 0, bytes);
            }
            out.write(end_data);
            out.flush();
            
            int status = conn.getResponseCode();
            
            System.out.println("File upload response status: " + status);
            
            // printHeaders(conn);
            // printBody(conn, false);
            conn.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Post file failed!", e);
        }
        finally
        {
            close(in);
            close(out);
        }
        
    }
    
    private FilePreUploadResponse preUpload(String filePath, Long parent)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1;
        File file = new File(filePath);
        FilePreUploadRequest request = new FilePreUploadRequest();
        request.setName(file.getName());
        request.setSize(file.length());
        fileParentId = parent == null ? 0L : parent;
        request.setParent(fileParentId);
        String body = JsonUtils.toJson(request);
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            FilePreUploadResponse response = JSonUtils.stringToObject(result, FilePreUploadResponse.class);
            String uploadUrl = response.getUploadUrl();
            uploadUrl = uploadUrl + "?objectLength=" + file.length();
            response.setUploadUrl(uploadUrl);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Preupload Failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
    
    // private void printBody(HttpURLConnection conn, boolean isError) throws IOException
    // {
    // InputStream in = null;
    // if (isError)
    // {
    // in = conn.getErrorStream();
    // ;
    // }
    // else
    // {
    // in = conn.getInputStream();
    // }
    // InputStreamReader isr = new InputStreamReader(in);
    // BufferedReader br = new BufferedReader(isr);
    // String temp = null;
    // StringBuffer sb = new StringBuffer("");
    // while ((temp = br.readLine()) != null)
    // {
    // sb.append(temp);
    // }
    //
    // System.out.println("Body: " + sb.toString());
    // }
    //
    // private void printHeaders(HttpURLConnection conn) throws Exception
    // {
    // Map<String, List<String>> headerFields = conn.getHeaderFields();
    // Set<Entry<String, List<String>>> entrySet = headerFields.entrySet();
    // Iterator<Entry<String, List<String>>> iter = entrySet.iterator();
    // while (iter.hasNext())
    // {
    // Entry<String, List<String>> obj = iter.next();
    // System.out.print(obj.getKey() + ":");
    // for (String value : obj.getValue())
    // {
    // System.out.println(" " + URLDecoder.decode(value, "UTF-8"));
    // }
    // }
    // }
    
    // protected void login()
    // {
    // String failMsg = "User login faield! ";
    // try
    // {
    // String uri = "/api/v2/token";
    // String url = new StringBuffer(uamServerAddr).append(uri).toString();
    //
    // headers.put("x-device-type", "web");
    // headers.put("x-device-sn", "3");
    // headers.put("x-device-os", "ios");
    // headers.put("x-device-name", "5");
    // headers.put("x-client-version", "1");
    // headers.put("x-request-ip", "10.169.52.192");
    //
    // LoginRequest request = new LoginRequest();
    // request.setLoginName(username);
    // request.setPassword(password);
    // request.setAppId("OneBox");
    // RestResponse response = restClientService.performPostByUri(url, headers, request);
    //
    // // 获取响应
    // int status = response.getStatusCode();
    // if (status / 100 != 2)
    // {
    // fail(failMsg);
    // return;
    // }
    //
    // String content = response.getResponseBody();
    // RestUserloginRsp user = JSonUtils.stringToObject(content, RestUserloginRsp.class);
    // ownerId = user.getCloudUserId();
    // token = user.getToken();
    // System.out.println(" Owner id : " + ownerId);
    // System.out.println(" Token : " + token);
    //
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // fail(failMsg);
    // }
    // }
}
