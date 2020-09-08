package pw.cdmi.file.engine.filesystem;

import pw.cdmi.file.engine.filesystem.model.FSObject;

public class FSMultipartObject extends FSObject {
	private static final long serialVersionUID = 1L;
	private String mObjUploadID;
	private long partSize;
	private String path;

	public FSMultipartObject(FSObject fsObject, String uploadID) {
		super(fsObject.getFSEndpoint());
		setObjectKey(fsObject.getObjectKey());
		setLength(fsObject.getLength());
		setPath(fsObject.getPath());
		this.mObjUploadID = uploadID;
	}

	public String getMObjUploadID() {
		return this.mObjUploadID;
	}

	public long getPartSize() {
		return this.partSize;
	}

	public String getPath() {
		return this.path;
	}

	public String logFormat() {
		StringBuilder sb = new StringBuilder(FSMultipartObject.class.getCanonicalName()).append("[")
				.append("mObjUploadID=").append(this.mObjUploadID).append(", ").append("length=").append(getLength())
				.append(", ").append("fsDefinition=").append(this.fsDefinition).append(", ").append("fsEndpoint=")
				.append(this.fsEndpoint == null ? "null" : this.fsEndpoint.logFormat()).append(", ")
				.append("fsAccessPath=").append(this.fsAccessPath == null ? "null" : this.fsAccessPath.logFormat())
				.append(", ").append("]");

		return sb.toString();
	}

	public void setMObjUploadID(String uploadID) {
		this.mObjUploadID = uploadID;
	}

	public void setPartSize(long partSize) {
		this.partSize = partSize;
	}

	public void setPath(String path) {
		this.path = path;
	}

	protected void initByPath(String path) {
	}
}