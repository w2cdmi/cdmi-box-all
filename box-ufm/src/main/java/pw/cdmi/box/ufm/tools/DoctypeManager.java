package pw.cdmi.box.ufm.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.sharedrive.app.system.service.SystemConfigService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.ufm.tools.Doctype.Doctypes;
import pw.cdmi.box.ufm.tools.domain.DocUserConfig;
import pw.cdmi.box.ufm.tools.service.DocTypeService;

@Component
public class DoctypeManager {

	// public static final String jsonFile = "cdmi/Doctypes.json";
	//
	// /**
	// * 得到doctypes.json内容并转为jsonNode对象
	// * @return
	// * @throws Exception
	// */
	// public static JsonNode getdoctype() throws Exception {
	// PropertiesConfiguration pc = new PropertiesConfiguration(jsonFile);
	// JsonNode jsonNode = new ObjectMapper().readTree(pc.getURL());
	// return jsonNode;
	// }

	/*
	 * 采用数据库配置 对应system_config表中数据， 具体说明参见doctype类
	 */
	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private DocTypeService docTypeService;

	public static final String preffix = "ufm.doctype.";

	public static final String comma = ".";

	public JsonNode getDoctype() throws Exception {
		// 参见：doctype类
		List<DocUserConfig> list = docTypeService.getByPrefix(new Limit(), preffix);
		Map<String, Map<String, Object>> map = new HashMap<>();
		for (DocUserConfig duc : list) {
			long doctype = duc.getId();
			String name = duc.getName();
			// String subStr = id.substring(0,id.lastIndexOf("."));
			String doctypeName = name.substring(name.lastIndexOf(".")+1);
			// String doctype = subStr.substring(subStr.lastIndexOf(".")+1);

			Map<String, Object> tmp = new HashMap<>();
			tmp.put(Doctypes.doctype.name(), doctype);
			tmp.put(Doctypes.extensions.name(), duc.getValue());
			
			map.put(doctypeName, tmp);
		}

		JsonNode jsonNode = new ObjectMapper().valueToTree(map);
		return jsonNode;
	}

	// public void saveOrUpdate(Map <String , Map <String , Object>> map)
	// {
	// List <SystemConfig> list = systemConfigService.getByPrefix(new Limit() ,
	// preffix);
	// loop :
	// for (String key : map.keySet())
	// {
	// for (SystemConfig sc : list)
	// {
	// if (sc.getId().contains(key))
	// {
	// sc.setValue(map.get(key).get(Doctypes.extensions.name()).toString());
	// systemConfigService.update(sc);
	// continue loop;
	// }
	// }
	// SystemConfig newSc = new SystemConfig();
	// newSc.setId(preffix + key + comma +
	// map.get(key).get(Doctypes.doctype.name()).toString());
	// newSc.setValue(map.get(key).get(Doctypes.extensions.name()).toString());
	// newSc.setAppId(Constants.UFM_DEFAULT_APP_ID);
	// systemConfigService.create(newSc);
	// }
	// }

	/**
	 * 
	 * 判断文件名后缀是否存在 后缀为doctypes.json的extensions中内容
	 *
	 * <参数类型> @param suffixs 为全小写文件名后缀 <参数类型> @return 存在返回other外的值，不存在返回other的值
	 * <参数类型> @throws Exception
	 *
	 * @return List<String>
	 */
	public int contains(String suffixs) throws Exception {
		Map<String, Map<String, Object>> map = getDoctypeAll();
		for (Object o : Doctypes.values()) {
			Map<String, Object> result = map.get(o.toString());
			if (null != result && null != suffixs) {
				if (Arrays.asList(((String) result.get(Doctypes.extensions.name())).split(","))
						.contains(suffixs.toLowerCase())) {
					return Integer.valueOf(result.get(Doctypes.doctype.name()).toString());
				}
			}
		}
		return Doctypes.other.getValue();
	}

