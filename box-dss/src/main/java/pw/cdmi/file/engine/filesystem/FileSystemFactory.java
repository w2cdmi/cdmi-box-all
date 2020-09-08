package pw.cdmi.file.engine.filesystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pw.cdmi.file.engine.core.spring.ext.BeanHolder;
import pw.cdmi.file.engine.filesystem.exception.UnknownFSException;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;

public final class FileSystemFactory {
	private static final Map<String, FileSystem> FILESYSTEMS = new ConcurrentHashMap<String, FileSystem>(
			1);

	public static FileSystem<FSObject> getInstance(FSDefinition fsDefinition) throws UnknownFSException {
		FileSystem<FSObject> fileSystem = FILESYSTEMS.get(fsDefinition.getName());
		if (fileSystem == null) {
			Map<String, FileSystem> temp = BeanHolder.getBeans(FileSystem.class);
			if (temp == null) {
				throw new UnknownFSException("Can not aquire file system information");
			}
			for (Map.Entry<String, FileSystem> entry : temp.entrySet()) {
				FILESYSTEMS.put((entry.getValue()).getFSManager().getDefinition().getName(), entry.getValue());
			}

			fileSystem = FILESYSTEMS.get(fsDefinition.getName());
		}
		return fileSystem;
	}

	public static FileSystem<FSObject> getInstance(FSEndpoint fsEndpoint) throws UnknownFSException {
		return getInstance(FSDefinition.findFSDefinition(fsEndpoint.getFsType()));
	}

	public static FileSystem<FSObject> getInstance(String path) throws UnknownFSException {
		FSDefinition definition = FSDefinition.parseByPath(path);
		if (definition == null) {
			throw new UnknownFSException("FSDefinition is null");
		}
		return getInstance(definition);
	}
}