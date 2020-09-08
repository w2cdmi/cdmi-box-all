/**
 * 
 */
package com.huawei.sharedrive.app.files.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.domain.RecentBrowse;
import com.huawei.sharedrive.app.files.synchronous.INodeRowHandle;

import pw.cdmi.box.domain.Limit;

/**
 * @author q90003805
 *         
 */
public interface RecentBrowseDao
{
    List<RecentBrowse> list(long userId,long accountId,long offset,long limit);
    void deleteByTime(Date dateTime);
    void create(RecentBrowse recentBrowse);
	void delete(RecentBrowse recentBrowse);
	RecentBrowse get(RecentBrowse recentBrowse);
	void deleteRecentByNode(long ownerId, long nodeId);
	void updateCreateAt(RecentBrowse recentBrowse);
	void deleteRecentByUserId(RecentBrowse recentBrowse);
}
