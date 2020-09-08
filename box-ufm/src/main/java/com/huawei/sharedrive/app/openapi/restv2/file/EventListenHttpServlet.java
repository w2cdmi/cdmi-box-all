/**
 * 
 */
package com.huawei.sharedrive.app.openapi.restv2.file;

import javax.servlet.annotation.WebServlet;

import org.springframework.web.context.support.HttpRequestHandlerServlet;

/**
 * @author q90003805
 * 
 */
@WebServlet(urlPatterns = {"/api/v2/event/listen"}, asyncSupported = true, name = "eventListeServletHandler")
public class EventListenHttpServlet extends HttpRequestHandlerServlet
{
    
    private static final long serialVersionUID = 4406145000941543661L;
    
}
