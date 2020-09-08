package com.huawei.sharedrive.uam.watermark.service;

import java.io.InputStream;

public interface WaterMarkServcie
{
    
    void putWaterMark(long accoutId, InputStream in, int imagelength);
    
    byte[] getWaterMark(long accoutId);
    
}
