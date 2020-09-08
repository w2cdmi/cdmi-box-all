/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 *
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;
import pw.cdmi.file.dao.CloudEndpointDao;
import pw.cdmi.file.domain.FsEndpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * @author s90006125
 */
@SuppressWarnings({"deprecation"})
@Repository("cloudEndpointDao")
public class CloudEndpointDaoImpl implements CloudEndpointDao {
    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;

    public SqlMapClientTemplate getSqlMapClientTemplate() {
        return sqlMapClientTemplate;
    }

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    @Override
    public FsEndpoint get(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        return (FsEndpoint)sqlMapClientTemplate.queryForObject("FsEndpoint.get", map);
    }
}
