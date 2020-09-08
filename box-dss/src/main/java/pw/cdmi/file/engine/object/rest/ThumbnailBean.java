/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.rest;

import javax.servlet.http.HttpServletRequest;

import pw.cdmi.file.engine.object.rest.support.RequestConstants;

public class ThumbnailBean
{
    public static ThumbnailBean getBean(HttpServletRequest request)
    {
        ThumbnailBean thumbnailBean = new ThumbnailBean();
        String heightStr = request.getParameter(RequestConstants.REQUEST_THUMBNAIL_HEIGTH);
        if(null == heightStr)
        {
            thumbnailBean.setHeight(800);
        }
        else
        {
            thumbnailBean.setHeight(Integer.parseInt(heightStr));
        }
        String widthStr = request.getParameter(RequestConstants.REQUEST_THUMBNAIL_WIDTH);
        if(null == widthStr)
        {
            thumbnailBean.setWidth(600);
        }
        else
        {
            thumbnailBean.setWidth(Integer.parseInt(widthStr));
        }
        String defalut = request.getParameter(RequestConstants.REQUEST_THUMBNAIL_DEFAULT);
        if(null == defalut)
        {
            thumbnailBean.setNeedDefault(true);
        }
        else
        {
            thumbnailBean.setNeedDefault(Boolean.parseBoolean(defalut));
        }
        return thumbnailBean;
    }

    private int height;


    private boolean needDefault;


    private int width;


    public int getHeight()
    {
        return height;
    }


    public int getWidth()
    {
        return width;
    }


    public boolean isNeedDefault()
    {
        return needDefault;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public void setNeedDefault(boolean needDefault)
    {
        this.needDefault = needDefault;
    }
    
    
    public void setWidth(int width)
    {
        this.width = width;
    }
}
