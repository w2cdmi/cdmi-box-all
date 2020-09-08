package com.huawei.sharedrive.app.test.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.domain.Limit;

public class UserDAOTest extends AbstractSpringTest
{
    
    private String loginName = "testLoginName_";
    
    private String password = "testPassword_";
    
    private String name = "testName_";
    
    @Autowired
    private UserDAOV2 userDAO;
    
    @Autowired
    private UserReverseDAO userReverseDAO;
    
    @Test
    public void testCreate()
    {
        for (int i = 0; i < 100; i++)
        {
            //long id = userDAO.getNextAvailableUserId();
            User user = new User();
            user.setId(i);
            user.setPassword(password + i);
            user.setLoginName(loginName + i);
            user.setName(name + i);
            user.setCreatedAt(new Date());
            userDAO.create(user);
            user = userDAO.get((long)i);
            Assert.assertEquals(password + i, user.getPassword());
        }
    }
    
    @Test
    public void testUpdate()
    {
        List<User> list = userReverseDAO.getFilterd(null, null, null);
        for (User user : list)
        {
            long id = user.getId();
            user.setPassword("modifyPassword");
            user.setName("modifyName");
            userDAO.update(user);
            user = userDAO.get(id);
            Assert.assertEquals("modifyPassword", user.getPassword());
        }
    }
    
    @Test
    public void testDelete()
    {
        List<User> list = userReverseDAO.getFilterd(null, null, null);
        for (User user : list)
        {
            long id = user.getId();
            userDAO.delete(1, id);
            user = userDAO.get(id);
            Assert.assertNull(user);
        }
    }
    
    @Test
    public void testFilterList()
    {
        User user = new User();
        user.setLoginName("testLoginName_6");
        OrderV1 sort = new OrderV1();
        sort.setField("id");
        sort.setDesc(true);
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(20);
        int count = userReverseDAO.getFilterdCount(user);
        System.out.println(count);
        count = userReverseDAO.getFilterdCount(null);
        System.out.println(count);
        List<User> list = userReverseDAO.getFilterd(user, sort, limit);
        System.out.println(list.size());
        for (User user0 : list)
        {
            System.out.println(ToStringBuilder.reflectionToString(user0));
        }
    }
}
