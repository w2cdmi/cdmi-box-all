/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.test.dataserver;

import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

/**
 * 
 * @author s90006125
 *
 */
public class RegionServiceTest extends AbstractSpringTest
{
    @Autowired
    private RegionService regionService;
}
