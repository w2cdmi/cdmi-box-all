package com.huawei.sharedrive.app.core.domain;

import java.io.Serializable;

import com.huawei.sharedrive.app.exception.InvalidParamException;

/**
 * 缩略图对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-4
 * @see
 * @since
 */
public class Thumbnail implements Serializable
{
    public static final int DEFAULT_BIGBIG_HEIGHT = 200;
    
    public static final int DEFAULT_BIGBIG_WIDTH = 200;
    
    public static final int DEFAULT_BIG_HEIGHT = 96;
    
    public static final int DEFAULT_BIG_WIDTH = 96;
    
    public static final int DEFAULT_SMALL_HEIGHT = 32;
    
    public static final int DEFAULT_SMALL_WIDTH = 32;
    
    private static final long serialVersionUID = 7319693071900240402L;
    
    private Integer height;
    
    private Integer width;
    
    public Thumbnail()
    {
        
    }
    
    public Thumbnail(Integer width, Integer height)
    {
        this.width = width;
        this.height = height;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (width == null || height == null || width <= 0 || height <= 0)
        {
            throw new InvalidParamException();
        }
    }
    
    public Integer getHeight()
    {
        return height;
    }
    
    public Integer getWidth()
    {
        return width;
    }
    
    public void setHeight(Integer height)
    {
        this.height = height;
    }
    
    public void setWidth(Integer width)
    {
        this.width = width;
    }
    
}
