package com.huawei.sharedrive.app.test.dao;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

import pw.cdmi.common.slavedb.dao.DatabaseAddrDAO;
import pw.cdmi.common.slavedb.domain.DatabaseAddr;

public class DatabaseAddrDAOTest extends AbstractSpringTest
{
    
    @Autowired
    private DatabaseAddrDAO databaseAddrDAO;
    
    @Test
    public void testFilterList()
    {
        List<DatabaseAddr> list = databaseAddrDAO.getAll();
        System.out.println(list.size());
        for (DatabaseAddr addr : list)
        {
            System.out.println(ToStringBuilder.reflectionToString(addr));
        }
    }
}
