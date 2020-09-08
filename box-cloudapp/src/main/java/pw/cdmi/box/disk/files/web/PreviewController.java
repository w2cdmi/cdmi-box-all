package pw.cdmi.box.disk.files.web;

import com.huawei.cs.json.JSONException;
import com.huawei.cs.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pw.cdmi.box.disk.client.api.FileClient;
import pw.cdmi.box.disk.client.domain.node.PreviewMetaResponse;
import pw.cdmi.box.disk.client.domain.node.PreviewUrlResponse;
import pw.cdmi.box.disk.files.domain.ImgViewInfo;
import pw.cdmi.core.restrpc.RestClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//用于文件预览的页面，目前系统使用的是idocview服务器，未使用此Controller。
@Deprecated
@Controller
@RequestMapping(value = "/preview")
public class PreviewController extends CommonController {
    private FileClient fileClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewController.class);
    @Resource
    private RestClient ufmClientService;

    /*
     * 获取预览文件信息
     */
    @RequestMapping(value = "/files/{ownerId}/{fileId}", method = RequestMethod.GET)
    public String previewFile(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId, Model model) {
        return "files/baiduPreviewReader";
    }

    /*
     * 获取预览文件信息
     */
    @RequestMapping(value = "viewInfo/{ownerId}/{fileId}/{viewFlag}", method = RequestMethod.GET)
    public String getPreviewInfo(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId, @PathVariable("viewFlag") long viewFlag, Model model) {

        // 是否可以预览
        model.addAttribute("viewFlag", viewFlag);
        PreviewController.LOGGER.info("getPreviewInfo/" + ownerId + "/" + fileId + "/" + viewFlag);

        String token = getToken();
        PreviewMetaResponse res = fileClient.getPreviewMeta(token, ownerId, fileId);
        String num = res.getTotalPages();
        String pageSize = res.getRange();

        model.addAttribute("num", num);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("fileId", fileId);
        model.addAttribute("page", 1);
        PreviewController.LOGGER.info("pageSize####" + pageSize);
        return "imgView/imageView";

    }

    @RequestMapping(value = "viewMetaInfo/{ownerId}/{fileId}/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ImgViewInfo> getPreviewMetaResponse(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId, @PathVariable("page") long page) {
        // 是否可以预览
        PreviewController.LOGGER.info("getPreviewInfo/" + ownerId + "/" + fileId + "/" + page);

        String token = getToken();
        PreviewMetaResponse res = fileClient.getPreviewMeta(token, ownerId, fileId);
        PreviewUrlResponse presponse = fileClient.getPreviewUrl(token, ownerId, fileId);
        String range = getFileRange(res.getRange(), String.valueOf(page));
        String url = presponse.getUrl() + "?range=bytes=" + range;
        PreviewController.LOGGER.info("pageSize####" + res.getTotalPages());
        ImgViewInfo imgViewInfo = new ImgViewInfo();
        imgViewInfo.setUrl(url);
        imgViewInfo.setCurPage(page);
        imgViewInfo.setTotalPage(Long.valueOf(res.getTotalPages()));
        return new ResponseEntity<ImgViewInfo>(imgViewInfo, HttpStatus.OK);

    }

    @RequestMapping(value = "getViewFlag/{ownerId}/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getViewFlag(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId) {
        String token = getToken();
        PreviewMetaResponse res = fileClient.getPreviewMeta(token, ownerId, fileId);
        String pageSize = res.getRange();
        String viewFlag = "0";
        if (!pageSize.isEmpty()) {
            viewFlag = "2";
        }
        JSONObject json = new JSONObject();
        try {
            json.put("viewFlag", viewFlag); // 原有的數據
            if (res.getInodeSize() > res.getMaxSize()) {
                json.put("isSizeLarge", true);
            } else {
                json.put("isSizeLarge", false);
            }
        } catch (JSONException e) {
            PreviewController.LOGGER.error("getViewFlag:" + e);
            e.printStackTrace();
        }
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "imageInfo/{ownerId}/{fileId}/{page}", method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@PathVariable("ownerId") long ownerId, @PathVariable("fileId") long fileId, @PathVariable("page") long page, HttpServletResponse response, Model model) {
        String token = getToken();
        PreviewMetaResponse res = fileClient.getPreviewMeta(token, ownerId, fileId);
        String pageSize = res.getRange();
        PreviewController.LOGGER.info("getImage/" + ownerId + "/" + fileId + "/" + page);
        String range = getFileRange(pageSize, String.valueOf(page));
        PreviewUrlResponse presponse = fileClient.getPreviewUrl(token, ownerId, fileId);
        String url = presponse.getUrl();
        model.addAttribute("page", page);

        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream outs = null;
        try {
            trustAllHosts();
            URL reurl = new URL(url);
            if (reurl.getProtocol().toLowerCase().equals("https")) {
                HttpsURLConnection https = (HttpsURLConnection) reurl.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) reurl.openConnection();
            }
            conn.setRequestProperty("Range", "bytes=" + range);
            conn.connect();
            System.out.println(conn.getResponseCode());
            in = conn.getInputStream();
            outs = response.getOutputStream();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                outs.write(buffer, 0, len);
            }
        } catch (IOException e) {
            PreviewController.LOGGER.error("getImage error", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    PreviewController.LOGGER.error("getImage error", e);
                }
            }
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException e) {
                    PreviewController.LOGGER.error("getImage error", e);
                }
            }
        }
    }

    private String getFileRange(String jsonStr, String num) {
        PreviewController.LOGGER.info("getFileRange####" + jsonStr);
        Map<String, String> outMap = new HashMap<String, String>();
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(jsonStr);
            Iterator<String> nameItr = jsonObj.keys();
            String name;

            while (nameItr.hasNext()) {
                name = nameItr.next();
                outMap.put(name, jsonObj.getString(name));
            }
        } catch (Exception e) {
            PreviewController.LOGGER.error("getFileRange fail");
        }

        return outMap.get(num);
    }

    @PostConstruct
    public void init() {
        this.fileClient = new FileClient(ufmClientService);
    }

    private static void trustAllHosts() {

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
