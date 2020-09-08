package com.huawei.sharedrive.uam.openapi.domain.account;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class AccountTitle implements Serializable
{
    private static final long serialVersionUID = 6715844470358523896L;
    
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
}
