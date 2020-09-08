package pw.cdmi.box.app.convertservice.dao;

import pw.cdmi.box.app.convertservice.domain.ImgObject;


public interface ImageDAO {
    void addImage(ImgObject o);

    ImgObject getImage(String sourceObjectId);

    int checkImageObjectId(String objectId);

    void deleteImageObject(String objectId);
}
