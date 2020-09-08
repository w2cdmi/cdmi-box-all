package com.huawei.sharedrive.app.oauth2.dao;

import com.huawei.sharedrive.app.oauth2.domain.AuthCode;

import pw.cdmi.box.dao.BaseDAO;

public interface AuthCodeDAO extends BaseDAO<AuthCode, String>
{
    AuthCode getAuthCodeByUserId(long userId, String clientId);
}
