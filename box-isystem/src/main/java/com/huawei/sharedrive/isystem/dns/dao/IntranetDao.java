package com.huawei.sharedrive.isystem.dns.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.dns.domain.Intranet;

import pw.cdmi.box.dao.BaseDAO;

public interface IntranetDao extends BaseDAO<Intranet, String>
{
    List<Intranet> getAllList();
}
