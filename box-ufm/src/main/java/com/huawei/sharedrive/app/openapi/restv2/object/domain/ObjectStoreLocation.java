package com.huawei.sharedrive.app.openapi.restv2.object.domain;

/**
 * 对象文字
 * @author c00287749
 *
 */
public class ObjectStoreLocation
{
    private String id;
    
    private StoreLocation storeLocation;

    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }


    public StoreLocation getStoreLocation()
    {
        return storeLocation;
    }

    public void setStoreLocation(StoreLocation storeLocation)
    {
        this.storeLocation = storeLocation;
    }
}
