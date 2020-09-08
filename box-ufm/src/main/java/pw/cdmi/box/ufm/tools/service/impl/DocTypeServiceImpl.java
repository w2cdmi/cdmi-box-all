package pw.cdmi.box.ufm.tools.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.ufm.tools.dao.DocTypeDao;
import pw.cdmi.box.ufm.tools.domain.DocUserConfig;
import pw.cdmi.box.ufm.tools.service.DocTypeService;

/**
 * 文件智能分类数据库访问服务类
 * 
 * @author guoz
 *
 */
@Component
public class DocTypeServiceImpl implements DocTypeService {
	@Autowired
	private DocTypeDao docTypeDao;

	@Override
	public List<DocUserConfig> getByPrefix(Limit limit, String prefix) {
		return docTypeDao.getByPrefix(limit, prefix);
	}

	public static void main(String[] args) {
		String string = "ufm.DocType.document";
		System.out.println(string.substring(string.lastIndexOf(".") + 1));
	}

	@Override
	public void insertUserDoctype(DocUserConfig docUserConfig) throws Exception {
		List<DocUserConfig> docUserConfigs = getDocUserConfigsByOwnerId(docUserConfig.getUserId());
		for (DocUserConfig duc : docUserConfigs) {
			String name = duc.getName();
			String matchName = name.substring(name.lastIndexOf(".") + 1);
			if (matchName.equals(docUserConfig.getName())) {
				throw new Exception("nameRepeat");
			}
		}
		if (docUserConfig.getName().equals("文档") || docUserConfig.getName().equals("其它")
				|| docUserConfig.getName().equals("音频") || docUserConfig.getName().equals("视频")
				|| docUserConfig.getName().equals("图片")) {
			throw new Exception("nameRepeat");
		}
		docTypeDao.insertUserDoctype(docUserConfig);
	}

	@Override
	public void delUserDocTypeByOwner(long ownerId) {
		docTypeDao.delUserDocTypeByOwner(ownerId);
	}

	@Override
	public void delUserDocTypeById(long id) {
		docTypeDao.delUserDocTypeById(id);
	}

	@Override
	public DocUserConfig getDocUserConfigById(Long id) {
		return docTypeDao.getDocUserConfigById(id);
	}

	@Override
	public List<DocUserConfig> getDocUserConfigsByOwnerId(long ownerId) {
		return docTypeDao.getDocUserConfigsByOwnerId(ownerId);
	}

	@Override
	public long getCountByOwnerId(long ownerId) {
		return docTypeDao.getCountByOwnerId(ownerId);
	}

	@Override
	public void updateDocUserType(DocUserConfig docUserConfig) throws Exception {
		List<DocUserConfig> docUserConfigs = getDocUserConfigsByOwnerId(docUserConfig.getUserId());
		DocUserConfig docConfig = getDocUserConfigById(docUserConfig.getId());
		if (!docConfig.getName().equals(docUserConfig.getName())) {
			for (DocUserConfig duc : docUserConfigs) {
				String name = duc.getName();
				String matchName = name.substring(name.lastIndexOf(".") + 1);
				if (matchName.equals(docUserConfig.getName())) {
					throw new Exception("nameRepeat");
				}
			}
			if (docUserConfig.getName().equals("文档") || docUserConfig.getName().equals("其它")
					|| docUserConfig.getName().equals("音频") || docUserConfig.getName().equals("视频")
					|| docUserConfig.getName().equals("图片")) {
				throw new Exception("nameRepeat");
			}
		}
		docTypeDao.updateDocTypeById(docUserConfig);

	}
}
