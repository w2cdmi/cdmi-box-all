package pw.cdmi.file.engine.filesystem.uds;

import pw.cdmi.file.engine.filesystem.aws.S3FSObject;
import pw.cdmi.file.engine.filesystem.exception.UnrecognizedFileException;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class UDSFSObject extends S3FSObject {
	private static final long serialVersionUID = -8965131894898929748L;

	public UDSFSObject(String path) throws UnrecognizedFileException {
		super(path);
	}

	public UDSFSObject(FSEndpoint endpoint, String objectKey) {
		super(endpoint, objectKey);
	}

}