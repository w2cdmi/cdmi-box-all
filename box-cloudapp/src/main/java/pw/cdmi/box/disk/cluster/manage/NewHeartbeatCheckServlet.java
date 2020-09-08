package pw.cdmi.box.disk.cluster.manage;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 新的心跳<br>
 * 失败后返回500
 * @author s90006125
 *
 */
public class NewHeartbeatCheckServlet extends HeartbeatCheckServlet
{
    private static final long serialVersionUID = -1833357430613093609L;

    private static final String RESPONST_CONTENT = "check realserver health";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (NetworkStatusCache.isReachable())
        {
            responseOut(response, RESPONST_CONTENT);
        }
        else
        {
            response.sendError(500, "Server Failed.");
        }
    }
}
