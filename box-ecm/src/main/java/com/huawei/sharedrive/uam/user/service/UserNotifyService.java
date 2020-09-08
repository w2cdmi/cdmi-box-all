package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.user.domain.NtlmCaches;

public interface UserNotifyService {
    void sendMessage(long enterpriseId, long enterpriseUserId, String message);
}