	/**
	 * 
	 * 得到doctypes.json json串转为java中map对象
	 *
	 * <参数类型> @param Doctypes.json中doctype的值 <参数类型> @return <参数类型> @throws
	 * Exception
	 *
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getDoctypeAll() throws Exception {
		// 将json转为map对象
		Map<String, Map<String, Object>> map = new HashMap<>();
		for (Entry<String, JsonNode> entry : (List<Entry<String, JsonNode>>) IteratorUtils
				.toList(getDoctype().fields())) {
			map.put(entry.getKey(), new ObjectMapper().readValue(entry.getValue().traverse(), Map.class));
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getdoctypeByJsonNode(JsonNode jsonNode) throws Exception {
		// 将json转为map对象
		Map<String, Map<String, Object>> map = new HashMap<>();
		for (Entry<String, JsonNode> entry : (List<Entry<String, JsonNode>>) IteratorUtils.toList(jsonNode.fields())) {
			map.put(entry.getKey(), new ObjectMapper().readValue(entry.getValue().traverse(), Map.class));
		}
		return map;
	}

	/**
	 * 
	 * 得到doctypes.json 中extensions内容
	 *
	 * <参数类型> @param Doctypes.json中doctype的值 <参数类型> @return <参数类型> @throws
	 * Exception
	 *
	 * @return String
	 */
	public String getDoctypeValue(int doctype) throws Exception {

		Map<String, Map<String, Object>> map = getDoctypeAll();
		String extensions = null;
		if (doctype == Doctypes.picture.getValue()) {
			extensions = (String) map.get(Doctypes.picture.name()).get(Doctypes.extensions.name());
		} else if (doctype == Doctypes.document.getValue()) {
			extensions = (String) map.get(Doctypes.document.name()).get(Doctypes.extensions.name());
		} else if (doctype == Doctypes.video.getValue()) {
			extensions = (String) map.get(Doctypes.video.name()).get(Doctypes.extensions.name());
		} else if (doctype == Doctypes.audio.getValue()) {
			extensions = (String) map.get(Doctypes.audio.name()).get(Doctypes.extensions.name());
		}
		return extensions;
	}

	/**
	 * 
	 * Doctypes.json doctype对应的extensions内容转换为 jpg,jpeg,gif,psd ->
	 * \.jpg|\.jpeg|\.gif|\.psd$
	 *
	 * <参数类型> @param doctype 类型 <参数类型> @return <参数类型> @throws Exception
	 *
	 * @return String
	 */
	public String getRegdoctypeValue(int doctype) throws Exception {
		String extensions = getDoctypeValue(doctype);
		// jpg,jpeg,gif,psd -> \.jpg|\.jpeg|\.gif|\.psd$
		extensions = "\\." + extensions.replaceAll(",", "|\\\\.") + "$";
		return extensions;
	}

	/**
	 * 
	 * 返回doctype对应extensions值的集合 eg: string : jpg,jpeg,gif,psd -> array[]:
	 * [jpg,jpeg,gif,psd] <参数类型> @param doctype类型 <参数类型> @return <参数类型> @throws
	 * Exception
	 *
	 * @return List<String>
	 */
	public List<String> getSplitdoctypeValue(int doctype) throws Exception {
		String extensions = getDoctypeValue(doctype);
		List<String> resultLst = Arrays.asList(extensions.split(","));
		return resultLst;
	}

	/**
	 * 得到所有的类型的正则 eg: "audio" : {"doctype":3,"extensions" :
	 * "mp3,wma,wav,ra,cd,md"}, "video" : {"doctype":4,"extensions" :
	 * "mp4,rmvb,rm,avi"} ->
	 * \.mp3|\.wma|\.wav|\.ra|\.cd|\.md|\.mp4|\.rmvb|\.rm|\.avi$
	 * 
	 * @throws Exception
	 * 
	 */
	public String regValesAll() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		for (Integer doctype : values()) {
			String tmp = getRegdoctypeValue(doctype);
			sb.append(tmp.substring(0, tmp.length() - 1));
		}
		sb.append("$");
		return sb.substring(1).toString();
	}

	/**
	 * 
	 * 得到所有的有效值。
	 * 
	 * <参数类型> @return
	 * 
	 * @return List<Integer>
	 */
	public List<Integer> values() {
		List<Integer> lst = new ArrayList<>(5);
		for (Doctypes dt : Doctypes.values()) {
			if (0 < dt.getValue()) {
				lst.add(dt.getValue());
			}
		}
		return lst;
	}

	public String getDoctypeValueByid(long id) {
		DocUserConfig docUserConfig = docTypeService.getDocUserConfigById(id);
		String value = null;
		if (docUserConfig != null) {
			value = docUserConfig.getValue();
		}
		return value;
	}
}
