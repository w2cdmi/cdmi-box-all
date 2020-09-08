package pw.cdmi.file.engine.filesystem.extend;

public class MultipartParam
{
  private int partNumber;
  private String uploadID;
  private String etag;
  private Long partCRC;
  private long partSize;

  public MultipartParam(String uploadID, int partNumber, String etag, Long partCRC,long partSize)
  {
    this.uploadID = uploadID;
    this.partNumber = partNumber;
    this.etag = etag;
    this.partCRC = partCRC;
    this.partSize = partSize;
  }

  public int getPartNumber() {
    return this.partNumber;
  }

  public String getUploadID() {
    return this.uploadID;
  }

  public void setPartNumber(int partNumber) {
    this.partNumber = partNumber;
  }

  public void setUploadID(String uploadID) {
    this.uploadID = uploadID;
  }

public String getEtag() {
	return etag;
}

public void setEtag(String etag) {
	this.etag = etag;
}

public Long getPartCRC() {
	return partCRC;
}

public void setPartCRC(Long partCRC) {
	this.partCRC = partCRC;
}

public long getPartSize() {
	return partSize;
}

public void setPartSize(long partSize) {
	this.partSize = partSize;
}
}