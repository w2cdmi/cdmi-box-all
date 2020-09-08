package com.huawei.sharedrive.isystem.util;

import org.springframework.stereotype.Service;

@Service("verifyCodeData")
public class VerifyCodeData
{
    private String bIsSetBackground;
    
    private String bIsSetInterferon;
    
    private String dictionary;
    
    private String bVariableFont;
    
    private String bVariableFontSize;
    
    private String bIsRotate;

    public String getbIsSetBackground()
    {
        return bIsSetBackground;
    }

    public void setbIsSetBackground(String bIsSetBackground)
    {
        this.bIsSetBackground = bIsSetBackground;
    }

    public String getbIsSetInterferon()
    {
        return bIsSetInterferon;
    }

    public void setbIsSetInterferon(String bIsSetInterferon)
    {
        this.bIsSetInterferon = bIsSetInterferon;
    }

    public String getDictionary()
    {
        return dictionary;
    }

    public void setDictionary(String dictionary)
    {
        this.dictionary = dictionary;
    }

    public String getbVariableFont()
    {
        return bVariableFont;
    }

    public void setbVariableFont(String bVariableFont)
    {
        this.bVariableFont = bVariableFont;
    }

    public String getbVariableFontSize()
    {
        return bVariableFontSize;
    }

    public void setbVariableFontSize(String bVariableFontSize)
    {
        this.bVariableFontSize = bVariableFontSize;
    }

    public String getbIsRotate()
    {
        return bIsRotate;
    }

    public void setbIsRotate(String bIsRotate)
    {
        this.bIsRotate = bIsRotate;
    }
    
    
    
}
