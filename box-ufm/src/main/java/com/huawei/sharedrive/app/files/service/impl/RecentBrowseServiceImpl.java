package com.huawei.sharedrive.app.files.service.impl;

import com.huawei.sharedrive.app.files.dao.RecentBrowseDao;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.RecentBrowse;
import com.huawei.sharedrive.app.files.service.RecentBrowseService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("recentBrowseService")
public class RecentBrowseServiceImpl implements RecentBrowseService {
    @Autowired
    private RecentBrowseDao recentBrowseDao;

    @Override
    public void create(RecentBrowse recentBrowse) {
        recentBrowseDao.create(recentBrowse);
    }

    @Override
    public void createByNode(UserToken user, INode node) {
        RecentBrowse recentBrowse = new RecentBrowse();
        recentBrowse.setAccountId(user.getAccountId());
        recentBrowse.setInodeId(node.getId());
        recentBrowse.setOwnedBy(node.getOwnedBy());
        recentBrowse.setUserId(user.getId());
        recentBrowse.setLastBrowseTime(new Date());
        RecentBrowse old = recentBrowseDao.get(recentBrowse);
        if (old == null) {
            recentBrowseDao.create(recentBrowse);
        } else {
            recentBrowseDao.updateCreateAt(recentBrowse);
        }
    }

    @Override
    public void delete(INode file) {
        RecentBrowse recentBrowse = new RecentBrowse();
        recentBrowse.setInodeId(file.getId());
        recentBrowse.setOwnedBy(file.getOwnedBy());
        recentBrowseDao.delete(recentBrowse);
    }

    @Override
    public void deleteRecentByUserId(long userId, long ownerId, long nodeId) {
        RecentBrowse recentBrowse = new RecentBrowse();
        recentBrowse.setInodeId(nodeId);
        recentBrowse.setOwnedBy(ownerId);
        recentBrowse.setUserId(userId);
        recentBrowseDao.delete(recentBrowse);
    }
}
