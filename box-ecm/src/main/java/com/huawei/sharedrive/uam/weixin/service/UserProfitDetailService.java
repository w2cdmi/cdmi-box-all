package com.huawei.sharedrive.uam.weixin.service;

import java.util.List;

import com.huawei.sharedrive.uam.openapi.domain.user.PageRequestUserProfits;
import com.huawei.sharedrive.uam.weixin.domain.UserProfitDetail;

public interface UserProfitDetailService {

	List<UserProfitDetail> listByTypeAndStatus(byte type,byte status);

	void create(UserProfitDetail userProfitDetail);

    List<UserProfitDetail> list(UserProfitDetail filter, PageRequestUserProfits requestUserProfits);

	void updateStatus(UserProfitDetail userProfitDetail);
}
