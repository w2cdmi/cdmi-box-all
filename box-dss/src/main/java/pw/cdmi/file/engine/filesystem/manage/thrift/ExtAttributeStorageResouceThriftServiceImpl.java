package pw.cdmi.file.engine.filesystem.manage.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.thrift.filesystem.StorageExtAttribute;
import com.huawei.sharedrive.thrift.filesystem.StorageExtAttributeThriftServiceOnDss;

import pw.cdmi.core.exception.CustomException;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.thrift.exception.ThriftException;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.ExtAttribute;
import pw.cdmi.file.engine.filesystem.exception.InvalidParamException;
import pw.cdmi.file.engine.filesystem.manage.FSEndpointManager;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class ExtAttributeStorageResouceThriftServiceImpl implements StorageExtAttributeThriftServiceOnDss.Iface {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtAttributeStorageResouceThriftServiceImpl.class);

	@Autowired
	private FSEndpointManager fsEndpointManager;

	@MethodLogAble(value = Level.INFO, newLogId = true, isPrintResult = false)
	public List<StorageExtAttribute> listAttributes(List<String> ids) throws TException {
		try {
			List<StorageExtAttribute> storageList = new ArrayList<StorageExtAttribute>(ids.size());
			FSEndpoint endpoint = null;
			StorageExtAttribute storageInfo = null;
			for (String id : ids) {
				endpoint = this.fsEndpointManager.getFSEndpoint(id);
				if (endpoint != null) {
					if (endpoint.getBestStartRange() == null) {
						endpoint.setBestStartRange(ExtAttributeConstant.BEST_START_RANGE_DEFAULT);
					}
					if (endpoint.getBestEndRange() == null) {
						endpoint.setBestEndRange(ExtAttributeConstant.BEST_END_RANGE_DEFAULT);
					}
					if (endpoint.getMultipartFirst() == null) {
						endpoint.setMultipartFirst(ExtAttributeConstant.MULTI_PART_FIRST_DEFAULT);
					}
					storageInfo = createExtAttributeStorageInfo(endpoint);
					storageList.add(storageInfo);
				}
			}
			return storageList;
		} catch (Exception e) {
			LOGGER.error("list attributes failed.", e);
			throw new ThriftException(e);
		}
	}

	@MethodLogAble(value = Level.INFO, newLogId = true, isPrintArgs = false)
	public void changeAttribute(StorageExtAttribute storageInfo) throws TException {
		try {
			LOGGER.info("change attribute : " + storageInfo.getId());
			FSEndpoint endpoint = creatExtAttributeEndpoint(storageInfo);
			this.fsEndpointManager.updateFSEndpointExtAttribute(endpoint);
		} catch (CustomException e) {
			LOGGER.info("change attribute failed [ " + storageInfo.getId() + " ]", e);
			throw new ThriftException(e);
		}
	}

	private StorageExtAttribute createExtAttributeStorageInfo(FSEndpoint endpoint) {
		StorageExtAttribute sea = new StorageExtAttribute();
		sea.setId(endpoint.getId());
		ExtAttribute bestStartRange = new ExtAttribute("bestStartRange", String.valueOf(endpoint.getBestStartRange()));
		ExtAttribute bestEndRange = new ExtAttribute("bestEndRange", String.valueOf(endpoint.getBestEndRange()));
		ExtAttribute multipartFirst = new ExtAttribute("multipartFirst", String.valueOf(endpoint.getMultipartFirst()));
		ExtAttribute[] extAttributeresource = { bestStartRange, bestEndRange, multipartFirst };
		sea.setAttribute(JsonUtils.toJson(extAttributeresource));
		return sea;
	}

	FSEndpoint creatExtAttributeEndpoint(StorageExtAttribute extAttributestorageInfo) throws InvalidParamException {
		FSEndpoint endpoint = new FSEndpoint();
		endpoint.setId(extAttributestorageInfo.getId());

		List temp = JsonUtils.stringToList(extAttributestorageInfo.getAttribute(), List.class,
				new Class[] { ExtAttribute.class });

		checkAndsetParameter(temp, endpoint);
		return endpoint;
	}

	private void checkAndsetParameter(List<ExtAttribute> temp, FSEndpoint endpoint) throws InvalidParamException {
		long startRange = 0L;
		long endRange = 0L;
		String multiFirst = null;
		for (ExtAttribute attr : temp) {
			switch (attr.getKey()) {
			case ExtAttributeConstant.BEST_START_RANGE:
				startRange = parseLong(attr.getValue());
				if (startRange < 0) {
					String message = "the param bestStartRange:" + startRange + " is invalid";
					throw new InvalidParamException(message);
				}
				endpoint.setBestStartRange(Long.valueOf(startRange));
		        break;
			case ExtAttributeConstant.BEST_END_RANGE:
				endRange = parseLong(attr.getValue());
				if (endRange < 0 && endRange != ExtAttributeConstant.BEST_END_RANGE_DEFAULT){
					String message = "the param bestEndRange: " + endRange + " is invalid";
		            throw new InvalidParamException(message);
				}
				endpoint.setBestEndRange(Long.valueOf(endRange));
				break;
			case ExtAttributeConstant.MULTI_PART_FIRST:
				multiFirst = attr.getValue();
				if (!multiFirst.equals(ExtAttributeConstant.JUDGE_TRUE) && !multiFirst.equals(ExtAttributeConstant.JUDGE_FALSE)){
					String message = "the param multipartFirst: " + attr.getValue() + " is invalid";
		            throw new InvalidParamException(message);
				}
				endpoint.setMultipartFirst(Boolean.parseBoolean(multiFirst));
				break;
			default:
				LOGGER.error("Unsupport parameter");
				break;
			}

		}

		if ((endRange != ExtAttributeConstant.BEST_END_RANGE_DEFAULT.longValue()) && (startRange >= endRange)) {
			String message = "bestInterval is error, bestStartRange:" + startRange + " >= " + endRange
					+ " bestEndRange";
			throw new InvalidParamException(message);
		}
	}

	private long parseLong(String input) throws InvalidParamException {
		long result = 0L;
		try {
			result = Long.parseLong(input);
		} catch (NumberFormatException e) {
			throw new InvalidParamException("the param is illegal", e);
		}
		return result;
	}
}