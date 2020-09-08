/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.box.disk.message.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>业务通知入口</pre>
 * @Project Alpha CDMI Service Platform, box-weixin Component. 2017/9/2
 ************************************************************/
@Controller
@RequestMapping(value = "/notification")
public class NotificationController {
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        return "notification/notificationList";
    }

    @RequestMapping(value = "collectionBox", method = RequestMethod.GET)
    public String collectionBox(Model model) {
        return "notification/collectionBox";
    }

    @RequestMapping(value = "approveList", method = RequestMethod.GET)
    public String approveList(Model model) {
        return "notification/approveList";
    }
}
