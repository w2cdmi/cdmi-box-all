package pw.cdmi.box.uam.cluster.manage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class HeartbeatCheckServlet extends HttpServlet
{
    private static final long serialVersionUID = -1833357430613093609L;
    
    private static final String RESPONST_CONTENT = "check realserver health";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    
    {
        if (NetworkStatusCache.isReachable())
        {
            PrintWriter out = null;
            try
            {
                out = response.getWriter();
                out.write(RESPONST_CONTENT);
                out.flush();
            }
            finally
            {
                IOUtils.closeQuietly(out);
            }
        }
        else
        {
            response.sendError(500, "Server Failed.");
        }
    }
}
