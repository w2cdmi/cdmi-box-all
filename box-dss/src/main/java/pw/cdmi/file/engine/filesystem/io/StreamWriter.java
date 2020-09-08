package pw.cdmi.file.engine.filesystem.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public final class StreamWriter {
	public static void write(InputStream fsInputStream, OutputStream[] fsOutputStreams) throws IOException {
		write(false, fsInputStream, fsOutputStreams);
	}

	public static void write(InputStream in, OutputStream out) throws IOException {
		write(false, in, out);
	}

	public static void write(boolean autoClose, InputStream in, OutputStream out) throws IOException {
		try {
			byte[] buffer = new byte[8192];
			int len = in.read(buffer);

			while (len != -1) {
				out.write(buffer, 0, len);
				len = in.read(buffer);
			}
		} finally {
			if (autoClose) {
				in.close();
				out.close();
			}
		}
	}

	public static void write(boolean autoClose, InputStream fsInputStream, OutputStream[] fsOutputStreams)
			throws IOException {
		byte[] buffer = new byte[262144];
		int len = 0;
		int n = 0;
		int sizeLeft = 0;
		OutputStream os;
		try {
			while (fsInputStream != null) {
				len = 0;
				sizeLeft = buffer.length;
				n = fsInputStream.read(buffer, len, sizeLeft);
				while (n != -1) {
					len += n;
					sizeLeft -= n;
					if (sizeLeft == 0) {
						break;
					}
					n = fsInputStream.read(buffer, len, sizeLeft);
				}

				if (len <= 0) {
					break;
				}

				fsOutputStreams[0].write(buffer, 0, len);
			}

		} finally {
			if (autoClose) {
				for (OutputStream oss : fsOutputStreams) {
					IOUtils.closeQuietly(oss);
				}
				IOUtils.closeQuietly(fsInputStream);
				fsInputStream = null;
			}
		}
	}
}