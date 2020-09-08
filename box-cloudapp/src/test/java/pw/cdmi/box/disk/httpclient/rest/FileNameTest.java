package pw.cdmi.box.disk.httpclient.rest;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;

public class FileNameTest {
	@Test
	public void fileNameTest() {
		Assert.assertNotNull(FilenameUtils.getExtension("你好.doc"));
		String ext = FilenameUtils.getExtension("你好");
		Assert.assertNotNull(ext);
	}
}
