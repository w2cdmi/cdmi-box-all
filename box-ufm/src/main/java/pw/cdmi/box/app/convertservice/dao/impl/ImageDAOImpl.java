package pw.cdmi.box.app.convertservice.dao.impl;

import pw.cdmi.box.app.convertservice.dao.ImageDAO;
import pw.cdmi.box.app.convertservice.domain.ImgObject;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


@SuppressWarnings("deprecation")
@Service("imageDao")
public class ImageDAOImpl extends AbstractDAOImpl implements ImageDAO {
    @Override
    public void addImage(ImgObject o) {
        sqlMapClientTemplate.insert("ImgObject.addImgObject", o);
    }

    @Override
    public ImgObject getImage(String sourceObjectId) {
        return (ImgObject) sqlMapClientTemplate.queryForObject("ImgObject.getImageByObjectId",
            sourceObjectId);
    }

    public int checkImageObjectId(String objectId) {
        int count = 0;
        Object obj = sqlMapClientTemplate.queryForObject("ImgObject.checkImageObjectId", objectId);
        if(obj != null && StringUtils.isNotEmpty(obj.toString())) {
            count = Integer.valueOf(obj.toString());
        }
        return count;
    }

    public void deleteImageObject(String objectId) {
        sqlMapClientTemplate.delete("ImgObject.deleteImageObject", objectId);
    }
}
