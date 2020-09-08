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
import pw.cdmi.core.utils.HashTool;
import pw.cdmi.file.dao.FileObjectDao;
import pw.cdmi.file.domain.FileObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author s90006125
 */
@SuppressWarnings({"deprecation"})
@Repository("fileObjectDao")
public class FileObjectDaoImpl implements FileObjectDao {
    private final static int TABLE_COUNT = 100;

    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;

    public SqlMapClientTemplate getSqlMapClientTemplate() {
        return sqlMapClientTemplate;
    }

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    @Override
    public FileObject get(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("objectId", id);
        map.put("tablePostfix", getTablePostfix(id));

        return (FileObject)sqlMapClientTemplate.queryForObject("FileObject.get", map);
    }

    private String getTablePostfix(String objectId) {
        return "" + (HashTool.apply(objectId) % TABLE_COUNT);
    }
}
