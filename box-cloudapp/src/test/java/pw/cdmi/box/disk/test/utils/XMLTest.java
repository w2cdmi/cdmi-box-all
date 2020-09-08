package pw.cdmi.box.disk.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import pw.cdmi.box.disk.user.domain.User;

public class XMLTest {
	private static XStream xstream = null;
	private static ObjectOutputStream out = null;
	private static ObjectInputStream in = null;
	private static Object bean = null;

	public static void getXMLToObject(InputStream ins) throws IOException,
			ClassNotFoundException {
		xstream = new XStream();
		in = xstream.createObjectInputStream(ins);
		bean = in.readObject();

	}

	public void destory() {
		xstream = null;
		bean = null;
		try {
			if (out != null) {
				out.flush();
				out.close();
			}
			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public final void fail(String string) {
		System.out.println(string);
	}

	public final void failRed(String string) {
		System.err.println(string);
	}

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		File file = new File(
				"D:\\CSE_Open_Client_R2C10\\Code\\trunk\\projects\\server\\cloudapp\\src\\test\\java\\com\\huawei\\sharedrive\\cloudapp\\test\\utils\\User.txt");
		InputStream in = new FileInputStream(file);
		XStream	xstream =new XStream();
		xstream.alias("list",ArrayList.class);
		xstream.alias("user", User.class);
		List<User> u = (List<User>)xstream.fromXML(in);
		for (User user : u)
		{
			System.out.println(user.getAppId());
		}

	}

}
