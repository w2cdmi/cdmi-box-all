package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.event.*;
import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>微信事件工厂类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public class WxEventFactory {
    private static Logger logger = LoggerFactory.getLogger(WxEventFactory.class);

    public static WxSysEvent parseSysEvent(String body) {
        try {
            body = trim(body);
            Document document = DocumentHelper.parseText(body);

            Element root = document.getRootElement();

            String infoType =root.element("InfoType").getTextTrim();
            String changeType = null;
            Element e = root.element("ChangeType");
            if(e != null) {
                changeType = e.getTextTrim();
            }

            WxSysEvent event = newSysEvent(infoType, changeType);
            if(event == null) {
                logger.error("Failed to create event for infoTpe={}, changeType={}", infoType, changeType);
                return null;
            }

            fillProperties(root, event);

            return event;
        } catch (DocumentException e) {
            logger.error("Failed to create event for error={}, xml={}", e.getMessage(), body);
            e.printStackTrace();
        }

        return null;
    }

    protected static WxSysEvent newSysEvent(String infoType, String changeType) {
        WxSysEvent event = null;

        switch (infoType) {
            case "register_corp":
                event = new SysRegisterCorpEvent();
                break;
            default:
        }

        if(event != null) {
            event.setInfoType(infoType);
        }

        return event;
    }

    public static WxSuiteEvent parseSuiteEvent(String body) {
        try {
            body = trim(body);
            Document document = DocumentHelper.parseText(body);

            Element root = document.getRootElement();

            String infoType =root.element("InfoType").getTextTrim();
            String changeType = null;
            Element e = root.element("ChangeType");
            if(e != null) {
                changeType = e.getTextTrim();
            }

            WxSuiteEvent event = newSuiteEvent(infoType, changeType);
            if(event == null) {
                logger.error("Failed to create event for infoTpe={}, changeType={}", infoType, changeType);
                return null;
            }

            fillProperties(root, event);

            return event;
        } catch (DocumentException e) {
            logger.error("Failed to create event for error={}, xml={}", e.getMessage(), body);
            e.printStackTrace();
        }

        return null;
    }

    protected static WxSuiteEvent newSuiteEvent(String infoType, String changeType) {
        WxSuiteEvent event = null;

        switch (infoType) {
            case "suite_ticket":
                event = new SuiteTicketEvent();
                break;
            case "create_auth":
                event = new SuiteCreateAuthEvent();
                break;
            case "change_auth":
                event = new SuiteChangeAuthEvent();
                break;
            case "cancel_auth":
                event = new SuiteCancelAuthEvent();
                break;
            case "change_contact":
                event = newSuiteEvent(changeType);
                break;
            default:
        }

        if(event != null) {
            event.setInfoType(infoType);
            event.setChangeType(changeType);
        }

        return event;
    }

    protected static WxSuiteEvent newSuiteEvent(String changeType) {
        WxSuiteEvent event = null;

        switch (changeType) {
            case "create_user":
                event = new SuiteCreateUserEvent();
                break;
            case "update_user":
                event = new SuiteUpdateUserEvent();
                break;
            case "delete_user":
                event = new SuiteDeleteUserEvent();
                break;
            case "create_party":
                event = new SuiteCreatePartyEvent();
                break;
            case "update_party":
                event = new SuiteUpdatePartyEvent();
                break;
            case "delete_party":
                event = new SuiteDeletePartyEvent();
                break;
            case "update_tag":
                event = new SuiteUpdateTagEvent();
                break;
        }

        return event;
    }

    public static WxAppEvent parseAppEvent(String body) {
        try {
            body = trim(body);
            Document document = DocumentHelper.parseText(body);

            Element root = document.getRootElement();

            String event = root.element("Event").getTextTrim();
            String changeType = null;
            Element element = root.element("ChangeType");
            if(element != null) {
                changeType = element.getTextTrim();
            }

            WxAppEvent e = newAppEvent(event, changeType);
            if(e == null) {
                logger.error("Failed to create event for Event={}, changeType={}", event, changeType);
                return null;
            }

            fillProperties(root, e);

            return e;
        } catch (DocumentException e) {
            logger.error("Failed to create event for error={}, xml={}", e.getMessage(), body);
            e.printStackTrace();
        }

        return null;
    }

    protected static WxAppEvent newAppEvent(String event, String changeType) {
        WxAppEvent e = null;

        switch (event) {
            case "subscribe":
                e = new AppSubscribeEvent();
                break;
            case "unsubscribe":
                e = new AppUnsubscribeEvent();
                break;
            case "enter_agent":
                e = new AppEnterAgentEvent();
                break;
            case "location":
            case "LOCATION":
                e = new AppLocationEvent();
                break;
            case "batch_job_result":
                e = new AppBatchJobResultEvent();
                break;
            case "click":
                e = new AppClickEvent();
                break;
            case "view":
                e = new AppViewEvent();
                break;
            case "scancode_push":
                e = new AppScanCodePushEvent();
                break;
            case "scancode_waitmsg":
                e = new AppScanCodeWaitMessageEvent();
                break;
            case "pic_sysphoto":
                e = new AppPicturePhoneEvent();
                break;
            case "pic_photo_or_album":
                e = new AppPicturePhoneOrAlbumEvent();
                break;
            case "pic_weixin":
                e = new AppPictureWeixinEvent();
                break;
            case "location_select":
                e = new AppLocationSelectEvent();
                break;
            case "change_contact":
                e = newAppEventByChangeType(changeType);
                break;
            default:
        }

        if(e != null) {
            e.setEvent(event);
            e.setChangeType(changeType);
        }

        return e;
    }

    protected static WxAppEvent newAppEventByChangeType(String changeType) {
        WxAppEvent event = null;

        switch (changeType) {
            case "create_user":
                event = new AppCreateUserEvent();
                break;
            case "update_user":
                event = new AppUpdateUserEvent();
                break;
            case "delete_user":
                event = new AppDeleteUserEvent();
                break;
            case "create_party":
                event = new AppCreatePartyEvent();
                break;
            case "update_party":
                event = new AppUpdatePartyEvent();
                break;
            case "delete_party":
                event = new AppDeletePartyEvent();
                break;
            case "update_tag":
                event = new AppUpdateTagEvent();
                break;
        }

        return event;
    }

    public static WwAppMessage parseAppMessage(String body) {
        try {
            body = trim(body);
            Document document = DocumentHelper.parseText(body);

            Element root = document.getRootElement();
            String msgType = root.element("MsgType").getTextTrim();

            WwAppMessage e = newAppMessage(msgType);
            if(e == null) {
                logger.error("Failed to create message for type={}, xml={}", msgType, body);
                return null;
            }

            fillProperties(root, e);

            return e;
        } catch (DocumentException e) {
            logger.error("Failed to create event for error={}, xml={}", e.getMessage(), body);
            e.printStackTrace();
        }

        return null;
    }

    protected static WwAppMessage newAppMessage(String type) {
        WwAppMessage e = null;

        switch (type) {
            case "text":
                e = new WwAppTextMessage();
                break;
            case "image":
                e = new WwAppImageMessage();
                break;
            case "voice":
                e = new WwAppVoiceMessage();
                break;
            case "video":
                e = new WwAppVideoMessage();
                break;
            case "location":
                e = new WwAppLocationMessage();
                break;
            case "link":
                e = new WwAppLinkMessage();
                break;
            default:
        }

        return e;
    }

    protected static void fillProperties(Element root, Object event) {
        for (Iterator it = root.elementIterator(); it.hasNext(); ) {
            Element element = (Element) it.next();

            String attrName = element.getName();
            if(element.elements().isEmpty()) {
                try {
                    BeanUtils.setProperty(event, camel(attrName), element.getTextTrim());
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            } else {
                //固定使用rest包下的类
                try {
                    Class beanClass = Class.forName("com.huawei.sharedrive.uam.weixin.rest." + attrName);
                    Object bean = beanClass.newInstance();
                    fillProperties(element, bean);
                    BeanUtils.setProperty(event, camel(attrName), bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //暂不支持ExtAttr属性
        }
    }

    protected static String trim(String body) {
        body = body.trim();

        int start = 0;
        if(body.startsWith("{")) {
            start = 1;
        }

        int end = body.length();
        if(body.endsWith("}")) {
            end -= 1;
        }

        return body.substring(start, end);
    }

    protected static String camel(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}
