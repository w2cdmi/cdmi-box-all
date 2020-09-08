package pw.cdmi.file.engine.core.ibatis;

import pw.cdmi.core.db.dao.MultiTableEntity;
import pw.cdmi.file.engine.core.common.BaseEntity;

/**
 * 支持分表的dao基础类
 * 
 * @author s90006125
 * @param <T>
 * 
 */
public abstract class IbatisMultiTableSupportDAO<T extends BaseEntity> extends IbatisSupportDAO<T>
{
    /**
     * 获取分表对象
     * 
     * @param obj
     * @return
     */
    protected abstract MultiTableEntity<T> generateMultiTableEntity(final T obj);
}
