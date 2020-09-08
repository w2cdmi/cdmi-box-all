package com.huawei.sharedrive.uam.watermark.exception;

@SuppressWarnings("serial")
public class WaterMarkException extends Exception
{
    
    public static final String FILE_BIG_ERROR = "watermask.imge.big.error";
    
    public static final String FILE_FROM_ERROR = "watermask.imge.from.error";
    
    public static final String FILE_UPLOAD_ERROR = "watermask.umage.upload.error";
    
    public WaterMarkException()
    {
        
    }
    
    public WaterMarkException(String message)
    {
        super(message);
    }
    
}
