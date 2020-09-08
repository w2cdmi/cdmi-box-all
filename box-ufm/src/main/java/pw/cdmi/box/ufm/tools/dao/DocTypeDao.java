package pw.cdmi.box.ufm.tools.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.ufm.tools.domain.DocUserConfig;


/**
 * 文件智能分类数据库访问层
 * @author guoz
 *
 */
public interface DocTypeDao
{
     
     /**
      * 查询配置项
      * @param limit
      * @param prefix
      * @return
      */
     public List<DocUserConfig> getByPrefix(Limit limit, String prefix);
     
     public void insertUserDoctype(DocUserConfig docUserConfig);
     
     public void delUserDocTypeByOwner(long ownerId);
     
     public void delUserDocTypeById(long id);
     
     public DocUserConfig getDocUserConfigById(Long id);
     
     public List<DocUserConfig> getDocUserConfigsByOwnerId(long ownerId);
     
     public long getCountByOwnerId(long ownerId);
     
     public void updateDocTypeById(DocUserConfig docUserConfig);
     
}
