package com.huawei.sharedrive.isystem.syslog.web;

import java.text.DateFormat;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;

public class CustomDateEditorImpl extends CustomDateEditor
{
    
    public CustomDateEditorImpl(DateFormat dateFormat, boolean allowEmpty)
    {
        super(dateFormat, allowEmpty);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void setAsText(String text)
    {
        if (StringUtils.isNotEmpty(text))
        {
            setAsText(text);
        }
    }
}
