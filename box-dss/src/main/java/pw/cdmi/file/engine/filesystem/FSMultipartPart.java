package pw.cdmi.file.engine.filesystem;

import pw.cdmi.common.log.LogFormat;
import pw.cdmi.file.engine.filesystem.model.FSObject;

public class FSMultipartPart<T extends FSObject> implements Comparable<FSMultipartPart<T>>, LogFormat {
	private T fsObject;
	private final int partNumber;	//partNumber
    private String eTag;			//Part的ETag值
    private long partSize;			//分片大小
    private Long partCRC;			//分片的CRC值

	public FSMultipartPart(T fsObject, int partNumber) {
		this.fsObject = fsObject;
		this.partNumber = partNumber;
	}
	
	public FSMultipartPart(T fsObject, int partNumber, String eTag, long partSize) {
		this.fsObject = fsObject;
		this.partNumber = partNumber;
		this.eTag = eTag;
		this.partSize = partSize;
	}
	
	public FSMultipartPart(T fsObject, int partNumber, String eTag, long partSize, Long partCRC) {
		this.fsObject = fsObject;
		this.partNumber = partNumber;
		this.eTag = eTag;
		this.partSize = partSize;
		this.partCRC = partCRC;
	}
	
	public int getPartNumber() {
		return this.partNumber;
	}

	public T getFSObject() {
		return this.fsObject;
	}

	public String getETag() {
		return this.eTag;
	}
	
	public long getPartSize() {
		return this.partSize;
	}
	
	public Long getPartCRC() {
		return this.partCRC;
	}
	
	public int compareTo(FSMultipartPart<T> o) {
		return getPartNumber() - o.getPartNumber();
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + this.partNumber;
		return result;
	}

	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if ((obj instanceof FSMultipartPart)) {
			if (this == obj) {
				return true;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			FSMultipartPart other = (FSMultipartPart) obj;
			if (this.partNumber != other.partNumber) {
				return false;
			}
			return true;
		}
		return false;
	}

	public String logFormat() {
		StringBuilder sb = new StringBuilder(FSMultipartPart.class.getCanonicalName()).append("[").append("fsObject=")
				.append(this.fsObject == null ? "null" : this.fsObject.logFormat()).append(", ").append("]");

		return sb.toString();
	}
}