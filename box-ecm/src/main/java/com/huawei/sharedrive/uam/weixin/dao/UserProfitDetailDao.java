package com.huawei.sharedrive.uam.weixin.dao;

import java.util.List;

import com.huawei.sharedrive.uam.openapi.domain.user.PageRequestUserProfits;
import com.huawei.sharedrive.uam.weixin.domain.UserProfitDetail;

public interface UserProfitDetailDao {

	List<UserProfitDetail> listByTypeAndStatus(byte type,byte status);

	void create(UserProfitDetail userProfitDetail);

	void updateStatus(UserProfitDetail userProfitDetail);

	List<UserProfitDetail> list(UserProfitDetail filter,PageRequestUserProfits requestUserProfits);

}
