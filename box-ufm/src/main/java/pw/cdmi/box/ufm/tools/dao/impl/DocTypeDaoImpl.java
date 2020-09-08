package pw.cdmi.box.ufm.tools.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.utils.Constants;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.ufm.tools.dao.DocTypeDao;
import pw.cdmi.box.ufm.tools.domain.DocUserConfig;


/**
 * 文件智能分类数据库访问层
 * @author guoz
 *
 */
@Component
public class DocTypeDaoImpl extends AbstractDAOImpl implements DocTypeDao
{

     @SuppressWarnings("unchecked")
     @Override
     public List <DocUserConfig> getByPrefix(Limit limit , String prefix)
     {
          Map<String, Object> map = new HashMap<String, Object>(2);
          map.put("prefix", prefix);
          map.put("appId", Constants.UFM_DEFAULT_APP_ID);
          map.put("limit", limit);
          List<DocUserConfig> list=sqlMapClientTemplate.queryForList("DocTypeConfig.getByPrefix", map);
          
          return list;
     }
     
	@Override
	public void insertUserDoctype(DocUserConfig docUserConfig) {
		try{
		sqlMapClientTemplate.insert("DocTypeConfig.insert", docUserConfig);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delUserDocTypeByOwner(long ownerId) {
//		Map<String, Object> map = new HashMap<String, Object>(2);
//        map.put("ownerId", ownerId);
//        map.put("appId", Constants.UFM_DEFAULT_APP_ID);
		DocUserConfig docUserConfig = new DocUserConfig();
		docUserConfig.setUserId(ownerId);
		docUserConfig.setAppId(Long.valueOf(Constants.UFM_DEFAULT_APP_ID));
        sqlMapClientTemplate.delete("DocTypeConfig.deleteOwner", docUserConfig);
		
	}

	@Override
	public void delUserDocTypeById(long id) {
		DocUserConfig docUserConfig = new DocUserConfig();
		docUserConfig.setId(id);
		docUserConfig.setAppId(Long.valueOf(Constants.UFM_DEFAULT_APP_ID));
		try{
			sqlMapClientTemplate.delete("DocTypeConfig.delete", docUserConfig);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public DocUserConfig getDocUserConfigById(Long id) {
		
		return (DocUserConfig)sqlMapClientTemplate.queryForObject("DocTypeConfig.getById", id);
	}

	@Override
	public List<DocUserConfig> getDocUserConfigsByOwnerId(long ownerId) {
		List<DocUserConfig> list=sqlMapClientTemplate.queryForList("DocTypeConfig.getdocTypeConfigByOwner", ownerId);
		return list;
	}

	@Override
	public long getCountByOwnerId(long ownerId) {
		// TODO Auto-generated method stub
		return (long)sqlMapClientTemplate.queryForObject("DocTypeConfig.getCountByOwner", ownerId);
	}

	@Override
	public void updateDocTypeById(DocUserConfig docUserConfig) {
		sqlMapClientTemplate.update("DocTypeConfig.update", docUserConfig);
		
	}
     
}
