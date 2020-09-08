package com.huawei.sharedrive.app.core.domain;

import org.junit.Test;

public class ThumbnailTest
{
    @Test
    public void thumbnailTest()
    {
        Thumbnail thumb = new Thumbnail();
        thumb.getClass();
    }
    
    @Test
    public void thumbnailTest2()
    {
        Thumbnail thumb = new Thumbnail();
        thumb.getHeight();
    }
    
    @Test
    public void thumbnailTest3()
    {
        Thumbnail thumb = new Thumbnail();
        thumb.getWidth();
    }
    
    @Test
    public void thumbnailTest4()
    {
        Thumbnail thumb = new Thumbnail();
        thumb.setHeight(12);
    }
    
    @Test
    public void thumbnailTest5()
    {
        Thumbnail thumb = new Thumbnail();
        thumb.setWidth(20);
    }
    
    @Test
    public void thumbnailTest6()
    {
        Thumbnail thumb = new Thumbnail();
        thumb.toString();
    }
}
