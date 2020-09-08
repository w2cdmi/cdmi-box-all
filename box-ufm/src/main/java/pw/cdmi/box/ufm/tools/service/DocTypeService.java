package pw.cdmi.box.ufm.tools.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.ufm.tools.domain.DocUserConfig;

/**
 * 文件智能分类数据库访问服务类
 * 
 * @author guoz
 *
 */
public interface DocTypeService {
	List<DocUserConfig> getByPrefix(Limit limit, String prefix);

	void insertUserDoctype(DocUserConfig docUserConfig) throws Exception;

	void delUserDocTypeByOwner(long ownerId);

	void delUserDocTypeById(long id);

	DocUserConfig getDocUserConfigById(Long id);

	List<DocUserConfig> getDocUserConfigsByOwnerId(long ownerId);

	long getCountByOwnerId(long ownerId);

	void updateDocUserType(DocUserConfig docUserConfig)throws Exception;
}
