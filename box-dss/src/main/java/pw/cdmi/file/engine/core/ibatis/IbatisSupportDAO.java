package pw.cdmi.file.engine.core.ibatis;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import pw.cdmi.core.db.dao.BaseDAOImpl;

/**
 * 
 * @author s90006125
 * @param <T>
 * 
 */
@SuppressWarnings("deprecation")
public abstract class IbatisSupportDAO<T extends Serializable> extends BaseDAOImpl<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IbatisSupportDAO.class);
    
    /**
     * 命名映射
     */
    private static final Map<String, String> NAMING_MAP = Collections.synchronizedMap(new HashMap<String, String>(5));
    
    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;
    
    /**
     * 命名空间与SQL语句分隔符号
     */
    private static final String SQL_SEP = ".";
    
    protected static final String SQL_INSERT = "insert";
    
    protected static final String SQL_SELECT = "select";
    
    protected static final String SQL_DELETE = "delete";
    
    protected static final String SQL_UPDATE = "update";
    
    protected static final String SQL_SELECT_ALL = "selectAll";
    
    protected String warpSqlstatement(Class<? extends Object> clazz, String stmt)
    {
        final String namespace = this.getNamesapce(clazz);
        return namespace + SQL_SEP + stmt;
    }
    
    /**
     * 获取命名空间名称
     * 
     * @param object 对象
     * @return
     */
    @SuppressWarnings("unchecked")
    protected String getNamesapce(Object object)
    {
        @SuppressWarnings("rawtypes")
        final Class clazz = object.getClass();
        return getNamesapce(clazz);
    }
    
    /**
     * 获取命名空间名称
     * 
     * @param clazz 对象类型
     * @return
     */
    protected String getNamesapce(Class<? extends Object> clazz)
    {
        String namespace = null;
        final String clazzName = clazz.getName();
        LOGGER.debug("NamingSQLAdapte.className:" + clazzName);
        
        // 从缓存中获取
        namespace = NAMING_MAP.get(clazzName);
        
        // 如果缓存中还没有保存对应的SQL命名空间名称，则初始化并且加载
        if (null == namespace)
        {
            Namingspace namingpace = (Namingspace) clazz.getAnnotation(Namingspace.class);
            namespace = namingpace.value();
            if (null == namespace || "".equals(namespace.trim()))
            {
                throw new RuntimeException("namespace is empty.");
            }
            NAMING_MAP.put(clazzName, namespace);
        }
        return namespace;
    }
}
